package br.gov.inep.censo.service;

import br.gov.inep.censo.config.TestDatabaseConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Testes de integracao do servico de Curso com H2 em memoria.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
public class CursoServiceTest {

    @Autowired
    private CursoService cursoService;

    @Test
    public void listarDeveRetornarListaNaoNula() throws Exception {
        Assert.assertNotNull(cursoService.listar());
    }

    @Test
    public void contarDeveRetornarValorNaoNegativo() throws Exception {
        Assert.assertTrue(cursoService.contar() >= 0);
    }

    @Test
    public void buscarPorIdNuloDeveRetornarNull() throws Exception {
        Assert.assertNull(cursoService.buscarPorId(null));
    }

    @Test
    public void excluirIdNuloNaoDeveLancarExcecao() throws Exception {
        cursoService.excluir(null);
    }
}
