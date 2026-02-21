package br.gov.inep.censo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Repositorio custom para vinculos 1..N de opcoes de dominio por modulo.
 */
@Component
public class OpcaoVinculoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void salvarVinculosAluno(Long alunoId, long[] opcaoIds) throws SQLException {
        salvarVinculos("aluno_opcao", "aluno_id", alunoId, opcaoIds);
    }

    public void salvarVinculosCurso(Long cursoId, long[] opcaoIds) throws SQLException {
        salvarVinculos("curso_opcao", "curso_id", cursoId, opcaoIds);
    }

    public void salvarVinculosCursoAluno(Long cursoAlunoId, long[] opcaoIds) throws SQLException {
        salvarVinculos("curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, opcaoIds);
    }

    public void substituirVinculosAluno(Long alunoId, long[] opcaoIds) throws SQLException {
        removerVinculos("aluno_opcao", "aluno_id", alunoId);
        salvarVinculosAluno(alunoId, opcaoIds);
    }

    public void substituirVinculosCurso(Long cursoId, long[] opcaoIds) throws SQLException {
        removerVinculos("curso_opcao", "curso_id", cursoId);
        salvarVinculosCurso(cursoId, opcaoIds);
    }

    public void substituirVinculosCursoAluno(Long cursoAlunoId, long[] opcaoIds) throws SQLException {
        removerVinculos("curso_aluno_opcao", "curso_aluno_id", cursoAlunoId);
        salvarVinculosCursoAluno(cursoAlunoId, opcaoIds);
    }

    public void removerVinculosAluno(Long alunoId) throws SQLException {
        removerVinculos("aluno_opcao", "aluno_id", alunoId);
    }

    public void removerVinculosCurso(Long cursoId) throws SQLException {
        removerVinculos("curso_opcao", "curso_id", cursoId);
    }

    public void removerVinculosCursoAluno(Long cursoAlunoId) throws SQLException {
        removerVinculos("curso_aluno_opcao", "curso_aluno_id", cursoAlunoId);
    }

    public String resumirAluno(Long alunoId, String categoria) throws SQLException {
        return resumir("aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public String resumirCurso(Long cursoId, String categoria) throws SQLException {
        return resumir("curso_opcao", "curso_id", cursoId, categoria);
    }

    public String resumirCursoAluno(Long cursoAlunoId, String categoria) throws SQLException {
        return resumir("curso_aluno_opcao", "curso_aluno_id", cursoAlunoId, categoria);
    }

    public List<Long> listarIdsAluno(Long alunoId, String categoria) throws SQLException {
        return listarIds("aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public List<Long> listarIdsCurso(Long cursoId, String categoria) throws SQLException {
        return listarIds("curso_opcao", "curso_id", cursoId, categoria);
    }

    public List<String> listarCodigosAluno(Long alunoId, String categoria) throws SQLException {
        return listarCodigos("aluno_opcao", "aluno_id", alunoId, categoria);
    }

    public List<String> listarCodigosCurso(Long cursoId, String categoria) throws SQLException {
        return listarCodigos("curso_opcao", "curso_id", cursoId, categoria);
    }

    private void salvarVinculos(String tabela, String colunaFk, Long fkValue, long[] opcaoIds) {
        if (fkValue == null || opcaoIds == null || opcaoIds.length == 0) {
            return;
        }
        Set<Long> idsUnicos = new LinkedHashSet<Long>();
        for (int i = 0; i < opcaoIds.length; i++) {
            idsUnicos.add(Long.valueOf(opcaoIds[i]));
        }
        Query insertQuery = entityManager.createNativeQuery(
                "INSERT INTO " + tabela + " (" + colunaFk + ", opcao_id) VALUES (:fkValue, :opcaoId)");
        for (Long opcaoId : idsUnicos) {
            insertQuery.setParameter("fkValue", fkValue.longValue());
            insertQuery.setParameter("opcaoId", opcaoId.longValue());
            insertQuery.executeUpdate();
        }
    }

    private void removerVinculos(String tabela, String colunaFk, Long fkValue) {
        if (fkValue == null) {
            return;
        }
        Query deleteQuery = entityManager.createNativeQuery(
                "DELETE FROM " + tabela + " WHERE " + colunaFk + " = :fkValue");
        deleteQuery.setParameter("fkValue", fkValue.longValue());
        deleteQuery.executeUpdate();
    }

    private String resumir(String tabela, String colunaFk, Long fkValue, String categoria)
            throws SQLException {
        if (fkValue == null) {
            return "";
        }
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT o.nome FROM " + tabela + " r " +
                            "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                            "WHERE r." + colunaFk + " = :fkValue AND o.categoria = :categoria " +
                            "ORDER BY o.nome");
            query.setParameter("fkValue", fkValue.longValue());
            query.setParameter("categoria", categoria);
            List nomes = query.getResultList();
            StringBuilder resumo = new StringBuilder();
            for (int i = 0; i < nomes.size(); i++) {
                Object nome = nomes.get(i);
                if (nome == null) {
                    continue;
                }
                if (resumo.length() > 0) {
                    resumo.append(", ");
                }
                resumo.append(nome.toString());
            }
            return resumo.toString();
        } catch (RuntimeException e) {
            throw toSqlException("Falha ao resumir opcoes.", e);
        }
    }

    private List<Long> listarIds(String tabela, String colunaFk, Long fkValue, String categoria)
            throws SQLException {
        List<Long> ids = new ArrayList<Long>();
        if (fkValue == null) {
            return ids;
        }
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT o.id FROM " + tabela + " r " +
                            "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                            "WHERE r." + colunaFk + " = :fkValue AND o.categoria = :categoria ORDER BY o.nome");
            query.setParameter("fkValue", fkValue.longValue());
            query.setParameter("categoria", categoria);
            List rows = query.getResultList();
            for (int i = 0; i < rows.size(); i++) {
                Object value = rows.get(i);
                if (value == null) {
                    continue;
                }
                if (value instanceof Number) {
                    ids.add(Long.valueOf(((Number) value).longValue()));
                } else {
                    ids.add(Long.valueOf(value.toString()));
                }
            }
        } catch (RuntimeException e) {
            throw toSqlException("Falha ao listar ids de opcoes.", e);
        }
        return ids;
    }

    private List<String> listarCodigos(String tabela,
                                       String colunaFk,
                                       Long fkValue,
                                       String categoria)
            throws SQLException {
        List<String> codigos = new ArrayList<String>();
        if (fkValue == null) {
            return codigos;
        }
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT o.codigo FROM " + tabela + " r " +
                            "INNER JOIN dominio_opcao o ON o.id = r.opcao_id " +
                            "WHERE r." + colunaFk + " = :fkValue AND o.categoria = :categoria ORDER BY o.nome");
            query.setParameter("fkValue", fkValue.longValue());
            query.setParameter("categoria", categoria);
            List rows = query.getResultList();
            for (int i = 0; i < rows.size(); i++) {
                Object value = rows.get(i);
                if (value != null) {
                    codigos.add(value.toString());
                }
            }
        } catch (RuntimeException e) {
            throw toSqlException("Falha ao listar codigos de opcoes.", e);
        }
        return codigos;
    }

    private SQLException toSqlException(String message, RuntimeException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }
        return new SQLException(message, e);
    }
}
