package br.gov.inep.censo.service;

import br.gov.inep.censo.config.TestDatabaseConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Testes de integracao do servico de IES com H2 em memoria.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
public class IesServiceTest {

    @Autowired
    private IesService iesService;

    @Test
    public void listarDeveRetornarListaNaoNula() throws Exception {
        Assert.assertNotNull(iesService.listar());
    }

    @Test
    public void contarDeveRetornarValorNaoNegativo() throws Exception {
        Assert.assertTrue(iesService.contar() >= 0);
    }

    @Test
    public void buscarPorIdNuloDeveRetornarNull() throws Exception {
        Assert.assertNull(iesService.buscarPorId(null));
    }

    @Test
    public void excluirIdNuloNaoDeveLancarExcecao() throws Exception {
        iesService.excluir(null);
    }
}
