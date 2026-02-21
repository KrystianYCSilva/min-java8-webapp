package br.gov.inep.censo.web.zk.modulo;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.OpcaoDominio;
import br.gov.inep.censo.model.enums.FormatoOfertaEnum;
import br.gov.inep.censo.model.enums.NivelAcademicoEnum;
import br.gov.inep.censo.service.CatalogoService;
import br.gov.inep.censo.service.CursoService;
import br.gov.inep.censo.web.zk.AbstractBaseViewModel;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.*;

import java.util.*;

/**
 * ViewModel MVVM do modulo Curso (Registro 21).
 * Substitui CursoComposer para os tres ZULs: list, form e view.
 */
public class CursoViewModel extends AbstractBaseViewModel {

    private static final int TAMANHO_PAGINA = 10;

    // --- Estado da listagem ---
    private String erroList;
    private boolean erroListVisivel;
    private String flashList;
    private boolean flashListVisivel;
    private String totalLabel;
    private String paginacaoLabel;
    private boolean anteriorDisabled;
    private boolean proximaDisabled;
    private int paginaAtual = 1;
    private int totalPaginas = 1;

    // Listbox construido programaticamente no @Init
    private Listbox lstCursos;

    // --- Estado do formulario ---
    private String erroForm;
    private boolean erroFormVisivel;
    private String tituloForm;
    private Long cursoIdEdicao;

    // Campos basicos
    private String codigoCursoEmec;
    private String nomeCurso;
    private String nivelAcademicoSelecionado;
    private String formatoOfertaSelecionado;
    private String teveAlunoVinculadoSelecionado;

    // Combos
    private List<Comboitem> itensNivelAcademico;
    private List<Comboitem> itensFormatoOferta;
    private List<Comboitem> itensTeveAlunoVinculado;

    // Campos dinamicos 1..N
    private Vbox boxRecursos;
    private Vbox boxCamposComplementares;
    private final Map<Long, Checkbox> checksRecursos = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Textbox> camposComplementares = new LinkedHashMap<Long, Textbox>();

    // --- Estado da visualizacao ---
    private String viewId;
    private String viewCodigo;
    private String viewNome;
    private String viewNivel;
    private String viewFormato;
    private String viewVinculado;
    private String viewRecursos;
    private Listbox lstViewCampos;
    private Long cursoIdVisualizacao;

    private CursoService cursoService() {
        return (CursoService) SpringUtil.getBean("cursoService");
    }

    private CatalogoService catalogoService() {
        return (CatalogoService) SpringUtil.getBean("catalogoService");
    }

    // =====================================================================
    // @Init
    // =====================================================================

    @Init
    public void init() {
        String req = currentRequest() != null ? currentRequest().getServletPath() : "";
        if (req.contains("curso-list")) {
            initList();
        } else if (req.contains("curso-form")) {
            initForm();
        } else if (req.contains("curso-view")) {
            initView();
        }
    }

    // --- Lista ---

    private void initList() {
        erroListVisivel = false;
        erroList = "";

        String flash = consumeFlash("flashCursoMessage");
        flashListVisivel = flash != null;
        flashList = flash != null ? flash : "";

        paginaAtual = parseIntOrDefault(
                currentRequest() != null ? currentRequest().getParameter("pagina") : null, 1);
        if (paginaAtual <= 0) {
            paginaAtual = 1;
        }

        lstCursos = new Listbox();
        lstCursos.setWidth("100%");
        lstCursos.setMold("paging");
        lstCursos.setPageSize(TAMANHO_PAGINA);

        Listhead head = new Listhead();
        head.setSizable(true);
        head.appendChild(new Listheader("ID", null, "70px"));
        head.appendChild(new Listheader("Codigo E-MEC", null, "110px"));
        head.appendChild(new Listheader("Nome", null, "260px"));
        head.appendChild(new Listheader("Nivel", null, "160px"));
        head.appendChild(new Listheader("Formato", null, "140px"));
        head.appendChild(new Listheader("Acoes", null, "300px"));
        lstCursos.appendChild(head);

        carregarLista();
    }

