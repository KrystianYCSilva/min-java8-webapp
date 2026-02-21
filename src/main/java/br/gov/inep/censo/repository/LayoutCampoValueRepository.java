package br.gov.inep.censo.repository;

import br.gov.inep.censo.model.LayoutCampo;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio custom para metadados de leiaute e valores complementares.
 */
@Component
public class LayoutCampoValueRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<LayoutCampo> listarPorModulo(String modulo) throws SQLException {
        try {
            TypedQuery<LayoutCampo> query = entityManager.createQuery(
                    "select l from LayoutCampo l where l.modulo = :modulo order by l.numeroCampo",
                    LayoutCampo.class);
            query.setParameter("modulo", modulo);
            return query.getResultList();
        } catch (RuntimeException e) {
            throw toSqlException("Falha ao listar campos de layout.", e);
        }
    }

    public void salvarValoresAluno(Long alunoId, Map<Long, String> valores) {
        salvarValores("aluno_layout_valor", "aluno_id", alunoId, valores);
    }

    public void salvarValoresCurso(Long cursoId, Map<Long, String> valores) {
        salvarValores("curso_layout_valor", "curso_id", cursoId, valores);
    }

    public void salvarValoresCursoAluno(Long cursoAlunoId, Map<Long, String> valores) {
        salvarValores("curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId, valores);
    }

    public void salvarValoresDocente(Long docenteId, Map<Long, String> valores) {
        salvarValores("docente_layout_valor", "docente_id", docenteId, valores);
    }

    public void salvarValoresIes(Long iesId, Map<Long, String> valores) {
        salvarValores("ies_layout_valor", "ies_id", iesId, valores);
    }

    public void substituirValoresAluno(Long alunoId, Map<Long, String> valores) {
        removerValores("aluno_layout_valor", "aluno_id", alunoId);
        salvarValoresAluno(alunoId, valores);
    }

    public void substituirValoresCurso(Long cursoId, Map<Long, String> valores) {
        removerValores("curso_layout_valor", "curso_id", cursoId);
        salvarValoresCurso(cursoId, valores);
    }

    public void substituirValoresCursoAluno(Long cursoAlunoId, Map<Long, String> valores) {
        removerValores("curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
        salvarValoresCursoAluno(cursoAlunoId, valores);
    }

    public void substituirValoresDocente(Long docenteId, Map<Long, String> valores) {
        removerValores("docente_layout_valor", "docente_id", docenteId);
        salvarValoresDocente(docenteId, valores);
    }

    public void substituirValoresIes(Long iesId, Map<Long, String> valores) {
        removerValores("ies_layout_valor", "ies_id", iesId);
        salvarValoresIes(iesId, valores);
    }

    public void removerValoresAluno(Long alunoId) {
        removerValores("aluno_layout_valor", "aluno_id", alunoId);
    }

    public void removerValoresCurso(Long cursoId) {
        removerValores("curso_layout_valor", "curso_id", cursoId);
    }

    public void removerValoresCursoAluno(Long cursoAlunoId) {
        removerValores("curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
    }

    public void removerValoresDocente(Long docenteId) {
        removerValores("docente_layout_valor", "docente_id", docenteId);
    }

    public void removerValoresIes(Long iesId) {
        removerValores("ies_layout_valor", "ies_id", iesId);
    }

    public Map<Long, String> carregarValoresAlunoPorCampoId(Long alunoId) throws SQLException {
        return carregarValoresPorCampoId("aluno_layout_valor", "aluno_id", alunoId);
    }

    public Map<Long, String> carregarValoresCursoPorCampoId(Long cursoId) throws SQLException {
        return carregarValoresPorCampoId("curso_layout_valor", "curso_id", cursoId);
    }

    public Map<Long, String> carregarValoresCursoAlunoPorCampoId(Long cursoAlunoId) throws SQLException {
        return carregarValoresPorCampoId("curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId);
    }

    public Map<Long, String> carregarValoresDocentePorCampoId(Long docenteId) throws SQLException {
        return carregarValoresPorCampoId("docente_layout_valor", "docente_id", docenteId);
    }

    public Map<Long, String> carregarValoresIesPorCampoId(Long iesId) throws SQLException {
        return carregarValoresPorCampoId("ies_layout_valor", "ies_id", iesId);
    }

    public Map<Integer, String> carregarValoresAlunoPorNumero(Long alunoId, String modulo) throws SQLException {
        return carregarValoresPorNumero("aluno_layout_valor", "aluno_id", alunoId, modulo);
    }

    public Map<Integer, String> carregarValoresCursoPorNumero(Long cursoId, String modulo) throws SQLException {
        return carregarValoresPorNumero("curso_layout_valor", "curso_id", cursoId, modulo);
    }

    public Map<Integer, String> carregarValoresCursoAlunoPorNumero(Long cursoAlunoId, String modulo) throws SQLException {
        return carregarValoresPorNumero("curso_aluno_layout_valor", "curso_aluno_id", cursoAlunoId, modulo);
    }

    public Map<Integer, String> carregarValoresDocentePorNumero(Long docenteId, String modulo) throws SQLException {
        return carregarValoresPorNumero("docente_layout_valor", "docente_id", docenteId, modulo);
    }

    public Map<Integer, String> carregarValoresIesPorNumero(Long iesId, String modulo) throws SQLException {
        return carregarValoresPorNumero("ies_layout_valor", "ies_id", iesId, modulo);
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

    private void salvarValores(String tabela,
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

    private Map<Long, String> carregarValoresPorCampoId(String tabela,
                                                        String colunaFk,
                                                        Long fkValue) throws SQLException {
        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (fkValue == null) {
            return valores;
        }
        try {
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
        } catch (RuntimeException e) {
            throw toSqlException("Falha ao carregar valores de layout.", e);
        }
        return valores;
    }

    private Map<Integer, String> carregarValoresPorNumero(String tabela,
                                                          String colunaFk,
                                                          Long fkValue,
                                                          String modulo) throws SQLException {
        Map<Integer, String> valores = new LinkedHashMap<Integer, String>();
        if (fkValue == null) {
            return valores;
        }
        try {
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
        } catch (RuntimeException e) {
            throw toSqlException("Falha ao carregar valores de layout por numero.", e);
        }
        return valores;
    }

    private void removerValores(String tabela, String colunaFk, Long fkValue) {
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
