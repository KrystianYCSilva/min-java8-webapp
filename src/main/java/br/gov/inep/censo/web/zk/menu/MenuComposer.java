package br.gov.inep.censo.web.zk.menu;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Shell principal da aplicacao (header + sidebar + center + footer).
 */
public class MenuComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;

    @Wire
    private Label lblUsuarioHeader;

    @Wire
    private Label lblUsuarioLogin;

    @Wire
    private Label lblFooter;

    @Wire
    private Label btnNavHome;

    @Wire
    private Label btnNavAluno;

    @Wire
    private Label btnNavCurso;

    @Wire
    private Label btnNavCursoAluno;

    @Wire
    private Label btnNavDocente;

    @Wire
    private Label btnNavIes;

    @Wire
    private Include incMain;

    @Wire
    private Window winSub;

    private String currentView = "dashboard";

    @Listen("onCreate = #winShell")
    public void onCreate() {
        HttpSession session = currentSession(false);
        if (session == null) {
            redirect("/login.zul");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            redirect("/login.zul");
            return;
        }

        setLabelValue(lblUsuarioHeader, usuario.getNome());
        setLabelValue(lblUsuarioLogin, usuario.getLogin());
        setLabelValue(lblFooter, "Censo Superior 2025 | Versao 2.0.0 | Frontend ZK 8.6.0.1 MVC");

        HttpServletRequest request = currentRequest();
        String viewParam = request != null ? request.getParameter("view") : null;
        String view = normalizeView(trimToNull(viewParam));
        currentView = view;
        updateActiveNavigation(view);
        if (incMain != null) {
            incMain.setSrc(resolveMainSrc(view));
        }

        String subParam = request != null ? request.getParameter("sub") : null;
        String idParam = request != null ? request.getParameter("id") : null;
        String sub = normalizeSub(trimToNull(subParam));
        String id = trimToNull(idParam);
        if (sub != null) {
            openSubWindow(sub, id);
        } else {
            if (winSub != null) {
                winSub.setVisible(false);
            }
            Include includeSub = resolveSubInclude();
            if (includeSub != null) {
                includeSub.setSrc((String) null);
            }
        }
    }

    @Listen("onClick = #btnNavHome")
    public void onClickBtnNavHome() {
        goShell("dashboard");
    }

    @Listen("onClick = #btnNavAluno")
    public void onClickBtnNavAluno() {
        goShell("aluno-list");
    }

    @Listen("onClick = #btnNavCurso")
    public void onClickBtnNavCurso() {
        goShell("curso-list");
    }

    @Listen("onClick = #btnNavCursoAluno")
    public void onClickBtnNavCursoAluno() {
        goShell("curso-aluno-list");
    }

    @Listen("onClick = #btnNavDocente")
    public void onClickBtnNavDocente() {
        goShell("docente-list");
    }

    @Listen("onClick = #btnNavIes")
    public void onClickBtnNavIes() {
        goShell("ies-list");
    }

    @Listen("onClick = #btnSairHeader")
    public void onClickBtnSairHeader() {
        HttpSession session = currentSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirect("/login.zul?logout=1");
    }

    @Listen("onClose = #winSub")
    public void onCloseWinSub() {
        goShell(currentView);
    }

    private void setLabelValue(Label label, String value) {
        if (label != null) {
            label.setValue(trimToEmpty(value));
        }
    }

    private void updateActiveNavigation(String view) {
        setNavItemState(btnNavHome, "dashboard".equals(view));
        setNavItemState(btnNavAluno, "aluno-list".equals(view));
        setNavItemState(btnNavCurso, "curso-list".equals(view));
        setNavItemState(btnNavCursoAluno, "curso-aluno-list".equals(view));
        setNavItemState(btnNavDocente, "docente-list".equals(view));
        setNavItemState(btnNavIes, "ies-list".equals(view));
    }

    private void setNavItemState(Label item, boolean active) {
        if (item == null) {
            return;
        }
        if (active) {
            item.setSclass("shell-nav-item shell-nav-item-active");
        } else {
            item.setSclass("shell-nav-item");
        }
    }

    private void openSubWindow(String sub, String id) {
        String src = resolveSubSrc(sub);
        if (src == null) {
            return;
        }
        if (id != null) {
            src = src + "?id=" + id;
        }
        Label labelSubTitle = resolveSubTitleLabel();
        if (labelSubTitle != null) {
            labelSubTitle.setValue(resolveSubTitle(sub));
        }

        Include includeSub = resolveSubInclude();
        if (includeSub != null) {
            includeSub.setSrc(src);
        }

        if (winSub != null) {
            winSub.setVisible(true);
        }
    }

    private Include resolveSubInclude() {
        if (winSub == null) {
            return null;
        }
        try {
            return (Include) winSub.getFellow("incSub");
        } catch (RuntimeException e) {
            return null;
        }
    }

    private Label resolveSubTitleLabel() {
        if (winSub == null) {
            return null;
        }
        try {
            return (Label) winSub.getFellow("lblSubTitle");
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String normalizeView(String view) {
        if ("aluno-list".equals(view) || "curso-list".equals(view) || "curso-aluno-list".equals(view)
                || "docente-list".equals(view) || "ies-list".equals(view) || "dashboard".equals(view)) {
            return view;
        }
        return "dashboard";
    }

    private String normalizeSub(String sub) {
        if ("aluno-form".equals(sub) || "aluno-view".equals(sub)
                || "curso-form".equals(sub) || "curso-view".equals(sub)
                || "curso-aluno-form".equals(sub)
                || "docente-form".equals(sub) || "docente-view".equals(sub)
                || "ies-form".equals(sub) || "ies-view".equals(sub)) {
            return sub;
        }
        return null;
    }

    private String resolveMainSrc(String view) {
        if ("aluno-list".equals(view)) {
            return "/app/aluno-list.zul";
        }
        if ("curso-list".equals(view)) {
            return "/app/curso-list.zul";
        }
        if ("curso-aluno-list".equals(view)) {
            return "/app/curso-aluno-list.zul";
        }
        if ("docente-list".equals(view)) {
            return "/app/docente-list.zul";
        }
        if ("ies-list".equals(view)) {
            return "/app/ies-list.zul";
        }
        return "/app/home-content.zul";
    }

    private String resolveSubSrc(String sub) {
        if ("aluno-form".equals(sub)) {
            return "/app/aluno-form.zul";
        }
        if ("aluno-view".equals(sub)) {
            return "/app/aluno-view.zul";
        }
        if ("curso-form".equals(sub)) {
            return "/app/curso-form.zul";
        }
        if ("curso-view".equals(sub)) {
            return "/app/curso-view.zul";
        }
        if ("curso-aluno-form".equals(sub)) {
            return "/app/curso-aluno-form.zul";
        }
        if ("docente-form".equals(sub)) {
            return "/app/docente-form.zul";
        }
        if ("docente-view".equals(sub)) {
            return "/app/docente-view.zul";
        }
        if ("ies-form".equals(sub)) {
            return "/app/ies-form.zul";
        }
        if ("ies-view".equals(sub)) {
            return "/app/ies-view.zul";
        }
        return null;
    }

    private String resolveSubTitle(String sub) {
        if ("aluno-form".equals(sub)) {
            return "Cadastro de Aluno";
        }
        if ("aluno-view".equals(sub)) {
            return "Visualizacao de Aluno";
        }
        if ("curso-form".equals(sub)) {
            return "Cadastro de Curso";
        }
        if ("curso-view".equals(sub)) {
            return "Visualizacao de Curso";
        }
        if ("curso-aluno-form".equals(sub)) {
            return "Cadastro de Vinculo Aluno x Curso (Registro 42)";
        }
        if ("docente-form".equals(sub)) {
            return "Cadastro de Docente";
        }
        if ("docente-view".equals(sub)) {
            return "Visualizacao de Docente";
        }
        if ("ies-form".equals(sub)) {
            return "Cadastro de IES";
        }
        if ("ies-view".equals(sub)) {
            return "Visualizacao de IES";
        }
        return "Detalhes";
    }
}