    private void carregarLista() {
        try {
            int total = cursoService().contar();
            totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
            if (paginaAtual > totalPaginas) {
                paginaAtual = totalPaginas;
            }

            lstCursos.getItems().clear();
            List<Curso> cursos = cursoService().listarPaginado(paginaAtual, TAMANHO_PAGINA);
            for (int i = 0; i < cursos.size(); i++) {
                adicionarLinhaCurso(cursos.get(i));
            }

            totalLabel = String.valueOf(total);
            paginacaoLabel = "Pagina " + paginaAtual + " de " + totalPaginas;
            anteriorDisabled = paginaAtual <= 1;
            proximaDisabled = paginaAtual >= totalPaginas;
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao carregar listagem de curso.";
        }
    }

    private void adicionarLinhaCurso(final Curso curso) {
        Listitem item = new Listitem();
        item.appendChild(new Listcell(safe(curso.getId())));
        item.appendChild(new Listcell(safe(curso.getCodigoCursoEmec())));
        item.appendChild(new Listcell(safe(curso.getNome())));
        item.appendChild(new Listcell(safe(curso.getNivelAcademico())));
        item.appendChild(new Listcell(safe(curso.getFormatoOferta())));

        Hbox acoes = new Hbox();
        acoes.setSpacing("6px");

        Toolbarbutton btnAlterar = new Toolbarbutton("Alterar");
        btnAlterar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        openSub("curso-list", "curso-form", curso.getId());
                    }
                });
        acoes.appendChild(btnAlterar);

        Toolbarbutton btnMostrar = new Toolbarbutton("Mostrar");
        btnMostrar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        openSub("curso-list", "curso-view", curso.getId());
                    }
                });
        acoes.appendChild(btnMostrar);

        Toolbarbutton btnExcluir = new Toolbarbutton("Excluir");
        btnExcluir.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        excluirCurso(curso.getId());
                    }
                });
        acoes.appendChild(btnExcluir);

        Toolbarbutton btnExportar = new Toolbarbutton("Exportar TXT");
        btnExportar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        redirect("/api/relatorios/cursos/" + curso.getId() + ".txt");
                    }
                });
        acoes.appendChild(btnExportar);

        Listcell celulaAcoes = new Listcell();
        celulaAcoes.appendChild(acoes);
        item.appendChild(celulaAcoes);
        lstCursos.appendChild(item);
    }

    private void excluirCurso(Long id) {
        try {
            cursoService().excluir(id);
            flashListVisivel = true;
            flashList = "Curso excluido com sucesso.";
            carregarLista();
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao excluir curso.";
        }
    }

    // --- Formulario ---

    private void initForm() {
        cursoIdEdicao = parseLongOrNull(currentRequest() != null ? currentRequest().getParameter("id") : null);
        erroFormVisivel = false;
        erroForm = "";

        try {
            Curso curso = cursoIdEdicao != null ? cursoService().buscarPorId(cursoIdEdicao) : new Curso();
            if (curso == null) {
                curso = new Curso();
                cursoIdEdicao = null;
            }

            tituloForm = cursoIdEdicao == null ? "Novo Curso" : "Alterar Curso";

            codigoCursoEmec = safe(curso.getCodigoCursoEmec());
            nomeCurso = safe(curso.getNome());

            itensNivelAcademico = montarItensNivelAcademico();
            nivelAcademicoSelecionado = curso.getNivelAcademico();

            itensFormatoOferta = montarItensFormatoOferta();
            formatoOfertaSelecionado = curso.getFormatoOferta();

            itensTeveAlunoVinculado = montarItensTeveAlunoVinculado();
            teveAlunoVinculadoSelecionado = curso.getCursoTeveAlunoVinculado() == null
                    ? "1" : String.valueOf(curso.getCursoTeveAlunoVinculado());

            boxRecursos = new Vbox();
            boxRecursos.setSpacing("6px");
            popularRecursos(cursoIdEdicao);

            boxCamposComplementares = new Vbox();
            boxCamposComplementares.setSpacing("6px");
            popularCamposComplementares(cursoIdEdicao);
        } catch (Exception e) {
            erroFormVisivel = true;
            erroForm = "Falha ao carregar formulario de curso.";
        }
    }

    private List<Comboitem> montarItensNivelAcademico() {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        for (NivelAcademicoEnum nivel : NivelAcademicoEnum.values()) {
            Comboitem item = new Comboitem(nivel.getDescricao());
            item.setValue(nivel.getCodigo());
            itens.add(item);
        }
        return itens;
    }

    private List<Comboitem> montarItensFormatoOferta() {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        for (FormatoOfertaEnum formato : FormatoOfertaEnum.values()) {
            Comboitem item = new Comboitem(formato.getDescricao());
            item.setValue(formato.getCodigo());
            itens.add(item);
        }
        return itens;
    }

    private List<Comboitem> montarItensTeveAlunoVinculado() {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        Comboitem sim = new Comboitem("1 - Sim");
        sim.setValue("1");
        itens.add(sim);
        Comboitem nao = new Comboitem("0 - Nao");
        nao.setValue("0");
        itens.add(nao);
        return itens;
    }

    private void popularRecursos(Long cursoId) throws Exception {
        checksRecursos.clear();
        Set<Long> selecionados = new HashSet<Long>();
        if (cursoId != null) {
            selecionados.addAll(cursoService().listarOpcaoRecursoAssistivoIds(cursoId));
        }
        List<OpcaoDominio> opcoes = catalogoService().listarOpcoesPorCategoria(
                CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA);
        for (int i = 0; i < opcoes.size(); i++) {
            OpcaoDominio opcao = opcoes.get(i);
            Checkbox check = new Checkbox(opcao.getNome());
            check.setChecked(selecionados.contains(opcao.getId()));
            checksRecursos.put(opcao.getId(), check);
            boxRecursos.appendChild(check);
        }
    }

    private void popularCamposComplementares(Long cursoId) throws Exception {
        camposComplementares.clear();
        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (cursoId != null) {
            valores.putAll(cursoService().carregarCamposComplementaresPorCampoId(cursoId));
        }
        List<LayoutCampo> campos = filtrarCamposComplementares(
                catalogoService().listarCamposModulo(ModulosLayout.CURSO_21));
        for (int i = 0; i < campos.size(); i++) {
            LayoutCampo campo = campos.get(i);
            Hbox linha = new Hbox();
            linha.setSpacing("6px");
            Label label = new Label("[" + campo.getNumeroCampo() + "] " + campo.getNomeCampo());
            label.setWidth("360px");
            Textbox textbox = new Textbox();
            textbox.setWidth("420px");
            String valorAtual = valores.get(campo.getId());
            textbox.setValue(valorAtual == null ? "" : valorAtual);
            linha.appendChild(label);
            linha.appendChild(textbox);
            boxCamposComplementares.appendChild(linha);
            camposComplementares.put(campo.getId(), textbox);
        }
    }

    private List<LayoutCampo> filtrarCamposComplementares(List<LayoutCampo> campos) {
        List<LayoutCampo> filtrados = new ArrayList<LayoutCampo>();
        if (campos == null) {
            return filtrados;
        }
        for (int i = 0; i < campos.size(); i++) {
            LayoutCampo campo = campos.get(i);
            int numero = campo.getNumeroCampo().intValue();
            if (numero == 1 || numero == 2 || numero == 3 || (numero >= 54 && numero <= 65)) {
                continue;
            }
            filtrados.add(campo);
        }
        return filtrados;
    }

    // --- Visualizacao ---

    private void initView() {
        cursoIdVisualizacao = parseLongOrNull(currentRequest() != null ? currentRequest().getParameter("id") : null);
        if (cursoIdVisualizacao == null) {
            goShell("curso-list");
            return;
        }
        try {
            Curso curso = cursoService().buscarPorId(cursoIdVisualizacao);
            if (curso == null) {
                goShell("curso-list");
                return;
            }
            viewId = safe(curso.getId());
            viewCodigo = safe(curso.getCodigoCursoEmec());
            viewNome = safe(curso.getNome());
            viewNivel = curso.getNivelAcademicoEnum() != null
                    ? curso.getNivelAcademicoEnum().getDescricao() + " (" + curso.getNivelAcademicoEnum().getCodigo() + ")"
                    : safe(curso.getNivelAcademico());
            viewFormato = curso.getFormatoOfertaEnum() != null
                    ? curso.getFormatoOfertaEnum().getDescricao() + " (" + curso.getFormatoOfertaEnum().getCodigo() + ")"
                    : safe(curso.getFormatoOferta());
            viewVinculado = safe(curso.getCursoTeveAlunoVinculado());
            viewRecursos = safe(curso.getRecursosTecnologiaAssistivaResumo());

            lstViewCampos = new Listbox();
            lstViewCampos.setWidth("100%");
            Listhead head = new Listhead();
            head.appendChild(new Listheader("Campo", null, "55%"));
            head.appendChild(new Listheader("Valor", null, "45%"));
            lstViewCampos.appendChild(head);
            preencherCamposView(cursoIdVisualizacao);
        } catch (Exception e) {
            goShell("curso-list");
        }
    }

    private void preencherCamposView(Long cursoId) throws Exception {
        Map<Long, String> valores = cursoService().carregarCamposComplementaresPorCampoId(cursoId);
        Map<Long, String> rotulos = montarRotulosCampos(ModulosLayout.CURSO_21);
        if (valores == null || valores.isEmpty()) {
            Listitem vazio = new Listitem();
            vazio.appendChild(new Listcell("Nenhum campo complementar informado."));
            vazio.appendChild(new Listcell(""));
            lstViewCampos.appendChild(vazio);
            return;
        }
        for (Map.Entry<Long, String> entry : valores.entrySet()) {
            Listitem item = new Listitem();
            String rotulo = rotulos.get(entry.getKey());
            item.appendChild(new Listcell(rotulo != null ? rotulo : String.valueOf(entry.getKey())));
            item.appendChild(new Listcell(safe(entry.getValue())));
            lstViewCampos.appendChild(item);
        }
    }

    private Map<Long, String> montarRotulosCampos(String modulo) throws Exception {
        Map<Long, String> rotulos = new LinkedHashMap<Long, String>();
        List<LayoutCampo> campos = catalogoService().listarCamposModulo(modulo);
        if (campos == null) {
            return rotulos;
        }
        for (int i = 0; i < campos.size(); i++) {
            LayoutCampo campo = campos.get(i);
            rotulos.put(campo.getId(), "[" + campo.getNumeroCampo() + "] " + campo.getNomeCampo());
        }
        return rotulos;
    }

    // =====================================================================
    // @Commands
    // =====================================================================

    @Command
    public void novo() {
        openSub("curso-list", "curso-form");
    }

    @Command
    public void exportarTodos() {
        redirect("/api/relatorios/cursos.txt");
    }

    @Command
    @NotifyChange({"lstCursos","erroList","erroListVisivel","flashList","flashListVisivel",
                   "totalLabel","paginacaoLabel","anteriorDisabled","proximaDisabled"})
    public void importar(@BindingParam("conteudo") String conteudo) {
        try {
            int total = cursoService().importarTxtPipe(conteudo);
            putFlash("flashHomeMessage", "Importacao de curso concluida: " + total + " registro(s).");
            putFlash("flashCursoMessage", "Importacao concluida: " + total + " registro(s).");
            goShell("curso-list");
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao importar TXT de curso.";
        }
    }

    @Command
    public void paginaAnterior() {
        if (paginaAtual > 1) {
            goShell("curso-list", paginaAtual - 1);
        }
    }

    @Command
    public void proximaPagina() {
        if (paginaAtual < totalPaginas) {
            goShell("curso-list", paginaAtual + 1);
        }
    }

    @Command
    public void voltar() {
        goShell("curso-list");
    }

    @Command
    @NotifyChange({"erroForm","erroFormVisivel"})
    public void salvar() {
        erroFormVisivel = false;
        erroForm = "";
        try {
            Curso curso = Curso.builder()
                    .id(cursoIdEdicao)
                    .codigoCursoEmec(trimToEmpty(codigoCursoEmec))
                    .nome(trimToEmpty(nomeCurso))
                    .nivelAcademico(trimToNull(nivelAcademicoSelecionado))
                    .formatoOferta(trimToNull(formatoOfertaSelecionado))
                    .cursoTeveAlunoVinculado(Integer.valueOf(
                            parseIntOrDefault(teveAlunoVinculadoSelecionado, 1)))
                    .build();

            long[] recursos = mapSelectedIds(checksRecursos);
            Map<Long, String> extras = mapCamposComplementares(camposComplementares);

            if (cursoIdEdicao == null) {
                cursoService().cadastrar(curso, recursos, extras);
                putFlash("flashCursoMessage", "Curso incluido com sucesso.");
            } else {
                cursoService().atualizar(curso, recursos, extras);
                putFlash("flashCursoMessage", "Curso alterado com sucesso.");
            }
            goShell("curso-list");
        } catch (Exception e) {
            erroFormVisivel = true;
            erroForm = e.getMessage() != null ? e.getMessage() : "Falha ao salvar curso.";
        }
    }

    @Command
    public void voltarView() {
        goShell("curso-list");
    }

    @Command
    public void editarView() {
        openSub("curso-list", "curso-form", cursoIdVisualizacao);
    }

    // =====================================================================
    // Getters / Setters
    // =====================================================================

    public String getErroList()             { return erroList; }
    public boolean isErroListVisivel()      { return erroListVisivel; }
    public String getFlashList()            { return flashList; }
    public boolean isFlashListVisivel()     { return flashListVisivel; }
    public String getTotalLabel()           { return totalLabel; }
    public String getPaginacaoLabel()       { return paginacaoLabel; }
    public boolean isAnteriorDisabled()     { return anteriorDisabled; }
    public boolean isProximaDisabled()      { return proximaDisabled; }
    public Listbox getLstCursos()           { return lstCursos; }

    public String getErroForm()             { return erroForm; }
    public boolean isErroFormVisivel()      { return erroFormVisivel; }
    public String getTituloForm()           { return tituloForm; }

    public String getCodigoCursoEmec()      { return codigoCursoEmec; }
    public void   setCodigoCursoEmec(String v){ this.codigoCursoEmec = v; }
    public String getNomeCurso()            { return nomeCurso; }
    public void   setNomeCurso(String v)    { this.nomeCurso = v; }

    public List<Comboitem> getItensNivelAcademico()        { return itensNivelAcademico; }
    public String getNivelAcademicoSelecionado()           { return nivelAcademicoSelecionado; }
    public void   setNivelAcademicoSelecionado(String v)   { this.nivelAcademicoSelecionado = v; }

    public List<Comboitem> getItensFormatoOferta()         { return itensFormatoOferta; }
    public String getFormatoOfertaSelecionado()            { return formatoOfertaSelecionado; }
    public void   setFormatoOfertaSelecionado(String v)    { this.formatoOfertaSelecionado = v; }

    public List<Comboitem> getItensTeveAlunoVinculado()    { return itensTeveAlunoVinculado; }
    public String getTeveAlunoVinculadoSelecionado()       { return teveAlunoVinculadoSelecionado; }
    public void   setTeveAlunoVinculadoSelecionado(String v){ this.teveAlunoVinculadoSelecionado = v; }

    public Vbox getBoxRecursos()                 { return boxRecursos; }
    public Vbox getBoxCamposComplementares()     { return boxCamposComplementares; }

    public String getViewId()               { return viewId; }
    public String getViewCodigo()           { return viewCodigo; }
    public String getViewNome()             { return viewNome; }
    public String getViewNivel()            { return viewNivel; }
    public String getViewFormato()          { return viewFormato; }
    public String getViewVinculado()        { return viewVinculado; }
    public String getViewRecursos()         { return viewRecursos; }
    public Listbox getLstViewCampos()       { return lstViewCampos; }
}
