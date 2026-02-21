package br.gov.inep.censo.web.zk.auth;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.service.AuthService;
import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import javax.servlet.http.HttpSession;

/**
 * Controller MVC de autenticacao.
 */
public class LoginComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;

    private AuthService authService() {
        return (AuthService) SpringUtil.getBean("authService");
    }

    @Wire
    private Textbox txtLogin;

    @Wire
    private Textbox txtSenha;

    @Wire
    private Label lblErro;

    @Wire
    private Label lblInfo;

    @Listen("onCreate = #winLogin")
    public void onCreate() {
        clearMessages();
        String logout = trimToNull(currentRequest().getParameter("logout"));
        if (logout != null) {
            lblInfo.setVisible(true);
            lblInfo.setValue("Sessao encerrada com sucesso.");
        }
    }

    @Listen("onClick = #btnEntrar")
    public void onClickBtnEntrar() {
        clearMessages();

        String login = trimToNull(txtLogin.getValue());
        String senha = trimToNull(txtSenha.getValue());
        if (login == null || senha == null) {
            showError("Informe login e senha.");
            return;
        }

        try {
            Usuario usuario = authService().autenticar(login, senha);
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

    @Listen("onClick = #btnVoltar")
    public void onClickBtnVoltar() {
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
