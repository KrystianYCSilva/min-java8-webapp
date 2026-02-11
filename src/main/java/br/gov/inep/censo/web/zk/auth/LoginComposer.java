package br.gov.inep.censo.web.zk.auth;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.service.AuthService;
import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import javax.servlet.http.HttpSession;

/**
 * Controller MVC de autenticacao.
 */
public class LoginComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    private Textbox txtLogin;
    private Textbox txtSenha;
    private Label lblErro;
    private Label lblInfo;

    public void onCreate$winLogin() {
        clearMessages();
        String logout = trimToNull(currentRequest().getParameter("logout"));
        if (logout != null) {
            lblInfo.setVisible(true);
            lblInfo.setValue("Sessao encerrada com sucesso.");
        }
    }

    public void onClick$btnEntrar() {
        clearMessages();

        String login = trimToNull(txtLogin.getValue());
        String senha = trimToNull(txtSenha.getValue());
        if (login == null || senha == null) {
            showError("Informe login e senha.");
            return;
        }

        try {
            Usuario usuario = authService.autenticar(login, senha);
            if (usuario == null) {
                showError("Credenciais invalidas.");
                return;
            }

            HttpSession sessionExistente = currentSession(false);
            if (sessionExistente != null) {
                sessionExistente.invalidate();
            }

            HttpSession novaSessao = currentSession(true);
            novaSessao.setAttribute("usuarioLogado", usuario);
            goShell("dashboard");
        } catch (Exception e) {
            showError("Erro ao autenticar usuario.");
        }
    }

    public void onClick$btnVoltar() {
        redirect("/home.zul");
    }

    private void clearMessages() {
        lblErro.setVisible(false);
        lblErro.setValue("");
        lblInfo.setVisible(false);
        lblInfo.setValue("");
    }

    private void showError(String message) {
        lblErro.setVisible(true);
        lblErro.setValue(message);
    }
}
