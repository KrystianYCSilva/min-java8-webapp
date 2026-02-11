package br.gov.inep.censo.repository;

import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.spring.SpringBridge;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio custom para metadados de leiaute e valores complementares.
 */
public class LayoutCampoValueRepository {

    private final EntityManagerFactory entityManagerFactory;

    private interface EntityManagerWork<T> {
        T execute(EntityManager entityManager) throws SQLException;
    }

    public LayoutCampoValueRepository() {
        this(SpringBridge.getBean(EntityManagerFactory.class));
    }

    public LayoutCampoValueRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public List<LayoutCampo> listarPorModulo(final String modulo) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<List<LayoutCampo>>() {
            public List<LayoutCampo> execute(EntityManager entityManager) {
                TypedQuery<LayoutCampo> query = entityManager.createQuery(
                        "select l from LayoutCampo l where l.modulo = :modulo order by l.numeroCampo",
                        LayoutCampo.class);
                query.setParameter("modulo", modulo);
                return query.getResultList();
            }
        });
    }

    public void salvarValoresAluno(EntityManager entityManager, Long alunoId, Map<Long, String> valores) {
        salvarValores(entityManager, "aluno_layout_valor", "aluno_id", alunoId, valores);
    }

    public void salvarValoresCurso(EntityManager entityManager, Long cursoId, Map<Long, String> valores) {
        salvarValores(entityManager, "curso_layout_valor", "curso_id", cursoId, valores);
    }

    public void salvarValoresCursoAluno(EntityManager entityManager, Long cursoAlunoId, Map<Long, String> valores) {
        salvarValores(entityManager, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId, valores);
    }

    public void salvarValoresDocente(EntityManager entityManager, Long docenteId, Map<Long, String> valores) {
        salvarValores(entityManager, "docente_layout_valor", "docente_id", docenteId, valores);
    }

    public void salvarValoresIes(EntityManager entityManager, Long iesId, Map<Long, String> valores) {
        salvarValores(entityManager, "ies_layout_valor", "ies_id", iesId, valores);
    }

    public void substituirValoresAluno(EntityManager entityManager, Long alunoId, Map<Long, String> valores) {
        removerValores(entityManager, "aluno_layout_valor", "aluno_id", alunoId);
        salvarValoresAluno(entityManager, alunoId, valores);
    }

    public void substituirValoresCurso(EntityManager entityManager, Long cursoId, Map<Long, String> valores) {
        removerValores(entityManager, "curso_layout_valor", "curso_id", cursoId);
        salvarValoresCurso(entityManager, cursoId, valores);
    }

    public void substituirValoresCursoAluno(EntityManager entityManager, Long cursoAlunoId, Map<Long, String> valores) {
        removerValores(entityManager, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
        salvarValoresCursoAluno(entityManager, cursoAlunoId, valores);
    }

    public void substituirValoresDocente(EntityManager entityManager, Long docenteId, Map<Long, String> valores) {
        removerValores(entityManager, "docente_layout_valor", "docente_id", docenteId);
        salvarValoresDocente(entityManager, docenteId, valores);
    }

    public void substituirValoresIes(EntityManager entityManager, Long iesId, Map<Long, String> valores) {
        removerValores(entityManager, "ies_layout_valor", "ies_id", iesId);
        salvarValoresIes(entityManager, iesId, valores);
    }

    public void removerValoresAluno(EntityManager entityManager, Long alunoId) {
        removerValores(entityManager, "aluno_layout_valor", "aluno_id", alunoId);
    }

    public void removerValoresCurso(EntityManager entityManager, Long cursoId) {
        removerValores(entityManager, "curso_layout_valor", "curso_id", cursoId);
    }

    public void removerValoresCursoAluno(EntityManager entityManager, Long cursoAlunoId) {
        removerValores(entityManager, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
    }

    public void removerValoresDocente(EntityManager entityManager, Long docenteId) {
        removerValores(entityManager, "docente_layout_valor", "docente_id", docenteId);
    }

    public void removerValoresIes(EntityManager entityManager, Long iesId) {
        removerValores(entityManager, "ies_layout_valor", "ies_id", iesId);
    }

    public Map<Long, String> carregarValoresAlunoPorCampoId(final Long alunoId) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Map<Long, String>>() {
            public Map<Long, String> execute(EntityManager entityManager) {
                return carregarValoresPorCampoId(entityManager, "aluno_layout_valor", "aluno_id", alunoId);
            }
        });
    }

    public Map<Long, String> carregarValoresCursoPorCampoId(final Long cursoId) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Map<Long, String>>() {
            public Map<Long, String> execute(EntityManager entityManager) {
                return carregarValoresPorCampoId(entityManager, "curso_layout_valor", "curso_id", cursoId);
            }
        });
    }

    public Map<Long, String> carregarValoresCursoAlunoPorCampoId(final Long cursoAlunoId) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Map<Long, String>>() {
            public Map<Long, String> execute(EntityManager entityManager) {
                return carregarValoresPorCampoId(entityManager, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
            }
        });
    }

    public Map<Long, String> carregarValoresDocentePorCampoId(final Long docenteId) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Map<Long, String>>() {
            public Map<Long, String> execute(EntityManager entityManager) {
                return carregarValoresPorCampoId(entityManager, "docente_layout_valor", "docente_id", docenteId);
            }
        });
    }

    public Map<Long, String> carregarValoresIesPorCampoId(final Long iesId) throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Map<Long, String>>() {
            public Map<Long, String> execute(EntityManager entityManager) {
                return carregarValoresPorCampoId(entityManager, "ies_layout_valor", "ies_id", iesId);
            }
        });
    }

    public Map<Integer, String> carregarValoresAlunoPorNumero(final Long alunoId, final String modulo)
            throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Map<Integer, String>>() {
            public Map<Integer, String> execute(EntityManager entityManager) {
                return carregarValoresPorNumero(entityManager, "aluno_layout_valor", "aluno_id", alunoId, modulo);
            }
        });
    }

    public Map<Integer, String> carregarValoresCursoPorNumero(final Long cursoId, final String modulo)
            throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Map<Integer, String>>() {
            public Map<Integer, String> execute(EntityManager entityManager) {
                return carregarValoresPorNumero(entityManager, "curso_layout_valor", "curso_id", cursoId, modulo);
            }
        });
    }

    public Map<Integer, String> carregarValoresCursoAlunoPorNumero(final Long cursoAlunoId, final String modulo)
            throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Map<Integer, String>>() {
            public Map<Integer, String> execute(EntityManager entityManager) {
                return carregarValoresPorNumero(
                        entityManager, "curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId, modulo);
            }
        });
    }

    public Map<Integer, String> carregarValoresDocentePorNumero(final Long docenteId, final String modulo)
            throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Map<Integer, String>>() {
            public Map<Integer, String> execute(EntityManager entityManager) {
                return carregarValoresPorNumero(entityManager, "docente_layout_valor", "docente_id", docenteId, modulo);
            }
        });
    }

    public Map<Integer, String> carregarValoresIesPorNumero(final Long iesId, final String modulo)
            throws SQLException {
        return executeInEntityManager(new EntityManagerWork<Map<Integer, String>>() {
            public Map<Integer, String> execute(EntityManager entityManager) {
                return carregarValoresPorNumero(entityManager, "ies_layout_valor", "ies_id", iesId, modulo);
            }
        });
    }

    public Map<Integer, Long> mapaCampoIdPorNumero(String modulo) throws SQLException {
        List<LayoutCampo> campos = listarPorModulo(modulo);
        Map<Integer, Long> mapa = new LinkedHashMap<Integer, Long>();
        for (int i = 0; i < campos.size(); i++) {
            LayoutCampo campo = campos.get(i);
            mapa.put(campo.getNumeroCampo(), campo.getId());
        }
        return mapa;
    }

    private void salvarValores(EntityManager entityManager,
                               String tabela,
                               String colunaFk,
                               Long fkValue,
                               Map<Long, String> valores) {
        if (fkValue == null || valores == null || valores.isEmpty()) {
            return;
        }
        Query query = entityManager.createNativeQuery(
                "INSERT INTO " + tabela + " (" + colunaFk + ", layout_campo_id, valor) " +
                        "VALUES (:fkValue, :layoutCampoId, :valor)");
        for (Map.Entry<Long, String> entry : valores.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            String valor = trimToNull(entry.getValue());
            if (valor == null) {
                continue;
            }
            query.setParameter("fkValue", fkValue.longValue());
            query.setParameter("layoutCampoId", entry.getKey().longValue());
            query.setParameter("valor", valor);
            query.executeUpdate();
        }
    }

    private Map<Long, String> carregarValoresPorCampoId(EntityManager entityManager,
                                                        String tabela,
                                                        String colunaFk,
                                                        Long fkValue) {
        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (fkValue == null) {
            return valores;
        }
        Query query = entityManager.createNativeQuery(
                "SELECT layout_campo_id, valor FROM " + tabela + " WHERE " + colunaFk + " = :fkValue");
        query.setParameter("fkValue", fkValue.longValue());
        List rows = query.getResultList();
        for (int i = 0; i < rows.size(); i++) {
            Object row = rows.get(i);
            if (!(row instanceof Object[])) {
                continue;
            }
            Object[] values = (Object[]) row;
            if (values.length < 2 || values[0] == null) {
                continue;
            }
            valores.put(toLong(values[0]), values[1] == null ? null : values[1].toString());
        }
        return valores;
    }

    private Map<Integer, String> carregarValoresPorNumero(EntityManager entityManager,
                                                          String tabela,
                                                          String colunaFk,
                                                          Long fkValue,
                                                          String modulo) {
        Map<Integer, String> valores = new LinkedHashMap<Integer, String>();
        if (fkValue == null) {
            return valores;
        }
        Query query = entityManager.createNativeQuery(
                "SELECT c.numero_campo, v.valor FROM " + tabela + " v " +
                        "INNER JOIN layout_campo c ON c.id = v.layout_campo_id " +
                        "WHERE v." + colunaFk + " = :fkValue AND c.modulo = :modulo");
        query.setParameter("fkValue", fkValue.longValue());
        query.setParameter("modulo", modulo);
        List rows = query.getResultList();
        for (int i = 0; i < rows.size(); i++) {
            Object row = rows.get(i);
            if (!(row instanceof Object[])) {
                continue;
            }
            Object[] values = (Object[]) row;
            if (values.length < 2 || values[0] == null) {
                continue;
            }
            valores.put(toInteger(values[0]), values[1] == null ? null : values[1].toString());
        }
        return valores;
    }

    private void removerValores(EntityManager entityManager, String tabela, String colunaFk, Long fkValue) {
        if (fkValue == null) {
            return;
        }
        Query query = entityManager.createNativeQuery("DELETE FROM " + tabela + " WHERE " + colunaFk + " = :fkValue");
        query.setParameter("fkValue", fkValue.longValue());
        query.executeUpdate();
    }

    private Long toLong(Object value) {
        if (value instanceof Number) {
            return Long.valueOf(((Number) value).longValue());
        }
        return Long.valueOf(value.toString());
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number) {
            return Integer.valueOf(((Number) value).intValue());
        }
        return Integer.valueOf(value.toString());
    }

    private <T> T executeInEntityManager(EntityManagerWork<T> work) throws SQLException {
        if (entityManagerFactory == null) {
            throw new SQLException("EntityManagerFactory indisponivel para LayoutCampoValueRepository.");
        }
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            return work.execute(entityManager);
        } catch (SQLException e) {
            throw e;
        } catch (RuntimeException e) {
            throw toSqlException("Falha ao executar operacao de layout.", e);
        } finally {
            if (entityManager != null) {
                try {
                    entityManager.close();
                } catch (RuntimeException ignored) {
                    // noop
                }
            }
        }
    }

    private SQLException toSqlException(String message, RuntimeException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }
        return new SQLException(message, e);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() == 0 ? null : trimmed;
    }
}
