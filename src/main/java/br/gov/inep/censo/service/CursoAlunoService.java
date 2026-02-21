package br.gov.inep.censo.service;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.model.CursoAluno;
import br.gov.inep.censo.repository.CursoAlunoRepository;
import br.gov.inep.censo.repository.LayoutCampoValueRepository;
import br.gov.inep.censo.repository.OpcaoVinculoRepository;
import br.gov.inep.censo.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Servico de negocio do Registro 42 (vinculo aluno-curso).
 */
@Service
public class CursoAlunoService {

    private final CursoAlunoRepository cursoAlunoRepository;
    private final OpcaoVinculoRepository opcaoVinculoRepository;
    private final LayoutCampoValueRepository layoutCampoValueRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CursoAlunoService(CursoAlunoRepository cursoAlunoRepository,
                             OpcaoVinculoRepository opcaoVinculoRepository,
                             LayoutCampoValueRepository layoutCampoValueRepository) {
        this.cursoAlunoRepository = cursoAlunoRepository;
        this.opcaoVinculoRepository = opcaoVinculoRepository;
        this.layoutCampoValueRepository = layoutCampoValueRepository;
    }

    @Transactional
    public Long cadastrar(CursoAluno cursoAluno, long[] opcaoIds, Map<Long, String> camposComplementares)
            throws SQLException {
        validar(cursoAluno);
        cursoAluno.setAluno(entityManager.getReference(Aluno.class, cursoAluno.getAlunoId()));
        cursoAluno.setCurso(entityManager.getReference(Curso.class, cursoAluno.getCursoId()));
        CursoAluno salvo = cursoAlunoRepository.save(cursoAluno);
        Long cursoAlunoId = salvo != null ? salvo.getId() : cursoAluno.getId();
        if (cursoAlunoId == null) {
            throw new SQLException("Falha ao gerar ID para curso_aluno.");
        }
        opcaoVinculoRepository.salvarVinculosCursoAluno(cursoAlunoId, opcaoIds);
        layoutCampoValueRepository.salvarValoresCursoAluno(cursoAlunoId, camposComplementares);
        return cursoAlunoId;
    }

    public List<CursoAluno> listar() throws SQLException {
        try {
            List<CursoAluno> itens = cursoAlunoRepository.findAllWithAlunoAndCursoOrderByIdDesc();
            for (int i = 0; i < itens.size(); i++) {
                hydrateResumo(itens.get(i));
            }
            return itens;
        } catch (RuntimeException e) {
            throw toSqlException("Falha ao listar registros 42 via repository.", e);
        }
    }

    private void hydrateResumo(CursoAluno item) throws SQLException {
        if (item == null) {
            return;
        }
        if (item.getAluno() != null) {
            item.setAlunoId(item.getAluno().getId());
            item.setAlunoNome(item.getAluno().getNome());
        }
        if (item.getCurso() != null) {
            item.setCursoId(item.getCurso().getId());
            item.setCursoNome(item.getCurso().getNome());
            item.setCodigoCursoEmec(item.getCurso().getCodigoCursoEmec());
        }
        item.setFinanciamentosResumo(opcaoVinculoRepository.resumirCursoAluno(
                item.getId(), CategoriasOpcao.CURSO_ALUNO_TIPO_FINANCIAMENTO));
        item.setApoioSocialResumo(opcaoVinculoRepository.resumirCursoAluno(
                item.getId(), CategoriasOpcao.CURSO_ALUNO_APOIO_SOCIAL));
        item.setAtividadesResumo(opcaoVinculoRepository.resumirCursoAluno(
                item.getId(), CategoriasOpcao.CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR));
        item.setReservasResumo(opcaoVinculoRepository.resumirCursoAluno(
                item.getId(), CategoriasOpcao.CURSO_ALUNO_RESERVA_VAGA));
    }

    private void validar(CursoAluno cursoAluno) {
        if (cursoAluno == null) {
            throw new IllegalArgumentException("Registro 42 nao informado.");
        }
        if (cursoAluno.getAlunoId() == null || cursoAluno.getCursoId() == null) {
            throw new IllegalArgumentException("Aluno e Curso sao obrigatorios.");
        }
        if (cursoAluno.getIdAlunoIes() == null || cursoAluno.getIdAlunoIes().trim().length() == 0) {
            throw new IllegalArgumentException("ID na IES e obrigatorio.");
        }
        if (!ValidationUtils.isPeriodoReferenciaValido(cursoAluno.getPeriodoReferencia())) {
            throw new IllegalArgumentException("Periodo de referencia deve estar no formato AAAA.");
        }
        if (cursoAluno.getSemestreIngresso() != null && cursoAluno.getSemestreIngresso().trim().length() > 0
                && !ValidationUtils.isSemestreValido(cursoAluno.getSemestreIngresso())) {
            throw new IllegalArgumentException("Semestre de ingresso invalido. Use 01AAAA ou 02AAAA.");
        }
    }

    private SQLException toSqlException(String mensagem, RuntimeException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }
        return new SQLException(mensagem, e);
    }
}
