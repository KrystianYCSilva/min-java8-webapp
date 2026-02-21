package br.gov.inep.censo.web.zk.modulo;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Ies;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.enums.EstadoEnum;
import br.gov.inep.censo.model.enums.TipoLaboratorioEnum;
import br.gov.inep.censo.service.CatalogoService;
import br.gov.inep.censo.service.IesService;
import br.gov.inep.censo.web.zk.AbstractBaseViewModel;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.*;

import java.util.*;

/**
 * ViewModel MVVM do modulo IES (Registro 11).
 * Substitui IesComposer para os tres ZULs: list, form e view.
 */
public class IesViewModel extends AbstractBaseViewModel {

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

    private Listbox lstIes;

    // --- Estado do formulario ---
    private String erroForm;
    private boolean erroFormVisivel;
    private String tituloForm;
    private Long iesIdEdicao;

    // Campos basicos
    private String idIesInep;
    private String nomeLaboratorio;
    private String registroLaboratorio;
    private String descricaoAtividades;
    private String palavrasChave;
    private String municipioLaboratorio;

    // Combos
    private List<Comboitem> itensAtivoAno;
    private String ativoAnoSelecionado;
    private List<Comboitem> itensLaboratorioInformatica;
    private String laboratorioInformaticaSelecionado;
    private List<Comboitem> itensTipoLaboratorio;
    private String tipoLaboratorioSelecionado;
    private List<Comboitem> itensUfLaboratorio;
    private String ufLaboratorioSelecionado;

    // Campos complementares dinamicos
    private Vbox boxCamposComplementares;
    private final Map<Long, Textbox> camposComplementares = new LinkedHashMap<Long, Textbox>();

    // --- Estado da visualizacao ---
    private String viewId;
    private String viewIdInep;
    private String viewNomeLaboratorio;
    private String viewRegistro;
    private String viewAtivo;
    private String viewDescricao;
    private String viewPalavras;
    private String viewLabInfo;
    private String viewTipo;
    private String viewUf;
    private String viewMunicipio;
    private Listbox lstViewCampos;
    private Long iesIdVisualizacao;

    private IesService iesService() {
        return (IesService) SpringUtil.getBean("iesService");
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
        if (req.contains("ies-list")) {
            initList();
        } else if (req.contains("ies-form")) {
            initForm();
        } else if (req.contains("ies-view")) {
            initView();
        }
    }

    // --- Lista ---

    private void initList() {
        erroListVisivel = false;
        erroList = "";

        String flash = consumeFlash("flashIesMessage");
        flashListVisivel = flash != null;
        flashList = flash != null ? flash : "";

        paginaAtual = parseIntOrDefault(
                currentRequest() != null ? currentRequest().getParameter("pagina") : null, 1);
        if (paginaAtual <= 0) {
            paginaAtual = 1;
        }

        lstIes = new Listbox();
        lstIes.setWidth("100%");
        lstIes.setMold("paging");
        lstIes.setPageSize(TAMANHO_PAGINA);

        Listhead head = new Listhead();
        head.setSizable(true);
        head.appendChild(new Listheader("ID", null, "70px"));
        head.appendChild(new Listheader("ID INEP", null, "90px"));
        head.appendChild(new Listheader("Nome Laboratorio", null, "220px"));
        head.appendChild(new Listheader("Registro", null, "100px"));
        head.appendChild(new Listheader("Tipo", null, "150px"));
        head.appendChild(new Listheader("UF", null, "80px"));
        head.appendChild(new Listheader("Municipio", null, "100px"));
        head.appendChild(new Listheader("Acoes", null, "300px"));
        lstIes.appendChild(head);

        carregarLista();
    }

