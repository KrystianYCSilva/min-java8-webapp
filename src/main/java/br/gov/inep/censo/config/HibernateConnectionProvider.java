package br.gov.inep.censo.config;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Fabrica central de EntityManagerFactory/EntityManager com provider Hibernate.
 *
 * Mantem compatibilidade com acesso JDBC bruto quando necessario, mas prioriza
 * o uso de EntityManager/EntityTransaction na camada de repositorios.
 */
public final class HibernateConnectionProvider {

    private static final Object LOCK = new Object();
    private static final String PERSISTENCE_UNIT_NAME = "censo-superior-pu";

    private static volatile EntityManagerFactory entityManagerFactory;
    private static volatile String activeUrl;
    private static volatile String activeUser;
    private static volatile String activePassword;

    private HibernateConnectionProvider() {
    }

    public static EntityManager openEntityManager() throws SQLException {
        try {
            return ensureEntityManagerFactory().createEntityManager();
        } catch (RuntimeException e) {
            throw new SQLException("Falha ao abrir EntityManager JPA.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        final EntityManager entityManager = openEntityManager();

        final Session session;
        try {
            Object delegate = entityManager.getDelegate();
            if (delegate instanceof Session) {
                session = (Session) delegate;
            } else {
                session = (Session) entityManager.unwrap(Session.class);
            }
        } catch (RuntimeException e) {
            closeEntityManagerQuietly(entityManager);
            throw new SQLException("Falha ao obter sessao nativa a partir do EntityManager.", e);
        }

        final Connection rawConnection;
        try {
            rawConnection = (Connection) session.doReturningWork(new ReturningWork<Connection>() {
                public Connection execute(Connection connection) {
                    return connection;
                }
            });
        } catch (RuntimeException e) {
            closeEntityManagerQuietly(entityManager);
            throw new SQLException("Falha ao obter conexao JDBC via EntityManager.", e);
        }

        return createEntityManagerBoundConnection(rawConnection, entityManager);
    }

    public static void invalidate() {
        synchronized (LOCK) {
            closeEntityManagerFactoryQuietly(entityManagerFactory);
            entityManagerFactory = null;
            activeUrl = null;
            activeUser = null;
            activePassword = null;
        }
    }

    public static void shutdown() {
        invalidate();
    }

    private static EntityManagerFactory ensureEntityManagerFactory() {
        String url = ConnectionFactory.getJdbcUrl();
        String user = ConnectionFactory.getJdbcUser();
        String password = ConnectionFactory.getJdbcPassword();

        if (isCurrentConfig(url, user, password) && entityManagerFactory != null) {
            return entityManagerFactory;
        }

        synchronized (LOCK) {
            if (isCurrentConfig(url, user, password) && entityManagerFactory != null) {
                return entityManagerFactory;
            }
            closeEntityManagerFactoryQuietly(entityManagerFactory);
            entityManagerFactory = buildEntityManagerFactory(url, user, password);
            activeUrl = url;
            activeUser = user;
            activePassword = password;
            return entityManagerFactory;
        }
    }

    private static boolean isCurrentConfig(String url, String user, String password) {
        return equalsNullable(activeUrl, url)
                && equalsNullable(activeUser, user)
                && equalsNullable(activePassword, password);
    }

    private static boolean equalsNullable(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    private static EntityManagerFactory buildEntityManagerFactory(String url, String user, String password) {
        try {
            String jdbcDriver = resolveDriver(url);
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("javax.persistence.jdbc.driver", jdbcDriver);
            properties.put("javax.persistence.jdbc.url", url);
            properties.put("javax.persistence.jdbc.user", user == null ? "" : user);
            properties.put("javax.persistence.jdbc.password", password == null ? "" : password);
            properties.put("hibernate.connection.driver_class", jdbcDriver);
            properties.put("hibernate.connection.url", url);
            properties.put("hibernate.connection.username", user == null ? "" : user);
            properties.put("hibernate.connection.password", password == null ? "" : password);
            properties.put("hibernate.connection.pool_size", "10");
            properties.put("hibernate.dialect", resolveDialect(url));
            properties.put("hibernate.show_sql", "false");
            properties.put("hibernate.format_sql", "false");
            properties.put("hibernate.use_sql_comments", "false");
            properties.put("hibernate.jdbc.batch_size", "25");
            properties.put("hibernate.order_inserts", "true");
            properties.put("hibernate.order_updates", "true");
            return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
        } catch (HibernateException e) {
            throw new IllegalStateException("Falha ao criar EntityManagerFactory JPA.", e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Falha ao criar EntityManagerFactory JPA.", e);
        }
    }

    private static String resolveDriver(String url) {
        if (url != null && url.startsWith("jdbc:postgresql:")) {
            return "org.postgresql.Driver";
        }
        if (url != null && url.startsWith("jdbc:mysql:")) {
            return "com.mysql.jdbc.Driver";
        }
        if (url != null && url.startsWith("jdbc:db2:")) {
            return "com.ibm.db2.jcc.DB2Driver";
        }
        return "org.h2.Driver";
    }

    private static String resolveDialect(String url) {
        if (url != null && url.startsWith("jdbc:postgresql:")) {
            return "org.hibernate.dialect.PostgreSQLDialect";
        }
        if (url != null && url.startsWith("jdbc:mysql:")) {
            return "org.hibernate.dialect.MySQL5InnoDBDialect";
        }
        if (url != null && url.startsWith("jdbc:db2:")) {
            return "org.hibernate.dialect.DB2Dialect";
        }
        return "org.hibernate.dialect.H2Dialect";
    }

    private static Connection createEntityManagerBoundConnection(final Connection delegate,
                                                                 final EntityManager entityManager) {
        InvocationHandler handler = new InvocationHandler() {
            private boolean closed;

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();

                if ("close".equals(methodName)) {
                    closeProxyConnection();
                    return null;
                }
                if ("isClosed".equals(methodName)) {
                    if (closed) {
                        return Boolean.TRUE;
                    }
                    return Boolean.valueOf(delegate.isClosed());
                }
                if (closed) {
                    throw new SQLException("Conexao encerrada.");
                }

                try {
                    return method.invoke(delegate, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }

            private void closeProxyConnection() {
                if (closed) {
                    return;
                }
                closed = true;
                closeEntityManagerQuietly(entityManager);
            }
        };

        return (Connection) Proxy.newProxyInstance(
                HibernateConnectionProvider.class.getClassLoader(),
                new Class[]{Connection.class},
                handler);
    }

    private static void closeEntityManagerFactoryQuietly(EntityManagerFactory factory) {
        if (factory != null) {
            try {
                factory.close();
            } catch (RuntimeException ignored) {
                // noop
            }
        }
    }

    private static void closeEntityManagerQuietly(EntityManager entityManager) {
        if (entityManager != null) {
            try {
                entityManager.close();
            } catch (RuntimeException ignored) {
                // noop
            }
        }
    }
}
