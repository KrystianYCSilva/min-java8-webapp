package br.gov.inep.censo.web.zk.home;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.web.zk.AbstractBaseViewModel;
import org.zkoss.bind.annotation.Init;

import javax.servlet.http.HttpSession;

/**
 * ViewModel MVVM do painel inicial autenticado.
 * Substitui DashboardComposer.
 */
public class DashboardViewModel extends AbstractBaseViewModel {

    private String flashMsg;
    private boolean flashVisivel;
    private String resumo;

    @Init
    public void init() {
        String flash = consumeFlash("flashHomeMessage");
        if (flash != null) {
            flashMsg = flash;
            flashVisivel = true;
        } else {
            flashMsg = "";
            flashVisivel = false;
        }

        HttpSession session = currentSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        String nome = usuario == null ? "Usuario" : usuario.getNome();
        resumo = "Bem-vindo(a), " + nome + ". Use o menu lateral para abrir os modulos de trabalho.";
    }

    // --- Getters ---

    public String getFlashMsg() {
        return flashMsg;
    }

    public boolean isFlashVisivel() {
        return flashVisivel;
    }

    public String getResumo() {
        return resumo;
    }
}
