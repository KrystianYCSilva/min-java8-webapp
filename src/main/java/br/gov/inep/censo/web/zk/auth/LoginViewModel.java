package br.gov.inep.censo.web.zk.auth;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.service.AuthService;
import br.gov.inep.censo.web.zk.AbstractBaseViewModel;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zkplus.spring.SpringUtil;

import javax.servlet.http.HttpSession;

/**
 * ViewModel MVVM de autenticacao.
 * Substitui LoginComposer.
 */
public class LoginViewModel extends AbstractBaseViewModel {

    private String login;
    private String senha;
    private String msgErro;
    private String msgInfo;
    private boolean erroVisivel;
    private boolean infoVisivel;

    private AuthService authService() {
        return (AuthService) SpringUtil.getBean("authService");
    }

    @Init
    public void init() {
        erroVisivel = false;
        infoVisivel = false;
        msgErro = "";
        msgInfo = "";

        String logout = trimToNull(currentRequest() != null ? currentRequest().getParameter("logout") : null);
        if (logout != null) {
            infoVisivel = true;
            msgInfo = "Sessao encerrada com sucesso.";
        }
    }

    @Command
    @NotifyChange({"msgErro", "msgInfo", "erroVisivel", "infoVisivel"})
    public void entrar() {
        erroVisivel = false;
        infoVisivel = false;
        msgErro = "";
        msgInfo = "";

        String loginVal = trimToNull(login);
        String senhaVal = trimToNull(senha);
        if (loginVal == null || senhaVal == null) {
            erroVisivel = true;
            msgErro = "Informe login e senha.";
            return;
        }

        try {
            Usuario usuario = authService().autenticar(loginVal, senhaVal);
            if (usuario == null) {
                erroVisivel = true;
                msgErro = "Credenciais invalidas.";
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
            erroVisivel = true;
            msgErro = "Erro ao autenticar usuario.";
        }
    }

    @Command
    public void voltar() {
        redirect("/home.zul");
    }

    // --- Getters e Setters ---

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getMsgErro() {
        return msgErro;
    }

    public String getMsgInfo() {
        return msgInfo;
    }

    public boolean isErroVisivel() {
        return erroVisivel;
    }

    public boolean isInfoVisivel() {
        return infoVisivel;
    }
}
