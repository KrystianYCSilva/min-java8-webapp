package br.gov.inep.censo.service;

import br.gov.inep.censo.config.TestDatabaseConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Testes de integracao do servico de Docente com H2 em memoria.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
public class DocenteServiceTest {

    @Autowired
    private DocenteService docenteService;

    @Test
    public void listarDeveRetornarListaNaoNula() throws Exception {
        Assert.assertNotNull(docenteService.listar());
    }

    @Test
    public void contarDeveRetornarValorNaoNegativo() throws Exception {
        Assert.assertTrue(docenteService.contar() >= 0);
    }

    @Test
    public void buscarPorIdNuloDeveRetornarNull() throws Exception {
        Assert.assertNull(docenteService.buscarPorId(null));
    }

    @Test
    public void excluirIdNuloNaoDeveLancarExcecao() throws Exception {
        docenteService.excluir(null);
    }
}
