package br.gov.inep.censo.service;

import br.gov.inep.censo.model.Usuario;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Testes de integracao do servico de autenticacao com JDBC.
 */
public class AuthServiceTest {

    @Test
    @Ignore("Requer Spring Context configurado (UsuarioRepository)")
    public void deveAutenticarUsuarioPadraoQuandoSenhaCorreta() throws Exception {
        AuthService service = new AuthService();
        Usuario usuario = service.autenticar("admin", "admin123");

        Assert.assertNotNull(usuario);
        Assert.assertEquals("admin", usuario.getLogin());
        Assert.assertTrue(usuario.isAtivo());
    }

    @Test
    @Ignore("Requer Spring Context configurado (UsuarioRepository)")
    public void naoDeveAutenticarQuandoSenhaIncorreta() throws Exception {
        AuthService service = new AuthService();
        Usuario usuario = service.autenticar("admin", "senha-incorreta");
        Assert.assertNull(usuario);
    }
}