    private void carregarLista() {
        try {
            int total = iesService().contar();
            totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
            if (paginaAtual > totalPaginas) {
                paginaAtual = totalPaginas;
            }

            lstIes.getItems().clear();
            List<Ies> itens = iesService().listarPaginado(paginaAtual, TAMANHO_PAGINA);
            for (int i = 0; i < itens.size(); i++) {
                adicionarLinhaIes(itens.get(i));
            }

            totalLabel = String.valueOf(total);
            paginacaoLabel = "Pagina " + paginaAtual + " de " + totalPaginas;
            anteriorDisabled = paginaAtual <= 1;
            proximaDisabled = paginaAtual >= totalPaginas;
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao carregar listagem de IES.";
        }
    }

    private void adicionarLinhaIes(final Ies item) {
        Listitem linha = new Listitem();
        linha.appendChild(new Listcell(safe(item.getId())));
        linha.appendChild(new Listcell(safe(item.getIdIesInep())));
        linha.appendChild(new Listcell(safe(item.getNomeLaboratorio())));
        linha.appendChild(new Listcell(safe(item.getRegistroLaboratorioIes())));
        linha.appendChild(new Listcell(item.getTipoLaboratorioEnum() != null
                ? item.getTipoLaboratorioEnum().getDescricao() : "-"));
        linha.appendChild(new Listcell(item.getUfLaboratorioEnum() != null
                ? item.getUfLaboratorioEnum().getDescricao() : "-"));
        linha.appendChild(new Listcell(safe(item.getCodigoMunicipioLaboratorio())));

        Hbox acoes = new Hbox();
        acoes.setSpacing("6px");

        Toolbarbutton btnAlterar = new Toolbarbutton("Alterar");
        btnAlterar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        openSub("ies-list", "ies-form", item.getId());
                    }
                });
        acoes.appendChild(btnAlterar);

        Toolbarbutton btnMostrar = new Toolbarbutton("Mostrar");
        btnMostrar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        openSub("ies-list", "ies-view", item.getId());
                    }
                });
        acoes.appendChild(btnMostrar);

        Toolbarbutton btnExcluir = new Toolbarbutton("Excluir");
        btnExcluir.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        excluirIes(item.getId());
                    }
                });
        acoes.appendChild(btnExcluir);

        Toolbarbutton btnExportar = new Toolbarbutton("Exportar TXT");
        btnExportar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        redirect("/api/relatorios/ies/" + item.getId() + ".txt");
                    }
                });
        acoes.appendChild(btnExportar);

        Listcell celulaAcoes = new Listcell();
        celulaAcoes.appendChild(acoes);
        linha.appendChild(celulaAcoes);
        lstIes.appendChild(linha);
    }

    private void excluirIes(Long id) {
        try {
            iesService().excluir(id);
            flashListVisivel = true;
            flashList = "IES excluida com sucesso.";
            carregarLista();
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao excluir IES.";
        }
    }

    // --- Formulario ---

    private void initForm() {
        iesIdEdicao = parseLongOrNull(currentRequest() != null ? currentRequest().getParameter("id") : null);
        erroFormVisivel = false;
        erroForm = "";
        try {
            Ies ies = iesIdEdicao != null ? iesService().buscarPorId(iesIdEdicao) : new Ies();
            if (ies == null) {
                ies = new Ies();
                iesIdEdicao = null;
            }

            tituloForm = iesIdEdicao == null ? "Nova IES" : "Alterar IES";

            idIesInep = safe(ies.getIdIesInep());
            nomeLaboratorio = safe(ies.getNomeLaboratorio());
            registroLaboratorio = safe(ies.getRegistroLaboratorioIes());
            descricaoAtividades = safe(ies.getDescricaoAtividades());
            palavrasChave = safe(ies.getPalavrasChave());
            municipioLaboratorio = safe(ies.getCodigoMunicipioLaboratorio());

            itensAtivoAno = montarItensAtivoAno();
            ativoAnoSelecionado = ies.getLaboratorioAtivoAno() == null ? "1" : String.valueOf(ies.getLaboratorioAtivoAno());

            itensLaboratorioInformatica = montarItensLaboratorioInformatica();
            laboratorioInformaticaSelecionado = ies.getLaboratorioInformatica() == null
                    ? null : String.valueOf(ies.getLaboratorioInformatica());

            itensTipoLaboratorio = montarItensTipoLaboratorio();
            tipoLaboratorioSelecionado = ies.getTipoLaboratorio() == null
                    ? null : String.valueOf(ies.getTipoLaboratorio());

            itensUfLaboratorio = montarItensUfLaboratorio();
            ufLaboratorioSelecionado = ies.getCodigoUfLaboratorio() == null
                    ? null : String.valueOf(ies.getCodigoUfLaboratorio());

            boxCamposComplementares = new Vbox();
            boxCamposComplementares.setSpacing("6px");
            popularCamposComplementares(iesIdEdicao);
        } catch (Exception e) {
            erroFormVisivel = true;
            erroForm = "Falha ao carregar formulario de IES.";
        }
    }

    private List<Comboitem> montarItensAtivoAno() {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        Comboitem sim = new Comboitem("1 - Sim");
        sim.setValue("1");
        itens.add(sim);
        Comboitem nao = new Comboitem("0 - Nao");
        nao.setValue("0");
        itens.add(nao);
        return itens;
    }

    private List<Comboitem> montarItensLaboratorioInformatica() {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        Comboitem vazio = new Comboitem("Nao informado");
        vazio.setValue("");
        itens.add(vazio);
        Comboitem sim = new Comboitem("1 - Sim");
        sim.setValue("1");
        itens.add(sim);
        Comboitem nao = new Comboitem("0 - Nao");
        nao.setValue("0");
        itens.add(nao);
        return itens;
    }

    private List<Comboitem> montarItensTipoLaboratorio() {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        Comboitem vazio = new Comboitem("Nao informado");
        vazio.setValue("");
        itens.add(vazio);
        for (TipoLaboratorioEnum tipo : TipoLaboratorioEnum.values()) {
            Comboitem item = new Comboitem(tipo.getCodigo() + " - " + tipo.getDescricao());
            item.setValue(String.valueOf(tipo.getCodigo()));
            itens.add(item);
        }
        return itens;
    }

    private List<Comboitem> montarItensUfLaboratorio() {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        Comboitem vazio = new Comboitem("Nao informado");
        vazio.setValue("");
        itens.add(vazio);
        for (EstadoEnum uf : EstadoEnum.values()) {
            Comboitem item = new Comboitem(uf.getCodigo() + " - " + uf.getDescricao());
            item.setValue(String.valueOf(uf.getCodigo()));
            itens.add(item);
        }
        return itens;
    }

    private void popularCamposComplementares(Long iesId) throws Exception {
        camposComplementares.clear();
        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (iesId != null) {
            valores.putAll(iesService().carregarCamposComplementaresPorCampoId(iesId));
        }
        List<LayoutCampo> campos = filtrarCamposComplementares(
                catalogoService().listarCamposModulo(ModulosLayout.IES_11));
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
            if ((numero >= 1 && numero <= 7) || numero == 17 || numero == 18 || numero == 27 || numero == 28) {
                continue;
            }
            filtrados.add(campo);
        }
        return filtrados;
    }

    // --- Visualizacao ---

    private void initView() {
        iesIdVisualizacao = parseLongOrNull(currentRequest() != null ? currentRequest().getParameter("id") : null);
        if (iesIdVisualizacao == null) {
            goShell("ies-list");
            return;
        }
        try {
            Ies ies = iesService().buscarPorId(iesIdVisualizacao);
            if (ies == null) {
                goShell("ies-list");
                return;
            }
            viewId = safe(ies.getId());
            viewIdInep = safe(ies.getIdIesInep());
            viewNomeLaboratorio = safe(ies.getNomeLaboratorio());
            viewRegistro = safe(ies.getRegistroLaboratorioIes());
            viewAtivo = safe(ies.getLaboratorioAtivoAno());
            viewDescricao = safe(ies.getDescricaoAtividades());
            viewPalavras = safe(ies.getPalavrasChave());
            viewLabInfo = safe(ies.getLaboratorioInformatica());
            viewTipo = ies.getTipoLaboratorioEnum() != null
                    ? ies.getTipoLaboratorioEnum().getDescricao() + " (" + ies.getTipoLaboratorio() + ")"
                    : safe(ies.getTipoLaboratorio());
            viewUf = ies.getUfLaboratorioEnum() != null
                    ? ies.getUfLaboratorioEnum().getDescricao() + " (" + ies.getCodigoUfLaboratorio() + ")"
                    : safe(ies.getCodigoUfLaboratorio());
            viewMunicipio = safe(ies.getCodigoMunicipioLaboratorio());

            lstViewCampos = new Listbox();
            lstViewCampos.setWidth("100%");
            Listhead head = new Listhead();
            head.appendChild(new Listheader("Campo", null, "55%"));
            head.appendChild(new Listheader("Valor", null, "45%"));
            lstViewCampos.appendChild(head);
            preencherCamposView(iesIdVisualizacao);
        } catch (Exception e) {
            goShell("ies-list");
        }
    }

    private void preencherCamposView(Long iesId) throws Exception {
        Map<Long, String> valores = iesService().carregarCamposComplementaresPorCampoId(iesId);
        Map<Long, String> rotulos = montarRotulosCampos(ModulosLayout.IES_11);
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
        openSub("ies-list", "ies-form");
    }

    @Command
    public void exportarTodos() {
        redirect("/api/relatorios/ies.txt");
    }

    @Command
    @NotifyChange({"lstIes","erroList","erroListVisivel","flashList","flashListVisivel",
                   "totalLabel","paginacaoLabel","anteriorDisabled","proximaDisabled"})
    public void importar(@org.zkoss.bind.annotation.BindingParam("conteudo") String conteudo) {
        try {
            int total = iesService().importarTxtPipe(conteudo);
            putFlash("flashHomeMessage", "Importacao de IES concluida: " + total + " registro(s).");
            putFlash("flashIesMessage", "Importacao concluida: " + total + " registro(s).");
            goShell("ies-list");
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao importar TXT de IES.";
        }
    }

    @Command
    public void paginaAnterior() {
        if (paginaAtual > 1) {
            goShell("ies-list", paginaAtual - 1);
        }
    }

    @Command
    public void proximaPagina() {
        if (paginaAtual < totalPaginas) {
            goShell("ies-list", paginaAtual + 1);
        }
    }

    @Command
    public void voltar() {
        goShell("ies-list");
    }

    @Command
    @NotifyChange({"erroForm","erroFormVisivel"})
    public void salvar() {
        erroFormVisivel = false;
        erroForm = "";
        try {
            Ies ies = Ies.builder()
                    .id(iesIdEdicao)
                    .idIesInep(parseLongOrNull(idIesInep))
                    .nomeLaboratorio(trimToEmpty(nomeLaboratorio))
                    .registroLaboratorioIes(trimToEmpty(registroLaboratorio))
                    .laboratorioAtivoAno(Integer.valueOf(parseIntOrDefault(ativoAnoSelecionado, 1)))
                    .descricaoAtividades(trimToEmpty(descricaoAtividades))
                    .palavrasChave(trimToEmpty(palavrasChave))
                    .laboratorioInformatica(parseIntegerOrNull(laboratorioInformaticaSelecionado))
                    .tipoLaboratorio(parseIntegerOrNull(tipoLaboratorioSelecionado))
                    .codigoUfLaboratorio(parseIntegerOrNull(ufLaboratorioSelecionado))
                    .codigoMunicipioLaboratorio(trimToEmpty(municipioLaboratorio))
                    .build();

            Map<Long, String> extras = mapCamposComplementares(camposComplementares);

            if (iesIdEdicao == null) {
                iesService().cadastrar(ies, extras);
                putFlash("flashIesMessage", "IES incluida com sucesso.");
            } else {
                iesService().atualizar(ies, extras);
                putFlash("flashIesMessage", "IES alterada com sucesso.");
            }
            goShell("ies-list");
        } catch (Exception e) {
            erroFormVisivel = true;
            erroForm = e.getMessage() != null ? e.getMessage() : "Falha ao salvar IES.";
        }
    }

    @Command
    public void voltarView() {
        goShell("ies-list");
    }

    @Command
    public void editarView() {
        openSub("ies-list", "ies-form", iesIdVisualizacao);
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
    public Listbox getLstIes()              { return lstIes; }

    public String getErroForm()             { return erroForm; }
    public boolean isErroFormVisivel()      { return erroFormVisivel; }
    public String getTituloForm()           { return tituloForm; }

    public String getIdIesInep()            { return idIesInep; }
    public void   setIdIesInep(String v)    { this.idIesInep = v; }
    public String getNomeLaboratorio()      { return nomeLaboratorio; }
    public void   setNomeLaboratorio(String v){ this.nomeLaboratorio = v; }
    public String getRegistroLaboratorio()  { return registroLaboratorio; }
    public void   setRegistroLaboratorio(String v){ this.registroLaboratorio = v; }
    public String getDescricaoAtividades()  { return descricaoAtividades; }
    public void   setDescricaoAtividades(String v){ this.descricaoAtividades = v; }
    public String getPalavrasChave()        { return palavrasChave; }
    public void   setPalavrasChave(String v){ this.palavrasChave = v; }
    public String getMunicipioLaboratorio() { return municipioLaboratorio; }
    public void   setMunicipioLaboratorio(String v){ this.municipioLaboratorio = v; }

    public List<Comboitem> getItensAtivoAno()          { return itensAtivoAno; }
    public String getAtivoAnoSelecionado()             { return ativoAnoSelecionado; }
    public void   setAtivoAnoSelecionado(String v)     { this.ativoAnoSelecionado = v; }

    public List<Comboitem> getItensLaboratorioInformatica()      { return itensLaboratorioInformatica; }
    public String getLaboratorioInformaticaSelecionado()         { return laboratorioInformaticaSelecionado; }
    public void   setLaboratorioInformaticaSelecionado(String v) { this.laboratorioInformaticaSelecionado = v; }

    public List<Comboitem> getItensTipoLaboratorio()   { return itensTipoLaboratorio; }
    public String getTipoLaboratorioSelecionado()      { return tipoLaboratorioSelecionado; }
    public void   setTipoLaboratorioSelecionado(String v){ this.tipoLaboratorioSelecionado = v; }

    public List<Comboitem> getItensUfLaboratorio()     { return itensUfLaboratorio; }
    public String getUfLaboratorioSelecionado()        { return ufLaboratorioSelecionado; }
    public void   setUfLaboratorioSelecionado(String v){ this.ufLaboratorioSelecionado = v; }

    public Vbox getBoxCamposComplementares()           { return boxCamposComplementares; }

    public String getViewId()               { return viewId; }
    public String getViewIdInep()           { return viewIdInep; }
    public String getViewNomeLaboratorio()  { return viewNomeLaboratorio; }
    public String getViewRegistro()         { return viewRegistro; }
    public String getViewAtivo()            { return viewAtivo; }
    public String getViewDescricao()        { return viewDescricao; }
    public String getViewPalavras()         { return viewPalavras; }
    public String getViewLabInfo()          { return viewLabInfo; }
    public String getViewTipo()             { return viewTipo; }
    public String getViewUf()               { return viewUf; }
    public String getViewMunicipio()        { return viewMunicipio; }
    public Listbox getLstViewCampos()       { return lstViewCampos; }
}
