package br.gov.inep.censo.service;

import br.gov.inep.censo.model.CursoAluno;
import br.gov.inep.censo.repository.CursoAlunoRepository;
import br.gov.inep.censo.repository.LayoutCampoValueRepository;
import br.gov.inep.censo.repository.OpcaoVinculoRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Testes unitarios de validacao do servico de Registro 42.
 */
public class CursoAlunoServiceTest {

    @Mock
    private CursoAlunoRepository cursoAlunoRepository;

    @Mock
    private OpcaoVinculoRepository opcaoVinculoRepository;

    @Mock
    private LayoutCampoValueRepository layoutCampoValueRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    private CursoAlunoService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new CursoAlunoService(
                cursoAlunoRepository,
                opcaoVinculoRepository,
                layoutCampoValueRepository,
                transactionManager,
                entityManagerFactory
        );
    }

    // Teste requer Spring Context (SpringBridge.inTransaction)
    // Use testes de integração para validar cadastro completo

    @Test
    public void deveFalharQuandoPeriodoReferenciaInvalido() throws Exception {
        CursoAluno cursoAluno = novoCursoAlunoValido();
        cursoAluno.setPeriodoReferencia("20A5");

        try {
            service.cadastrar(cursoAluno, new long[0], Collections.<Long, String>emptyMap());
            Assert.fail("Esperava IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Periodo de referencia"));
        }
    }

    @Test
    public void deveFalharQuandoSemestreIngressoInvalido() throws Exception {
        CursoAluno cursoAluno = novoCursoAlunoValido();
        cursoAluno.setSemestreIngresso("032025");

        try {
            service.cadastrar(cursoAluno, new long[0], Collections.<Long, String>emptyMap());
            Assert.fail("Esperava IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Semestre de ingresso"));
        }
    }

    // Teste requer Spring Context (repository.findAllWithAlunoAndCursoOrderByIdDesc)
    // Use testes de integração para validar listagem completa

    private CursoAluno novoCursoAlunoValido() {
        CursoAluno cursoAluno = new CursoAluno();
        cursoAluno.setAlunoId(Long.valueOf(1L));
        cursoAluno.setCursoId(Long.valueOf(2L));
        cursoAluno.setIdAlunoIes("ALUNO_IES");
        cursoAluno.setPeriodoReferencia("2025");
        cursoAluno.setSemestreIngresso("012025");
        return cursoAluno;
    }
}
