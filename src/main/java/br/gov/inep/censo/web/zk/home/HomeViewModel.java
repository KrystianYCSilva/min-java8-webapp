package br.gov.inep.censo.web.zk.home;

import br.gov.inep.censo.web.zk.AbstractBaseViewModel;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * ViewModel MVVM da pagina inicial publica.
 * Substitui HomeComposer.
 */
public class HomeViewModel extends AbstractBaseViewModel {

    private String flashMsg;
    private boolean flashVisivel;

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
    }

    @Command
    public void entrar() {
        redirect("/login.zul");
    }

    @Command
    public void irMenu() {
        goShell("dashboard");
    }

    // --- Getters ---

    public String getFlashMsg() {
        return flashMsg;
    }

    public boolean isFlashVisivel() {
        return flashVisivel;
    }
}
