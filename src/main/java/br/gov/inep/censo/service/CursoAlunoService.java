package br.gov.inep.censo.service;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.model.CursoAluno;
import br.gov.inep.censo.repository.CursoAlunoRepository;
import br.gov.inep.censo.repository.LayoutCampoValueRepository;
import br.gov.inep.censo.repository.OpcaoVinculoRepository;
import br.gov.inep.censo.spring.SpringBridge;
import br.gov.inep.censo.util.ValidationUtils;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Servico de negocio do Registro 42 (vinculo aluno-curso).
 */
public class CursoAlunoService {

    private final CursoAlunoRepository cursoAlunoRepository;
    private final OpcaoVinculoRepository opcaoVinculoRepository;
    private final LayoutCampoValueRepository layoutCampoValueRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    public CursoAlunoService() {
        this(SpringBridge.getBean(CursoAlunoRepository.class),
                new OpcaoVinculoRepository(),
                new LayoutCampoValueRepository(),
                SpringBridge.getBean(PlatformTransactionManager.class),
                SpringBridge.getBean(EntityManagerFactory.class));
    }

    public CursoAlunoService(CursoAlunoRepository cursoAlunoRepository,
                             OpcaoVinculoRepository opcaoVinculoRepository,
                             LayoutCampoValueRepository layoutCampoValueRepository,
                             PlatformTransactionManager transactionManager,
                             EntityManagerFactory entityManagerFactory) {
        this.cursoAlunoRepository = cursoAlunoRepository;
        this.opcaoVinculoRepository = opcaoVinculoRepository;
        this.layoutCampoValueRepository = layoutCampoValueRepository;
        this.transactionManager = transactionManager;
        this.entityManagerFactory = entityManagerFactory;
    }

    public Long cadastrar(CursoAluno cursoAluno, long[] opcaoIds, Map<Long, String> camposComplementares)
            throws SQLException {
        validar(cursoAluno);
        if (canUseRepositoryWritePath()) {
            final CursoAluno cursoAlunoFinal = cursoAluno;
            final long[] opcaoIdsFinal = opcaoIds;
            final Map<Long, String> camposFinal = camposComplementares;
            return SpringBridge.inTransaction(transactionManager, entityManagerFactory,
                    new SpringBridge.SqlWork<Long>() {
                        public Long execute(EntityManager entityManager) throws SQLException {
                            cursoAlunoFinal.setAluno(entityManager.getReference(Aluno.class, cursoAlunoFinal.getAlunoId()));
                            cursoAlunoFinal.setCurso(entityManager.getReference(Curso.class, cursoAlunoFinal.getCursoId()));
                            CursoAluno salvo = cursoAlunoRepository.save(cursoAlunoFinal);
                            Long cursoAlunoId = salvo != null ? salvo.getId() : cursoAlunoFinal.getId();
                            if (cursoAlunoId == null) {
                                throw new SQLException("Falha ao gerar ID para curso_aluno.");
                            }
                            opcaoVinculoRepository.salvarVinculosCursoAluno(entityManager, cursoAlunoId, opcaoIdsFinal);
                            layoutCampoValueRepository.salvarValoresCursoAluno(entityManager, cursoAlunoId, camposFinal);
                            return cursoAlunoId;
                        }
                    }, "Falha ao cadastrar registro 42 via repository.");
        }
        throw new SQLException("Infraestrutura Spring Data/Transaction indisponivel para cadastrar registro 42.");
    }

    public List<CursoAluno> listar() throws SQLException {
        if (cursoAlunoRepository != null) {
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
        throw new SQLException("CursoAlunoRepository indisponivel para listagem.");
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

    private boolean canUseRepositoryWritePath() {
        return cursoAlunoRepository != null && opcaoVinculoRepository != null && layoutCampoValueRepository != null
                && transactionManager != null && entityManagerFactory != null;
    }
}


