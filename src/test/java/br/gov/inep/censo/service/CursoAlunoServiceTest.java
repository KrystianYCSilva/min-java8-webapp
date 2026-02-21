package br.gov.inep.censo.service;

import br.gov.inep.censo.model.CursoAluno;
import br.gov.inep.censo.repository.CursoAlunoRepository;
import br.gov.inep.censo.repository.LayoutCampoValueRepository;
import br.gov.inep.censo.repository.OpcaoVinculoRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

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

    private CursoAlunoService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new CursoAlunoService(
                cursoAlunoRepository,
                opcaoVinculoRepository,
                layoutCampoValueRepository
        );
    }

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
