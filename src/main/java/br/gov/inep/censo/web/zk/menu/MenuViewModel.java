package br.gov.inep.censo.web.zk.menu;

import br.gov.inep.censo.model.Usuario;
import br.gov.inep.censo.web.zk.AbstractBaseViewModel;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;
import org.zkoss.zul.Window;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * ViewModel MVVM do shell principal (header + sidebar + center + footer).
 * Substitui MenuComposer.
 */
public class MenuViewModel extends AbstractBaseViewModel {

    private String nomeUsuario;
    private String loginUsuario;
    private String footerText;
    private String currentView;

    // Navegacao ativa
    private boolean navHomeAtivo;
    private boolean navAlunoAtivo;
    private boolean navCursoAtivo;
    private boolean navCursoAlunoAtivo;
    private boolean navDocenteAtivo;
    private boolean navIesAtivo;

    // Conteudo principal e subjanela
    private String mainSrc;
    private String subSrc;
    private String subTitle;
    private boolean subVisible;

    @Init
    public void init() {
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

        nomeUsuario = usuario.getNome() == null ? "" : usuario.getNome().trim();
        loginUsuario = usuario.getLogin() == null ? "" : usuario.getLogin().trim();
        footerText = "Censo Superior 2025 | Versao 2.0.0 | Frontend ZK 8.6.0.2 MVVM";

        HttpServletRequest request = currentRequest();
        String viewParam = request != null ? trimToNull(request.getParameter("view")) : null;
        String view = normalizeView(viewParam);
        currentView = view;
        updateNavAtivo(view);
        mainSrc = resolveMainSrc(view);

        String subParam = request != null ? trimToNull(request.getParameter("sub")) : null;
        String idParam = request != null ? trimToNull(request.getParameter("id")) : null;
        String sub = normalizeSub(subParam);

        if (sub != null) {
            String src = resolveSubSrc(sub);
            if (idParam != null) {
                src = src + "?id=" + idParam;
            }
            subSrc = src;
            subTitle = resolveSubTitle(sub);
            subVisible = true;
        } else {
            subSrc = null;
            subTitle = "Detalhes";
            subVisible = false;
        }
    }

    @Command
    public void irHome() {
        goShell("dashboard");
    }

    @Command
    public void irAluno() {
        goShell("aluno-list");
    }

    @Command
    public void irCurso() {
        goShell("curso-list");
    }

    @Command
    public void irCursoAluno() {
        goShell("curso-aluno-list");
    }

    @Command
    public void irDocente() {
        goShell("docente-list");
    }

    @Command
    public void irIes() {
        goShell("ies-list");
    }

    @Command
    public void sair() {
        HttpSession session = currentSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirect("/login.zul?logout=1");
    }

    @Command
    @NotifyChange("subVisible")
    public void fecharSub() {
        subVisible = false;
        goShell(currentView);
    }

    // --- Helpers privados ---

    private void updateNavAtivo(String view) {
        navHomeAtivo       = "dashboard".equals(view);
        navAlunoAtivo      = "aluno-list".equals(view);
        navCursoAtivo      = "curso-list".equals(view);
        navCursoAlunoAtivo = "curso-aluno-list".equals(view);
        navDocenteAtivo    = "docente-list".equals(view);
        navIesAtivo        = "ies-list".equals(view);
    }

    private String normalizeView(String view) {
        if ("aluno-list".equals(view) || "curso-list".equals(view)
                || "curso-aluno-list".equals(view) || "docente-list".equals(view)
                || "ies-list".equals(view) || "dashboard".equals(view)) {
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
        if ("aluno-list".equals(view))       return "/app/aluno-list.zul";
        if ("curso-list".equals(view))       return "/app/curso-list.zul";
        if ("curso-aluno-list".equals(view)) return "/app/curso-aluno-list.zul";
        if ("docente-list".equals(view))     return "/app/docente-list.zul";
        if ("ies-list".equals(view))         return "/app/ies-list.zul";
        return "/app/home-content.zul";
    }

    private String resolveSubSrc(String sub) {
        if ("aluno-form".equals(sub))       return "/app/aluno-form.zul";
        if ("aluno-view".equals(sub))       return "/app/aluno-view.zul";
        if ("curso-form".equals(sub))       return "/app/curso-form.zul";
        if ("curso-view".equals(sub))       return "/app/curso-view.zul";
        if ("curso-aluno-form".equals(sub)) return "/app/curso-aluno-form.zul";
        if ("docente-form".equals(sub))     return "/app/docente-form.zul";
        if ("docente-view".equals(sub))     return "/app/docente-view.zul";
        if ("ies-form".equals(sub))         return "/app/ies-form.zul";
        if ("ies-view".equals(sub))         return "/app/ies-view.zul";
        return null;
    }

    private String resolveSubTitle(String sub) {
        if ("aluno-form".equals(sub))       return "Cadastro de Aluno";
        if ("aluno-view".equals(sub))       return "Visualizacao de Aluno";
        if ("curso-form".equals(sub))       return "Cadastro de Curso";
        if ("curso-view".equals(sub))       return "Visualizacao de Curso";
        if ("curso-aluno-form".equals(sub)) return "Cadastro de Vinculo Aluno x Curso (Registro 42)";
        if ("docente-form".equals(sub))     return "Cadastro de Docente";
        if ("docente-view".equals(sub))     return "Visualizacao de Docente";
        if ("ies-form".equals(sub))         return "Cadastro de IES";
        if ("ies-view".equals(sub))         return "Visualizacao de IES";
        return "Detalhes";
    }

    // --- Getters ---

    public String getNomeUsuario()   { return nomeUsuario; }
    public String getLoginUsuario()  { return loginUsuario; }
    public String getFooterText()    { return footerText; }
    public String getMainSrc()       { return mainSrc; }
    public String getSubSrc()        { return subSrc; }
    public String getSubTitle()      { return subTitle; }
    public boolean isSubVisible()    { return subVisible; }

    public String navHomeClass()       { return navItemClass(navHomeAtivo); }
    public String navAlunoClass()      { return navItemClass(navAlunoAtivo); }
    public String navCursoClass()      { return navItemClass(navCursoAtivo); }
    public String navCursoAlunoClass() { return navItemClass(navCursoAlunoAtivo); }
    public String navDocenteClass()    { return navItemClass(navDocenteAtivo); }
    public String navIesClass()        { return navItemClass(navIesAtivo); }

    private String navItemClass(boolean ativo) {
        return ativo ? "shell-nav-item shell-nav-item-active" : "shell-nav-item";
    }
}
