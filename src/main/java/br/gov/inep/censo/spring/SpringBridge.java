package br.gov.inep.censo.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.SQLException;

/**
 * Utilitarios para acesso ao contexto Spring e execucao transacional programatica.
 */
public final class SpringBridge {

    private static final String CLASSPATH_CONTEXT = "spring/applicationContext.xml";
    private static volatile ApplicationContext fallbackContext;

    private SpringBridge() {
    }

    public interface SqlWork<T> {
        T execute(EntityManager entityManager) throws SQLException;
    }

    public static <T> T getBean(Class<T> type) {
        try {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            if (context != null) {
                return context.getBean(type);
            }
            ApplicationContext classpathContext = getFallbackContext();
            return classpathContext != null ? classpathContext.getBean(type) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static ApplicationContext getFallbackContext() {
        ApplicationContext local = fallbackContext;
        if (local != null) {
            return local;
        }
        synchronized (SpringBridge.class) {
            if (fallbackContext == null) {
                fallbackContext = new ClassPathXmlApplicationContext(CLASSPATH_CONTEXT);
            }
            return fallbackContext;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T inTransaction(PlatformTransactionManager transactionManager,
                                      final EntityManagerFactory entityManagerFactory,
                                      final SqlWork<T> work,
                                      String errorMessage) throws SQLException {
        if (transactionManager == null || entityManagerFactory == null) {
            throw new SQLException("Infraestrutura transacional Spring indisponivel.");
        }

        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        try {
            Object result = template.execute(new TransactionCallback<Object>() {
                public Object doInTransaction(TransactionStatus status) {
                    EntityManager entityManager =
                            EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
                    if (entityManager == null) {
                        throw new IllegalStateException("EntityManager transacional indisponivel.");
                    }
                    try {
                        return work.execute(entityManager);
                    } catch (SQLException e) {
                        throw new SqlRuntimeException(e);
                    }
                }
            });
            return (T) result;
        } catch (SqlRuntimeException e) {
            throw e.getSqlException();
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                throw (SQLException) cause;
            }
            throw new SQLException(errorMessage, e);
        }
    }

    private static final class SqlRuntimeException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final SQLException sqlException;

        private SqlRuntimeException(SQLException sqlException) {
            super(sqlException);
            this.sqlException = sqlException;
        }

        private SQLException getSqlException() {
            return sqlException;
        }
    }
}
