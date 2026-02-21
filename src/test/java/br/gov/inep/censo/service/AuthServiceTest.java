package br.gov.inep.censo.service;

import br.gov.inep.censo.config.TestDatabaseConfig;
import br.gov.inep.censo.model.Usuario;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Testes de integracao do servico de autenticacao com H2 em memoria.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDatabaseConfig.class)
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    public void deveAutenticarUsuarioPadraoQuandoSenhaCorreta() throws Exception {
        Usuario usuario = authService.autenticar("admin", "admin123");

        Assert.assertNotNull(usuario);
        Assert.assertEquals("admin", usuario.getLogin());
        Assert.assertTrue(usuario.isAtivo());
    }

    @Test
    public void naoDeveAutenticarQuandoSenhaIncorreta() throws Exception {
        Usuario usuario = authService.autenticar("admin", "senha-incorreta");
        Assert.assertNull(usuario);
    }
}
