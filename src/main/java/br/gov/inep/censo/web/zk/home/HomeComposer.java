package br.gov.inep.censo.web.zk.home;

import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

/**
 * Controller MVC da pagina inicial.
 */
public class HomeComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;

    @Wire
    private Label lblFlash;

    @Listen("onCreate = #winHome")
    public void onCreate() {
        String flash = consumeFlash("flashHomeMessage");
        if (flash == null) {
            lblFlash.setVisible(false);
            lblFlash.setValue("");
            return;
        }
        lblFlash.setVisible(true);
        lblFlash.setValue(flash);
    }

    @Listen("onClick = #btnEntrar")
    public void onClickBtnEntrar() {
        redirect("/login.zul");
    }

    @Listen("onClick = #btnMenu")
    public void onClickBtnMenu() {
        goShell("dashboard");
    }
}
