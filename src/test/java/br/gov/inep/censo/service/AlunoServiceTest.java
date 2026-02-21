package br.gov.inep.censo.service;

import br.gov.inep.censo.config.TestDatabaseConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Testes de integracao do servico de Aluno com H2 em memoria.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
public class AlunoServiceTest {

    @Autowired
    private AlunoService alunoService;

    @Test
    public void listarDeveRetornarListaNaoNula() throws Exception {
        Assert.assertNotNull(alunoService.listar());
    }

    @Test
    public void contarDeveRetornarValorNaoNegativo() throws Exception {
        Assert.assertTrue(alunoService.contar() >= 0);
    }

    @Test
    public void buscarPorIdNuloDeveRetornarNull() throws Exception {
        Assert.assertNull(alunoService.buscarPorId(null));
    }

    @Test
    public void excluirIdNuloNaoDeveLancarExcecao() throws Exception {
        alunoService.excluir(null);
    }
}
