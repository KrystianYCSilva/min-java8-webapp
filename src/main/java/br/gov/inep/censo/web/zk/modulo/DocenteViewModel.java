package br.gov.inep.censo.web.zk.modulo;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Docente;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.enums.CorRacaEnum;
import br.gov.inep.censo.model.enums.EstadoEnum;
import br.gov.inep.censo.model.enums.NacionalidadeEnum;
import br.gov.inep.censo.model.enums.PaisEnum;
import br.gov.inep.censo.service.CatalogoService;
import br.gov.inep.censo.service.DocenteService;
import br.gov.inep.censo.web.zk.AbstractBaseViewModel;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.*;

import java.sql.Date;
import java.util.*;

/**
 * ViewModel MVVM do modulo Docente (Registro 31).
 * Substitui DocenteComposer para os tres ZULs: list, form e view.
 */
public class DocenteViewModel extends AbstractBaseViewModel {

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

    private Listbox lstDocentes;

    // --- Estado do formulario ---
    private String erroForm;
    private boolean erroFormVisivel;
    private String tituloForm;
    private Long docenteIdEdicao;

    // Campos basicos
    private String idDocenteIes;
    private String nomeDocente;
    private String cpfDocente;
    private String documentoEstrangeiro;
    private java.util.Date dataNascimento;
    private String municipioNascimento;

    // Combos
    private List<Comboitem> itensCorRaca;
    private String corRacaSelecionada;
    private List<Comboitem> itensNacionalidade;
    private String nacionalidadeSelecionada;
    private List<Comboitem> itensPaisOrigem;
    private String paisOrigemSelecionado;
    private List<Comboitem> itensUfNascimento;
    private String ufNascimentoSelecionado;
    private List<Comboitem> itensDeficiencia;
    private String deficienciaSelecionada;

    // Campos complementares dinamicos
    private Vbox boxCamposComplementares;
    private final Map<Long, Textbox> camposComplementares = new LinkedHashMap<Long, Textbox>();

    // --- Estado da visualizacao ---
    private String viewId;
    private String viewIdIes;
    private String viewNome;
    private String viewCpf;
    private String viewDocumento;
    private String viewNascimento;
    private String viewCorRaca;
    private String viewNacionalidade;
    private String viewPais;
    private String viewUf;
    private String viewMunicipio;
    private String viewDeficiencia;
    private Listbox lstViewCampos;
    private Long docenteIdVisualizacao;

    private DocenteService docenteService() {
        return (DocenteService) SpringUtil.getBean("docenteService");
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
        if (req.contains("docente-list")) {
            initList();
        } else if (req.contains("docente-form")) {
            initForm();
        } else if (req.contains("docente-view")) {
            initView();
        }
    }

    // --- Lista ---

    private void initList() {
        erroListVisivel = false;
        erroList = "";

        String flash = consumeFlash("flashDocenteMessage");
        flashListVisivel = flash != null;
        flashList = flash != null ? flash : "";

        paginaAtual = parseIntOrDefault(
                currentRequest() != null ? currentRequest().getParameter("pagina") : null, 1);
        if (paginaAtual <= 0) {
            paginaAtual = 1;
        }

        lstDocentes = new Listbox();
        lstDocentes.setWidth("100%");
        lstDocentes.setMold("paging");
        lstDocentes.setPageSize(TAMANHO_PAGINA);

        Listhead head = new Listhead();
        head.setSizable(true);
        head.appendChild(new Listheader("ID", null, "70px"));
        head.appendChild(new Listheader("ID IES", null, "90px"));
        head.appendChild(new Listheader("Nome", null, "260px"));
        head.appendChild(new Listheader("CPF", null, "110px"));
        head.appendChild(new Listheader("Nascimento", null, "120px"));
        head.appendChild(new Listheader("Nacionalidade", null, "120px"));
        head.appendChild(new Listheader("Acoes", null, "300px"));
        lstDocentes.appendChild(head);

        carregarLista();
    }

