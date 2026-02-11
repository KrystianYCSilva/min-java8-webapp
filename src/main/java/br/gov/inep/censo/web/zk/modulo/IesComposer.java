package br.gov.inep.censo.web.zk.modulo;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Ies;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.enums.EstadoEnum;
import br.gov.inep.censo.model.enums.TipoLaboratorioEnum;
import br.gov.inep.censo.service.CatalogoService;
import br.gov.inep.censo.service.IesService;
import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller MVC do modulo IES (Registro 11).
 */
public class IesComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_PAGINA = 10;

    private final IesService iesService = new IesService();
    private final CatalogoService catalogoService = new CatalogoService();

    // Lista
    private Label lblErroListIes;
    private Label lblFlashListIes;
    private Label lblTotalListIes;
    private Label lblPaginacaoListIes;
    private Textbox txtImportacaoListIes;
    private Listbox lstIes;
    private Button btnPaginaAnteriorIes;
    private Button btnPaginaProximaIes;

    // Formulario
    private Label lblErroFormIes;
    private Label lblTituloFormIes;
    private Textbox txtIdIesInep;
    private Textbox txtNomeLaboratorioIes;
    private Textbox txtRegistroLaboratorioIes;
    private Combobox cmbAtivoAnoIes;
    private Textbox txtDescricaoAtividadesIes;
    private Textbox txtPalavrasChaveIes;
    private Combobox cmbLaboratorioInformaticaIes;
    private Combobox cmbTipoLaboratorioIes;
    private Combobox cmbUfLaboratorioIes;
    private Textbox txtMunicipioLaboratorioIes;
    private Vbox boxCamposComplementaresIes;

    // Visualizacao
    private Label lblViewIesId;
    private Label lblViewIesIdInep;
    private Label lblViewIesNomeLaboratorio;
    private Label lblViewIesRegistro;
    private Label lblViewIesAtivo;
    private Label lblViewIesDescricao;
    private Label lblViewIesPalavras;
    private Label lblViewIesLabInfo;
    private Label lblViewIesTipo;
    private Label lblViewIesUf;
    private Label lblViewIesMunicipio;
    private Listbox lstViewCamposIes;

    private final Map<Long, Textbox> camposComplementares = new LinkedHashMap<Long, Textbox>();

    private int paginaAtual = 1;
    private int totalPaginas = 1;
    private Long iesIdEdicao;
    private Long iesIdVisualizacao;

    public void onCreate$winIesList() {
        lblErroListIes.setVisible(false);
        lblErroListIes.setValue("");

        String flash = consumeFlash("flashIesMessage");
        if (flash == null) {
            lblFlashListIes.setVisible(false);
            lblFlashListIes.setValue("");
        } else {
            lblFlashListIes.setVisible(true);
            lblFlashListIes.setValue(flash);
        }

        paginaAtual = parseIntOrDefault(currentRequest().getParameter("pagina"), 1);
        if (paginaAtual <= 0) {
            paginaAtual = 1;
        }

        carregarLista();
    }

    public void onClick$btnNovoListIes() {
        openSub("ies-list", "ies-form");
    }

    public void onClick$btnMenuListIes() {
        goShell("dashboard");
    }

    public void onClick$btnExportarListIes() {
        redirect("/api/relatorios/ies.txt");
    }

    public void onClick$btnImportarListIes() {
        try {
            String conteudo = txtImportacaoListIes.getValue();
            int total = iesService.importarTxtPipe(conteudo);
            putFlash("flashHomeMessage", "Importacao de IES concluida: " + total + " registro(s).");
            putFlash("flashIesMessage", "Importacao concluida: " + total + " registro(s).");
            goShell("ies-list");
        } catch (Exception e) {
            showListError("Falha ao importar TXT de IES.");
        }
    }

    public void onClick$btnPaginaAnteriorIes() {
        if (paginaAtual > 1) {
            goShell("ies-list", paginaAtual - 1);
        }
    }

    public void onClick$btnPaginaProximaIes() {
        if (paginaAtual < totalPaginas) {
            goShell("ies-list", paginaAtual + 1);
        }
    }

    public void onCreate$winIesForm() {
        iesIdEdicao = parseLongOrNull(currentRequest().getParameter("id"));
        lblErroFormIes.setVisible(false);
        lblErroFormIes.setValue("");

        try {
            Ies ies = iesIdEdicao != null ? iesService.buscarPorId(iesIdEdicao) : new Ies();
            if (ies == null) {
                ies = new Ies();
                iesIdEdicao = null;
            }

            lblTituloFormIes.setValue(iesIdEdicao == null ? "Nova IES" : "Alterar IES");
            popularCombos(ies);
            popularCamposBasicos(ies);
            popularCamposComplementares(iesIdEdicao);
        } catch (Exception e) {
            showFormError("Falha ao carregar formulario de IES.");
        }
    }

    public void onClick$btnVoltarFormIes() {
        goShell("ies-list");
    }

    public void onClick$btnSalvarFormIes() {
        lblErroFormIes.setVisible(false);
        lblErroFormIes.setValue("");

        try {
            Ies ies = Ies.builder()
                    .id(iesIdEdicao)
                    .idIesInep(parseLongOrNull(txtIdIesInep.getValue()))
                    .nomeLaboratorio(trimToEmpty(txtNomeLaboratorioIes.getValue()))
                    .registroLaboratorioIes(trimToEmpty(txtRegistroLaboratorioIes.getValue()))
                    .laboratorioAtivoAno(Integer.valueOf(parseComboIntOrDefault(cmbAtivoAnoIes, 1)))
                    .descricaoAtividades(trimToEmpty(txtDescricaoAtividadesIes.getValue()))
                    .palavrasChave(trimToEmpty(txtPalavrasChaveIes.getValue()))
                    .laboratorioInformatica(parseComboInteger(cmbLaboratorioInformaticaIes))
                    .tipoLaboratorio(parseComboInteger(cmbTipoLaboratorioIes))
                    .codigoUfLaboratorio(parseComboInteger(cmbUfLaboratorioIes))
                    .codigoMunicipioLaboratorio(trimToEmpty(txtMunicipioLaboratorioIes.getValue()))
                    .build();

            Map<Long, String> extras = mapCamposComplementares(camposComplementares);

            if (iesIdEdicao == null) {
                iesService.cadastrar(ies, extras);
                putFlash("flashIesMessage", "IES incluida com sucesso.");
            } else {
                iesService.atualizar(ies, extras);
                putFlash("flashIesMessage", "IES alterada com sucesso.");
            }

            goShell("ies-list");
        } catch (Exception e) {
            showFormError(e.getMessage() != null ? e.getMessage() : "Falha ao salvar IES.");
        }
    }

    public void onCreate$winIesView() {
        iesIdVisualizacao = parseLongOrNull(currentRequest().getParameter("id"));
        if (iesIdVisualizacao == null) {
            goShell("ies-list");
            return;
        }

        try {
            Ies ies = iesService.buscarPorId(iesIdVisualizacao);
            if (ies == null) {
                goShell("ies-list");
                return;
            }

            lblViewIesId.setValue(safe(ies.getId()));
            lblViewIesIdInep.setValue(safe(ies.getIdIesInep()));
            lblViewIesNomeLaboratorio.setValue(safe(ies.getNomeLaboratorio()));
            lblViewIesRegistro.setValue(safe(ies.getRegistroLaboratorioIes()));
            lblViewIesAtivo.setValue(safe(ies.getLaboratorioAtivoAno()));
            lblViewIesDescricao.setValue(safe(ies.getDescricaoAtividades()));
            lblViewIesPalavras.setValue(safe(ies.getPalavrasChave()));
            lblViewIesLabInfo.setValue(safe(ies.getLaboratorioInformatica()));

            String tipo = ies.getTipoLaboratorioEnum() != null
                    ? ies.getTipoLaboratorioEnum().getDescricao() + " (" + ies.getTipoLaboratorio() + ")"
                    : safe(ies.getTipoLaboratorio());
            lblViewIesTipo.setValue(tipo);

            String uf = ies.getUfLaboratorioEnum() != null
                    ? ies.getUfLaboratorioEnum().getDescricao() + " (" + ies.getCodigoUfLaboratorio() + ")"
                    : safe(ies.getCodigoUfLaboratorio());
            lblViewIesUf.setValue(uf);

            lblViewIesMunicipio.setValue(safe(ies.getCodigoMunicipioLaboratorio()));

            preencherCamposView(iesIdVisualizacao);
        } catch (Exception e) {
            goShell("ies-list");
        }
    }

    public void onClick$btnVoltarViewIes() {
        goShell("ies-list");
    }

    public void onClick$btnEditarViewIes() {
        openSub("ies-list", "ies-form", iesIdVisualizacao);
    }

    private void carregarLista() {
        try {
            int total = iesService.contar();
            totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
            if (paginaAtual > totalPaginas) {
                paginaAtual = totalPaginas;
            }

            List<Ies> itens = iesService.listarPaginado(paginaAtual, TAMANHO_PAGINA);
            lstIes.getItems().clear();

            for (int i = 0; i < itens.size(); i++) {
                adicionarLinhaIes(itens.get(i));
            }

            lblTotalListIes.setValue(String.valueOf(total));
            lblPaginacaoListIes.setValue("Pagina " + paginaAtual + " de " + totalPaginas);
            btnPaginaAnteriorIes.setDisabled(paginaAtual <= 1);
            btnPaginaProximaIes.setDisabled(paginaAtual >= totalPaginas);
        } catch (Exception e) {
            showListError("Falha ao carregar listagem de IES.");
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
        btnAlterar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                openSub("ies-list", "ies-form", item.getId());
            }
        });
        acoes.appendChild(btnAlterar);

        Toolbarbutton btnMostrar = new Toolbarbutton("Mostrar");
        btnMostrar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                openSub("ies-list", "ies-view", item.getId());
            }
        });
        acoes.appendChild(btnMostrar);

        Toolbarbutton btnExcluir = new Toolbarbutton("Excluir");
        btnExcluir.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                excluirIes(item.getId());
            }
        });
        acoes.appendChild(btnExcluir);

        Toolbarbutton btnExportar = new Toolbarbutton("Exportar TXT");
        btnExportar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                exportarIes(item.getId());
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
            iesService.excluir(id);
            lblFlashListIes.setVisible(true);
            lblFlashListIes.setValue("IES excluida com sucesso.");
            carregarLista();
        } catch (Exception e) {
            showListError("Falha ao excluir IES.");
        }
    }

    private void exportarIes(Long id) {
        if (id == null) {
            showListError("Falha ao exportar IES.");
            return;
        }
        redirect("/api/relatorios/ies/" + id + ".txt");
    }

    private void popularCombos(Ies ies) {
        cmbAtivoAnoIes.getItems().clear();
        Comboitem ativoSim = new Comboitem("1 - Sim");
        ativoSim.setValue("1");
        cmbAtivoAnoIes.appendChild(ativoSim);
        Comboitem ativoNao = new Comboitem("0 - Nao");
        ativoNao.setValue("0");
        cmbAtivoAnoIes.appendChild(ativoNao);
        selecionarComboPorValor(cmbAtivoAnoIes,
                ies.getLaboratorioAtivoAno() == null ? "1" : String.valueOf(ies.getLaboratorioAtivoAno()));

        cmbLaboratorioInformaticaIes.getItems().clear();
        Comboitem infoVazio = new Comboitem("Nao informado");
        infoVazio.setValue("");
        cmbLaboratorioInformaticaIes.appendChild(infoVazio);
        Comboitem infoSim = new Comboitem("1 - Sim");
        infoSim.setValue("1");
        cmbLaboratorioInformaticaIes.appendChild(infoSim);
        Comboitem infoNao = new Comboitem("0 - Nao");
        infoNao.setValue("0");
        cmbLaboratorioInformaticaIes.appendChild(infoNao);
        selecionarComboPorValor(cmbLaboratorioInformaticaIes,
                ies.getLaboratorioInformatica() == null ? null : String.valueOf(ies.getLaboratorioInformatica()));

        cmbTipoLaboratorioIes.getItems().clear();
        Comboitem tipoVazio = new Comboitem("Nao informado");
        tipoVazio.setValue("");
        cmbTipoLaboratorioIes.appendChild(tipoVazio);
        TipoLaboratorioEnum[] tipos = TipoLaboratorioEnum.values();
        for (int i = 0; i < tipos.length; i++) {
            Comboitem item = new Comboitem(tipos[i].getCodigo() + " - " + tipos[i].getDescricao());
            item.setValue(String.valueOf(tipos[i].getCodigo()));
            cmbTipoLaboratorioIes.appendChild(item);
        }
        selecionarComboPorValor(cmbTipoLaboratorioIes,
                ies.getTipoLaboratorio() == null ? null : String.valueOf(ies.getTipoLaboratorio()));

        cmbUfLaboratorioIes.getItems().clear();
        Comboitem ufVazio = new Comboitem("Nao informado");
        ufVazio.setValue("");
        cmbUfLaboratorioIes.appendChild(ufVazio);
        EstadoEnum[] ufs = EstadoEnum.values();
        for (int i = 0; i < ufs.length; i++) {
            Comboitem item = new Comboitem(ufs[i].getCodigo() + " - " + ufs[i].getDescricao());
            item.setValue(String.valueOf(ufs[i].getCodigo()));
            cmbUfLaboratorioIes.appendChild(item);
        }
        selecionarComboPorValor(cmbUfLaboratorioIes,
                ies.getCodigoUfLaboratorio() == null ? null : String.valueOf(ies.getCodigoUfLaboratorio()));
    }

    private void popularCamposBasicos(Ies ies) {
        txtIdIesInep.setValue(safe(ies.getIdIesInep()));
        txtNomeLaboratorioIes.setValue(safe(ies.getNomeLaboratorio()));
        txtRegistroLaboratorioIes.setValue(safe(ies.getRegistroLaboratorioIes()));
        txtDescricaoAtividadesIes.setValue(safe(ies.getDescricaoAtividades()));
        txtPalavrasChaveIes.setValue(safe(ies.getPalavrasChave()));
        txtMunicipioLaboratorioIes.setValue(safe(ies.getCodigoMunicipioLaboratorio()));
    }

    private void popularCamposComplementares(Long iesId) throws Exception {
        boxCamposComplementaresIes.getChildren().clear();
        camposComplementares.clear();

        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (iesId != null) {
            valores.putAll(iesService.carregarCamposComplementaresPorCampoId(iesId));
        }

        List<LayoutCampo> campos = filtrarCamposComplementares(catalogoService.listarCamposModulo(ModulosLayout.IES_11));
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
            boxCamposComplementaresIes.appendChild(linha);
            camposComplementares.put(campo.getId(), textbox);
        }
    }

    private void preencherCamposView(Long iesId) throws Exception {
        lstViewCamposIes.getItems().clear();

        Map<Long, String> valores = iesService.carregarCamposComplementaresPorCampoId(iesId);
        Map<Long, String> rotulos = montarRotulosCampos(ModulosLayout.IES_11);

        if (valores == null || valores.isEmpty()) {
            Listitem vazio = new Listitem();
            vazio.appendChild(new Listcell("Nenhum campo complementar informado."));
            vazio.appendChild(new Listcell(""));
            lstViewCamposIes.appendChild(vazio);
            return;
        }

        for (Map.Entry<Long, String> entry : valores.entrySet()) {
            Listitem item = new Listitem();
            String rotulo = rotulos.get(entry.getKey());
            item.appendChild(new Listcell(rotulo != null ? rotulo : String.valueOf(entry.getKey())));
            item.appendChild(new Listcell(safe(entry.getValue())));
            lstViewCamposIes.appendChild(item);
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

    private int parseComboIntOrDefault(Combobox combo, int defaultValue) {
        return parseIntOrDefault(parseComboString(combo), defaultValue);
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
        lblErroListIes.setVisible(true);
        lblErroListIes.setValue(message);
    }

    private void showFormError(String message) {
        lblErroFormIes.setVisible(true);
        lblErroFormIes.setValue(message);
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
