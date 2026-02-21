package br.gov.inep.censo.web.zk.home;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import javax.servlet.http.HttpSession;

/**
 * Conteudo central da home do shell autenticado.
 */
public class DashboardComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;

    @Wire
    private Label lblFlashDashboard;

    @Wire
    private Label lblResumoDashboard;

    @Listen("onCreate = #winDashboard")
    public void onCreate() {
        String flash = consumeFlash("flashHomeMessage");
        if (flash == null) {
            lblFlashDashboard.setVisible(false);
            lblFlashDashboard.setValue("");
        } else {
            lblFlashDashboard.setVisible(true);
            lblFlashDashboard.setValue(flash);
        }

        HttpSession session = currentSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        String nome = usuario == null ? "Usuario" : usuario.getNome();
        lblResumoDashboard.setValue("Bem-vindo(a), " + nome + ". Use o menu lateral para abrir os modulos de trabalho.");
    }
}
