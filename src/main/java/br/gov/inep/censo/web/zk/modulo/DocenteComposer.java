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
import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller MVC do modulo Docente (Registro 31).
 */
public class DocenteComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_PAGINA = 10;

    private final DocenteService docenteService = new DocenteService();
    private final CatalogoService catalogoService = new CatalogoService();

    // Lista
    private Label lblErroListDocente;
    private Label lblFlashListDocente;
    private Label lblTotalListDocente;
    private Label lblPaginacaoListDocente;
    private Textbox txtImportacaoListDocente;
    private Listbox lstDocentes;
    private Button btnPaginaAnteriorDocente;
    private Button btnPaginaProximaDocente;

    // Formulario
    private Label lblErroFormDocente;
    private Label lblTituloFormDocente;
    private Textbox txtIdDocenteIes;
    private Textbox txtNomeDocente;
    private Textbox txtCpfDocente;
    private Textbox txtDocumentoEstrangeiroDocente;
    private Datebox dtNascimentoDocente;
    private Combobox cmbCorRacaDocente;
    private Combobox cmbNacionalidadeDocente;
    private Combobox cmbPaisOrigemDocente;
    private Combobox cmbUfNascimentoDocente;
    private Textbox txtMunicipioNascimentoDocente;
    private Combobox cmbDeficienciaDocente;
    private Vbox boxCamposComplementaresDocente;

    // Visualizacao
    private Label lblViewDocenteId;
    private Label lblViewDocenteIdIes;
    private Label lblViewDocenteNome;
    private Label lblViewDocenteCpf;
    private Label lblViewDocenteDocumento;
    private Label lblViewDocenteNascimento;
    private Label lblViewDocenteCorRaca;
    private Label lblViewDocenteNacionalidade;
    private Label lblViewDocentePais;
    private Label lblViewDocenteUf;
    private Label lblViewDocenteMunicipio;
    private Label lblViewDocenteDeficiencia;
    private Listbox lstViewCamposDocente;

    private final Map<Long, Textbox> camposComplementares = new LinkedHashMap<Long, Textbox>();

    private int paginaAtual = 1;
    private int totalPaginas = 1;
    private Long docenteIdEdicao;
    private Long docenteIdVisualizacao;

    public void onCreate$winDocenteList() {
        lblErroListDocente.setVisible(false);
        lblErroListDocente.setValue("");

        String flash = consumeFlash("flashDocenteMessage");
        if (flash == null) {
            lblFlashListDocente.setVisible(false);
            lblFlashListDocente.setValue("");
        } else {
            lblFlashListDocente.setVisible(true);
            lblFlashListDocente.setValue(flash);
        }

        paginaAtual = parseIntOrDefault(currentRequest().getParameter("pagina"), 1);
        if (paginaAtual <= 0) {
            paginaAtual = 1;
        }

        carregarLista();
    }

    public void onClick$btnNovoListDocente() {
        openSub("docente-list", "docente-form");
    }

    public void onClick$btnMenuListDocente() {
        goShell("dashboard");
    }

    public void onClick$btnExportarListDocente() {
        redirect("/api/relatorios/docentes.txt");
    }

    public void onClick$btnImportarListDocente() {
        try {
            String conteudo = txtImportacaoListDocente.getValue();
            int total = docenteService.importarTxtPipe(conteudo);
            putFlash("flashHomeMessage", "Importacao de docente concluida: " + total + " registro(s).");
            putFlash("flashDocenteMessage", "Importacao concluida: " + total + " registro(s).");
            goShell("docente-list");
        } catch (Exception e) {
            showListError("Falha ao importar TXT de docente.");
        }
    }

    public void onClick$btnPaginaAnteriorDocente() {
        if (paginaAtual > 1) {
            goShell("docente-list", paginaAtual - 1);
        }
    }

    public void onClick$btnPaginaProximaDocente() {
        if (paginaAtual < totalPaginas) {
            goShell("docente-list", paginaAtual + 1);
        }
    }

    public void onCreate$winDocenteForm() {
        docenteIdEdicao = parseLongOrNull(currentRequest().getParameter("id"));
        lblErroFormDocente.setVisible(false);
        lblErroFormDocente.setValue("");

        try {
            Docente docente = docenteIdEdicao != null ? docenteService.buscarPorId(docenteIdEdicao) : new Docente();
            if (docente == null) {
                docente = new Docente();
                docenteIdEdicao = null;
            }

            lblTituloFormDocente.setValue(docenteIdEdicao == null ? "Novo Docente" : "Alterar Docente");
            popularCombos(docente);
            popularCamposBasicos(docente);
            popularCamposComplementares(docenteIdEdicao);
        } catch (Exception e) {
            showFormError("Falha ao carregar formulario de docente.");
        }
    }

    public void onClick$btnVoltarFormDocente() {
        goShell("docente-list");
    }

    public void onClick$btnSalvarFormDocente() {
        lblErroFormDocente.setVisible(false);
        lblErroFormDocente.setValue("");

        try {
            Date dataNascimento = toSqlDate(dtNascimentoDocente.getValue());
            Integer nacionalidade = parseComboInteger(cmbNacionalidadeDocente);
            if (nacionalidade == null) {
                nacionalidade = Integer.valueOf(1);
            }

            String paisOrigem = parseComboString(cmbPaisOrigemDocente);
            if (paisOrigem == null || paisOrigem.length() == 0) {
                paisOrigem = "BRA";
            }

            Docente docente = Docente.builder()
                    .id(docenteIdEdicao)
                    .idDocenteIes(trimToEmpty(txtIdDocenteIes.getValue()))
                    .nome(trimToEmpty(txtNomeDocente.getValue()))
                    .cpf(trimToEmpty(txtCpfDocente.getValue()))
                    .documentoEstrangeiro(trimToEmpty(txtDocumentoEstrangeiroDocente.getValue()))
                    .dataNascimento(dataNascimento)
                    .corRaca(parseComboInteger(cmbCorRacaDocente))
                    .nacionalidade(nacionalidade)
                    .paisOrigem(paisOrigem)
                    .ufNascimento(parseComboInteger(cmbUfNascimentoDocente))
                    .municipioNascimento(trimToEmpty(txtMunicipioNascimentoDocente.getValue()))
                    .docenteDeficiencia(parseComboInteger(cmbDeficienciaDocente))
                    .build();

            Map<Long, String> extras = mapCamposComplementares(camposComplementares);

            if (docenteIdEdicao == null) {
                docenteService.cadastrar(docente, extras);
                putFlash("flashDocenteMessage", "Docente incluido com sucesso.");
            } else {
                docenteService.atualizar(docente, extras);
                putFlash("flashDocenteMessage", "Docente alterado com sucesso.");
            }

            goShell("docente-list");
        } catch (Exception e) {
            showFormError(e.getMessage() != null ? e.getMessage() : "Falha ao salvar docente.");
        }
    }

    public void onCreate$winDocenteView() {
        docenteIdVisualizacao = parseLongOrNull(currentRequest().getParameter("id"));
        if (docenteIdVisualizacao == null) {
            goShell("docente-list");
            return;
        }

        try {
            Docente docente = docenteService.buscarPorId(docenteIdVisualizacao);
            if (docente == null) {
                goShell("docente-list");
                return;
            }

            lblViewDocenteId.setValue(safe(docente.getId()));
            lblViewDocenteIdIes.setValue(safe(docente.getIdDocenteIes()));
            lblViewDocenteNome.setValue(safe(docente.getNome()));
            lblViewDocenteCpf.setValue(safe(docente.getCpf()));
            lblViewDocenteDocumento.setValue(safe(docente.getDocumentoEstrangeiro()));
            lblViewDocenteNascimento.setValue(safe(docente.getDataNascimento()));

            String corRaca = docente.getCorRacaEnum() != null
                    ? docente.getCorRacaEnum().getDescricao() + " (" + docente.getCorRaca() + ")"
                    : safe(docente.getCorRaca());
            lblViewDocenteCorRaca.setValue(corRaca);

            String nacionalidade = docente.getNacionalidadeEnum() != null
                    ? docente.getNacionalidadeEnum().getDescricao() + " (" + docente.getNacionalidade() + ")"
                    : safe(docente.getNacionalidade());
            lblViewDocenteNacionalidade.setValue(nacionalidade);

            String pais = docente.getPaisOrigemEnum() != null
                    ? docente.getPaisOrigemEnum().getDescricao() + " (" + docente.getPaisOrigem() + ")"
                    : safe(docente.getPaisOrigem());
            lblViewDocentePais.setValue(pais);

            String uf = docente.getUfNascimentoEnum() != null
                    ? docente.getUfNascimentoEnum().getDescricao() + " (" + docente.getUfNascimento() + ")"
                    : safe(docente.getUfNascimento());
            lblViewDocenteUf.setValue(uf);

            lblViewDocenteMunicipio.setValue(safe(docente.getMunicipioNascimento()));
            lblViewDocenteDeficiencia.setValue(safe(docente.getDocenteDeficiencia()));

            preencherCamposView(docenteIdVisualizacao);
        } catch (Exception e) {
            goShell("docente-list");
        }
    }

    public void onClick$btnVoltarViewDocente() {
        goShell("docente-list");
    }

    public void onClick$btnEditarViewDocente() {
        openSub("docente-list", "docente-form", docenteIdVisualizacao);
    }

    private void carregarLista() {
        try {
            int total = docenteService.contar();
            totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
            if (paginaAtual > totalPaginas) {
                paginaAtual = totalPaginas;
            }

            List<Docente> docentes = docenteService.listarPaginado(paginaAtual, TAMANHO_PAGINA);
            lstDocentes.getItems().clear();

            for (int i = 0; i < docentes.size(); i++) {
                adicionarLinhaDocente(docentes.get(i));
            }

            lblTotalListDocente.setValue(String.valueOf(total));
            lblPaginacaoListDocente.setValue("Pagina " + paginaAtual + " de " + totalPaginas);
            btnPaginaAnteriorDocente.setDisabled(paginaAtual <= 1);
            btnPaginaProximaDocente.setDisabled(paginaAtual >= totalPaginas);
        } catch (Exception e) {
            showListError("Falha ao carregar listagem de docente.");
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
        btnAlterar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                openSub("docente-list", "docente-form", docente.getId());
            }
        });
        acoes.appendChild(btnAlterar);

        Toolbarbutton btnMostrar = new Toolbarbutton("Mostrar");
        btnMostrar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                openSub("docente-list", "docente-view", docente.getId());
            }
        });
        acoes.appendChild(btnMostrar);

        Toolbarbutton btnExcluir = new Toolbarbutton("Excluir");
        btnExcluir.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                excluirDocente(docente.getId());
            }
        });
        acoes.appendChild(btnExcluir);

        Toolbarbutton btnExportar = new Toolbarbutton("Exportar TXT");
        btnExportar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                exportarDocente(docente.getId());
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
            docenteService.excluir(id);
            lblFlashListDocente.setVisible(true);
            lblFlashListDocente.setValue("Docente excluido com sucesso.");
            carregarLista();
        } catch (Exception e) {
            showListError("Falha ao excluir docente.");
        }
    }

    private void exportarDocente(Long id) {
        if (id == null) {
            showListError("Falha ao exportar docente.");
            return;
        }
        redirect("/api/relatorios/docentes/" + id + ".txt");
    }

    private void popularCombos(Docente docente) {
        cmbCorRacaDocente.getItems().clear();
        Comboitem vazioCor = new Comboitem("Nao informado");
        vazioCor.setValue("");
        cmbCorRacaDocente.appendChild(vazioCor);
        CorRacaEnum[] cores = CorRacaEnum.values();
        for (int i = 0; i < cores.length; i++) {
            Comboitem item = new Comboitem(cores[i].getCodigo() + " - " + cores[i].getDescricao());
            item.setValue(String.valueOf(cores[i].getCodigo()));
            cmbCorRacaDocente.appendChild(item);
        }
        selecionarComboPorValor(cmbCorRacaDocente, docente.getCorRaca() == null ? null : String.valueOf(docente.getCorRaca()));

        cmbNacionalidadeDocente.getItems().clear();
        NacionalidadeEnum[] nacionalidades = NacionalidadeEnum.values();
        for (int i = 0; i < nacionalidades.length; i++) {
            Comboitem item = new Comboitem(nacionalidades[i].getCodigo() + " - " + nacionalidades[i].getDescricao());
            item.setValue(String.valueOf(nacionalidades[i].getCodigo()));
            cmbNacionalidadeDocente.appendChild(item);
        }
        selecionarComboPorValor(cmbNacionalidadeDocente,
                docente.getNacionalidade() == null ? "1" : String.valueOf(docente.getNacionalidade()));

        cmbPaisOrigemDocente.getItems().clear();
        PaisEnum[] paises = PaisEnum.values();
        for (int i = 0; i < paises.length; i++) {
            Comboitem item = new Comboitem(paises[i].getCodigo() + " - " + paises[i].getDescricao());
            item.setValue(paises[i].getCodigo());
            cmbPaisOrigemDocente.appendChild(item);
        }
        selecionarComboPorValor(cmbPaisOrigemDocente,
                docente.getPaisOrigem() == null ? "BRA" : docente.getPaisOrigem());

        cmbUfNascimentoDocente.getItems().clear();
        Comboitem vazioUf = new Comboitem("Nao informado");
        vazioUf.setValue("");
        cmbUfNascimentoDocente.appendChild(vazioUf);
        EstadoEnum[] ufs = EstadoEnum.values();
        for (int i = 0; i < ufs.length; i++) {
            Comboitem item = new Comboitem(ufs[i].getCodigo() + " - " + ufs[i].getDescricao());
            item.setValue(String.valueOf(ufs[i].getCodigo()));
            cmbUfNascimentoDocente.appendChild(item);
        }
        selecionarComboPorValor(cmbUfNascimentoDocente,
                docente.getUfNascimento() == null ? null : String.valueOf(docente.getUfNascimento()));

        cmbDeficienciaDocente.getItems().clear();
        Comboitem vazioDef = new Comboitem("Nao informado");
        vazioDef.setValue("");
        cmbDeficienciaDocente.appendChild(vazioDef);
        Comboitem nao = new Comboitem("0 - Nao");
        nao.setValue("0");
        cmbDeficienciaDocente.appendChild(nao);
        Comboitem sim = new Comboitem("1 - Sim");
        sim.setValue("1");
        cmbDeficienciaDocente.appendChild(sim);
        Comboitem nd = new Comboitem("2 - Nao dispoe da informacao");
        nd.setValue("2");
        cmbDeficienciaDocente.appendChild(nd);
        selecionarComboPorValor(cmbDeficienciaDocente,
                docente.getDocenteDeficiencia() == null ? null : String.valueOf(docente.getDocenteDeficiencia()));
    }

    private void popularCamposBasicos(Docente docente) {
        txtIdDocenteIes.setValue(safe(docente.getIdDocenteIes()));
        txtNomeDocente.setValue(safe(docente.getNome()));
        txtCpfDocente.setValue(safe(docente.getCpf()));
        txtDocumentoEstrangeiroDocente.setValue(safe(docente.getDocumentoEstrangeiro()));
        dtNascimentoDocente.setValue(docente.getDataNascimento() != null
                ? new java.util.Date(docente.getDataNascimento().getTime()) : null);
        txtMunicipioNascimentoDocente.setValue(safe(docente.getMunicipioNascimento()));
    }

    private void popularCamposComplementares(Long docenteId) throws Exception {
        boxCamposComplementaresDocente.getChildren().clear();
        camposComplementares.clear();

        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (docenteId != null) {
            valores.putAll(docenteService.carregarCamposComplementaresPorCampoId(docenteId));
        }

        List<LayoutCampo> campos = filtrarCamposComplementares(catalogoService.listarCamposModulo(ModulosLayout.DOCENTE_31));
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
            boxCamposComplementaresDocente.appendChild(linha);
            camposComplementares.put(campo.getId(), textbox);
        }
    }

    private void preencherCamposView(Long docenteId) throws Exception {
        lstViewCamposDocente.getItems().clear();

        Map<Long, String> valores = docenteService.carregarCamposComplementaresPorCampoId(docenteId);
        Map<Long, String> rotulos = montarRotulosCampos(ModulosLayout.DOCENTE_31);

        if (valores == null || valores.isEmpty()) {
            Listitem vazio = new Listitem();
            vazio.appendChild(new Listcell("Nenhum campo complementar informado."));
            vazio.appendChild(new Listcell(""));
            lstViewCamposDocente.appendChild(vazio);
            return;
        }

        for (Map.Entry<Long, String> entry : valores.entrySet()) {
            Listitem item = new Listitem();
            String rotulo = rotulos.get(entry.getKey());
            item.appendChild(new Listcell(rotulo != null ? rotulo : String.valueOf(entry.getKey())));
            item.appendChild(new Listcell(safe(entry.getValue())));
            lstViewCamposDocente.appendChild(item);
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

    private Map<Long, String> montarRotulosCampos(String modulo) throws Exception {
        Map<Long, String> rotulos = new LinkedHashMap<Long, String>();
        List<LayoutCampo> campos = catalogoService.listarCamposModulo(modulo);
        if (campos == null) {
            return rotulos;
        }

        for (int i = 0; i < campos.size(); i++) {
            LayoutCampo campo = campos.get(i);
            rotulos.put(campo.getId(), "[" + campo.getNumeroCampo() + "] " + campo.getNomeCampo());
        }
        return rotulos;
    }

    private String parseComboString(Combobox combo) {
        if (combo == null) {
            return null;
        }
        Comboitem selected = combo.getSelectedItem();
        String value = selected != null ? String.valueOf(selected.getValue()) : trimToNull(combo.getValue());
        return trimToNull(value);
    }

    private Integer parseComboInteger(Combobox combo) {
        return parseIntegerOrNull(parseComboString(combo));
    }

    private void selecionarComboPorValor(Combobox combo, String valor) {
        if (combo == null) {
            return;
        }

        List itens = combo.getItems();
        for (int i = 0; i < itens.size(); i++) {
            Comboitem item = (Comboitem) itens.get(i);
            Object itemValue = item.getValue();
            if (valor == null && (itemValue == null || String.valueOf(itemValue).length() == 0)) {
                combo.setSelectedItem(item);
                return;
            }
            if (valor != null && itemValue != null && valor.equals(String.valueOf(itemValue))) {
                combo.setSelectedItem(item);
                return;
            }
        }
    }

    private void showListError(String message) {
        lblErroListDocente.setVisible(true);
        lblErroListDocente.setValue(message);
    }

    private void showFormError(String message) {
        lblErroFormDocente.setVisible(true);
        lblErroFormDocente.setValue(message);
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