    private void carregarLista() {
        try {
            int total = docenteService().contar();
            totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
            if (paginaAtual > totalPaginas) {
                paginaAtual = totalPaginas;
            }

            lstDocentes.getItems().clear();
            List<Docente> docentes = docenteService().listarPaginado(paginaAtual, TAMANHO_PAGINA);
            for (int i = 0; i < docentes.size(); i++) {
                adicionarLinhaDocente(docentes.get(i));
            }

            totalLabel = String.valueOf(total);
            paginacaoLabel = "Pagina " + paginaAtual + " de " + totalPaginas;
            anteriorDisabled = paginaAtual <= 1;
            proximaDisabled = paginaAtual >= totalPaginas;
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao carregar listagem de docente.";
        }
    }

    private void adicionarLinhaDocente(final Docente docente) {
        Listitem item = new Listitem();
        item.appendChild(new Listcell(safe(docente.getId())));
        item.appendChild(new Listcell(safe(docente.getIdDocenteIes())));
        item.appendChild(new Listcell(safe(docente.getNome())));
        item.appendChild(new Listcell(safe(docente.getCpf())));
        item.appendChild(new Listcell(safe(docente.getDataNascimento())));
        item.appendChild(new Listcell(docente.getNacionalidadeEnum() != null
                ? docente.getNacionalidadeEnum().getDescricao()
                : safe(docente.getNacionalidade())));

        Hbox acoes = new Hbox();
        acoes.setSpacing("6px");

        Toolbarbutton btnAlterar = new Toolbarbutton("Alterar");
        btnAlterar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        openSub("docente-list", "docente-form", docente.getId());
                    }
                });
        acoes.appendChild(btnAlterar);

        Toolbarbutton btnMostrar = new Toolbarbutton("Mostrar");
        btnMostrar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        openSub("docente-list", "docente-view", docente.getId());
                    }
                });
        acoes.appendChild(btnMostrar);

        Toolbarbutton btnExcluir = new Toolbarbutton("Excluir");
        btnExcluir.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        excluirDocente(docente.getId());
                    }
                });
        acoes.appendChild(btnExcluir);

        Toolbarbutton btnExportar = new Toolbarbutton("Exportar TXT");
        btnExportar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        redirect("/api/relatorios/docentes/" + docente.getId() + ".txt");
                    }
                });
        acoes.appendChild(btnExportar);

        Listcell celulaAcoes = new Listcell();
        celulaAcoes.appendChild(acoes);
        item.appendChild(celulaAcoes);
        lstDocentes.appendChild(item);
    }

    private void excluirDocente(Long id) {
        try {
            docenteService().excluir(id);
            flashListVisivel = true;
            flashList = "Docente excluido com sucesso.";
            carregarLista();
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao excluir docente.";
        }
    }

    // --- Formulario ---

    private void initForm() {
        docenteIdEdicao = parseLongOrNull(currentRequest() != null ? currentRequest().getParameter("id") : null);
        erroFormVisivel = false;
        erroForm = "";
        try {
            Docente docente = docenteIdEdicao != null ? docenteService().buscarPorId(docenteIdEdicao) : new Docente();
            if (docente == null) {
                docente = new Docente();
                docenteIdEdicao = null;
            }

            tituloForm = docenteIdEdicao == null ? "Novo Docente" : "Alterar Docente";

            idDocenteIes = safe(docente.getIdDocenteIes());
            nomeDocente = safe(docente.getNome());
            cpfDocente = safe(docente.getCpf());
            documentoEstrangeiro = safe(docente.getDocumentoEstrangeiro());
            dataNascimento = docente.getDataNascimento() != null
                    ? new java.util.Date(docente.getDataNascimento().getTime()) : null;
            municipioNascimento = safe(docente.getMunicipioNascimento());

            itensCorRaca = montarItensCorRaca();
            corRacaSelecionada = docente.getCorRaca() == null ? null : String.valueOf(docente.getCorRaca());

            itensNacionalidade = montarItensNacionalidade();
            nacionalidadeSelecionada = docente.getNacionalidade() == null ? "1" : String.valueOf(docente.getNacionalidade());

            itensPaisOrigem = montarItensPaisOrigem();
            paisOrigemSelecionado = docente.getPaisOrigem() == null ? "BRA" : docente.getPaisOrigem();

            itensUfNascimento = montarItensUfNascimento();
            ufNascimentoSelecionado = docente.getUfNascimento() == null ? null : String.valueOf(docente.getUfNascimento());

            itensDeficiencia = montarItensDeficiencia();
            deficienciaSelecionada = docente.getDocenteDeficiencia() == null ? null : String.valueOf(docente.getDocenteDeficiencia());

            boxCamposComplementares = new Vbox();
            boxCamposComplementares.setSpacing("6px");
            popularCamposComplementares(docenteIdEdicao);
        } catch (Exception e) {
            erroFormVisivel = true;
            erroForm = "Falha ao carregar formulario de docente.";
        }
    }

    private List<Comboitem> montarItensCorRaca() {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        Comboitem vazio = new Comboitem("Nao informado");
        vazio.setValue("");
        itens.add(vazio);
        for (CorRacaEnum cor : CorRacaEnum.values()) {
            Comboitem item = new Comboitem(cor.getCodigo() + " - " + cor.getDescricao());
            item.setValue(String.valueOf(cor.getCodigo()));
            itens.add(item);
        }
        return itens;
    }

    private List<Comboitem> montarItensNacionalidade() {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        for (NacionalidadeEnum nac : NacionalidadeEnum.values()) {
            Comboitem item = new Comboitem(nac.getCodigo() + " - " + nac.getDescricao());
            item.setValue(String.valueOf(nac.getCodigo()));
            itens.add(item);
        }
        return itens;
    }

    private List<Comboitem> montarItensPaisOrigem() {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        for (PaisEnum pais : PaisEnum.values()) {
            Comboitem item = new Comboitem(pais.getCodigo() + " - " + pais.getDescricao());
            item.setValue(pais.getCodigo());
            itens.add(item);
        }
        return itens;
    }

    private List<Comboitem> montarItensUfNascimento() {
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

    private List<Comboitem> montarItensDeficiencia() {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        Comboitem vazio = new Comboitem("Nao informado");
        vazio.setValue("");
        itens.add(vazio);
        Comboitem nao = new Comboitem("0 - Nao");
        nao.setValue("0");
        itens.add(nao);
        Comboitem sim = new Comboitem("1 - Sim");
        sim.setValue("1");
        itens.add(sim);
        Comboitem nd = new Comboitem("2 - Nao dispoe da informacao");
        nd.setValue("2");
        itens.add(nd);
        return itens;
    }

    private void popularCamposComplementares(Long docenteId) throws Exception {
        camposComplementares.clear();
        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (docenteId != null) {
            valores.putAll(docenteService().carregarCamposComplementaresPorCampoId(docenteId));
        }
        List<LayoutCampo> campos = filtrarCamposComplementares(
                catalogoService().listarCamposModulo(ModulosLayout.DOCENTE_31));
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
            if (numero >= 1 && numero <= 12) {
                continue;
            }
            filtrados.add(campo);
        }
        return filtrados;
    }

    // --- Visualizacao ---

    private void initView() {
        docenteIdVisualizacao = parseLongOrNull(currentRequest() != null ? currentRequest().getParameter("id") : null);
        if (docenteIdVisualizacao == null) {
            goShell("docente-list");
            return;
        }
        try {
            Docente docente = docenteService().buscarPorId(docenteIdVisualizacao);
            if (docente == null) {
                goShell("docente-list");
                return;
            }
            viewId = safe(docente.getId());
            viewIdIes = safe(docente.getIdDocenteIes());
            viewNome = safe(docente.getNome());
            viewCpf = safe(docente.getCpf());
            viewDocumento = safe(docente.getDocumentoEstrangeiro());
            viewNascimento = safe(docente.getDataNascimento());
            viewCorRaca = docente.getCorRacaEnum() != null
                    ? docente.getCorRacaEnum().getDescricao() + " (" + docente.getCorRaca() + ")"
                    : safe(docente.getCorRaca());
            viewNacionalidade = docente.getNacionalidadeEnum() != null
                    ? docente.getNacionalidadeEnum().getDescricao() + " (" + docente.getNacionalidade() + ")"
                    : safe(docente.getNacionalidade());
            viewPais = docente.getPaisOrigemEnum() != null
                    ? docente.getPaisOrigemEnum().getDescricao() + " (" + docente.getPaisOrigem() + ")"
                    : safe(docente.getPaisOrigem());
            viewUf = docente.getUfNascimentoEnum() != null
                    ? docente.getUfNascimentoEnum().getDescricao() + " (" + docente.getUfNascimento() + ")"
                    : safe(docente.getUfNascimento());
            viewMunicipio = safe(docente.getMunicipioNascimento());
            viewDeficiencia = safe(docente.getDocenteDeficiencia());

            lstViewCampos = new Listbox();
            lstViewCampos.setWidth("100%");
            Listhead head = new Listhead();
            head.appendChild(new Listheader("Campo", null, "55%"));
            head.appendChild(new Listheader("Valor", null, "45%"));
            lstViewCampos.appendChild(head);
            preencherCamposView(docenteIdVisualizacao);
        } catch (Exception e) {
            goShell("docente-list");
        }
    }

    private void preencherCamposView(Long docenteId) throws Exception {
        Map<Long, String> valores = docenteService().carregarCamposComplementaresPorCampoId(docenteId);
        Map<Long, String> rotulos = montarRotulosCampos(ModulosLayout.DOCENTE_31);
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
        openSub("docente-list", "docente-form");
    }

    @Command
    public void exportarTodos() {
        redirect("/api/relatorios/docentes.txt");
    }

    @Command
    @NotifyChange({"lstDocentes","erroList","erroListVisivel","flashList","flashListVisivel",
                   "totalLabel","paginacaoLabel","anteriorDisabled","proximaDisabled"})
    public void importar(@org.zkoss.bind.annotation.BindingParam("conteudo") String conteudo) {
        try {
            int total = docenteService().importarTxtPipe(conteudo);
            putFlash("flashHomeMessage", "Importacao de docente concluida: " + total + " registro(s).");
            putFlash("flashDocenteMessage", "Importacao concluida: " + total + " registro(s).");
            goShell("docente-list");
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao importar TXT de docente.";
        }
    }

    @Command
    public void paginaAnterior() {
        if (paginaAtual > 1) {
            goShell("docente-list", paginaAtual - 1);
        }
    }

    @Command
    public void proximaPagina() {
        if (paginaAtual < totalPaginas) {
            goShell("docente-list", paginaAtual + 1);
        }
    }

    @Command
    public void voltar() {
        goShell("docente-list");
    }

    @Command
    @NotifyChange({"erroForm","erroFormVisivel"})
    public void salvar() {
        erroFormVisivel = false;
        erroForm = "";
        try {
            Date dataNasc = toSqlDate(dataNascimento);
            Integer nac = parseIntegerOrNull(nacionalidadeSelecionada);
            if (nac == null) {
                nac = Integer.valueOf(1);
            }
            String pais = trimToNull(paisOrigemSelecionado);
            if (pais == null) {
                pais = "BRA";
            }

            Docente docente = Docente.builder()
                    .id(docenteIdEdicao)
                    .idDocenteIes(trimToEmpty(idDocenteIes))
                    .nome(trimToEmpty(nomeDocente))
                    .cpf(trimToEmpty(cpfDocente))
                    .documentoEstrangeiro(trimToEmpty(documentoEstrangeiro))
                    .dataNascimento(dataNasc)
                    .corRaca(parseIntegerOrNull(corRacaSelecionada))
                    .nacionalidade(nac)
                    .paisOrigem(pais)
                    .ufNascimento(parseIntegerOrNull(ufNascimentoSelecionado))
                    .municipioNascimento(trimToEmpty(municipioNascimento))
                    .docenteDeficiencia(parseIntegerOrNull(deficienciaSelecionada))
                    .build();

            Map<Long, String> extras = mapCamposComplementares(camposComplementares);

            if (docenteIdEdicao == null) {
                docenteService().cadastrar(docente, extras);
                putFlash("flashDocenteMessage", "Docente incluido com sucesso.");
            } else {
                docenteService().atualizar(docente, extras);
                putFlash("flashDocenteMessage", "Docente alterado com sucesso.");
            }
            goShell("docente-list");
        } catch (Exception e) {
            erroFormVisivel = true;
            erroForm = e.getMessage() != null ? e.getMessage() : "Falha ao salvar docente.";
        }
    }

    @Command
    public void voltarView() {
        goShell("docente-list");
    }

    @Command
    public void editarView() {
        openSub("docente-list", "docente-form", docenteIdVisualizacao);
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
    public Listbox getLstDocentes()         { return lstDocentes; }

    public String getErroForm()             { return erroForm; }
    public boolean isErroFormVisivel()      { return erroFormVisivel; }
    public String getTituloForm()           { return tituloForm; }

    public String getIdDocenteIes()         { return idDocenteIes; }
    public void   setIdDocenteIes(String v) { this.idDocenteIes = v; }
    public String getNomeDocente()          { return nomeDocente; }
    public void   setNomeDocente(String v)  { this.nomeDocente = v; }
    public String getCpfDocente()           { return cpfDocente; }
    public void   setCpfDocente(String v)   { this.cpfDocente = v; }
    public String getDocumentoEstrangeiro() { return documentoEstrangeiro; }
    public void   setDocumentoEstrangeiro(String v){ this.documentoEstrangeiro = v; }
    public java.util.Date getDataNascimento()         { return dataNascimento; }
    public void   setDataNascimento(java.util.Date v) { this.dataNascimento = v; }
    public String getMunicipioNascimento()  { return municipioNascimento; }
    public void   setMunicipioNascimento(String v){ this.municipioNascimento = v; }

    public List<Comboitem> getItensCorRaca()       { return itensCorRaca; }
    public String getCorRacaSelecionada()           { return corRacaSelecionada; }
    public void   setCorRacaSelecionada(String v)   { this.corRacaSelecionada = v; }

    public List<Comboitem> getItensNacionalidade()     { return itensNacionalidade; }
    public String getNacionalidadeSelecionada()         { return nacionalidadeSelecionada; }
    public void   setNacionalidadeSelecionada(String v){ this.nacionalidadeSelecionada = v; }

    public List<Comboitem> getItensPaisOrigem()    { return itensPaisOrigem; }
    public String getPaisOrigemSelecionado()       { return paisOrigemSelecionado; }
    public void   setPaisOrigemSelecionado(String v){ this.paisOrigemSelecionado = v; }

    public List<Comboitem> getItensUfNascimento()  { return itensUfNascimento; }
    public String getUfNascimentoSelecionado()     { return ufNascimentoSelecionado; }
    public void   setUfNascimentoSelecionado(String v){ this.ufNascimentoSelecionado = v; }

    public List<Comboitem> getItensDeficiencia()   { return itensDeficiencia; }
    public String getDeficienciaSelecionada()      { return deficienciaSelecionada; }
    public void   setDeficienciaSelecionada(String v){ this.deficienciaSelecionada = v; }

    public Vbox getBoxCamposComplementares()       { return boxCamposComplementares; }

    public String getViewId()               { return viewId; }
    public String getViewIdIes()            { return viewIdIes; }
    public String getViewNome()             { return viewNome; }
    public String getViewCpf()              { return viewCpf; }
    public String getViewDocumento()        { return viewDocumento; }
    public String getViewNascimento()       { return viewNascimento; }
    public String getViewCorRaca()          { return viewCorRaca; }
    public String getViewNacionalidade()    { return viewNacionalidade; }
    public String getViewPais()             { return viewPais; }
    public String getViewUf()               { return viewUf; }
    public String getViewMunicipio()        { return viewMunicipio; }
    public String getViewDeficiencia()      { return viewDeficiencia; }
    public Listbox getLstViewCampos()       { return lstViewCampos; }
}
