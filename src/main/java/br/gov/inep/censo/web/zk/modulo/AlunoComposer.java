package br.gov.inep.censo.web.zk.modulo;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.OpcaoDominio;
import br.gov.inep.censo.model.enums.CorRacaEnum;
import br.gov.inep.censo.model.enums.NacionalidadeEnum;
import br.gov.inep.censo.service.AlunoService;
import br.gov.inep.censo.service.CatalogoService;
import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller MVC do modulo Aluno (Registro 41).
 */
public class AlunoComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_PAGINA = 10;

    private final AlunoService alunoService = new AlunoService();
    private final CatalogoService catalogoService = new CatalogoService();

    // Lista
    private Label lblErroListAluno;
    private Label lblFlashListAluno;
    private Label lblTotalListAluno;
    private Label lblPaginacaoListAluno;
    private Textbox txtImportacaoListAluno;
    private Listbox lstAlunos;
    private Button btnPaginaAnteriorAluno;
    private Button btnPaginaProximaAluno;

    // Formulario
    private Label lblErroFormAluno;
    private Label lblTituloFormAluno;
    private Textbox txtIdAlunoInep;
    private Textbox txtNomeAluno;
    private Textbox txtCpfAluno;
    private Datebox dtNascimentoAluno;
    private Combobox cmbCorRacaAluno;
    private Combobox cmbNacionalidadeAluno;
    private Textbox txtUfNascimentoAluno;
    private Textbox txtMunicipioNascimentoAluno;
    private Textbox txtPaisOrigemAluno;
    private Vbox boxDeficienciaAluno;
    private Vbox boxCamposComplementaresAluno;

    // Visualizacao
    private Label lblViewAlunoId;
    private Label lblViewAlunoIdInep;
    private Label lblViewAlunoNome;
    private Label lblViewAlunoCpf;
    private Label lblViewAlunoNascimento;
    private Label lblViewAlunoCorRaca;
    private Label lblViewAlunoNacionalidade;
    private Label lblViewAlunoUf;
    private Label lblViewAlunoMunicipio;
    private Label lblViewAlunoPais;
    private Label lblViewAlunoDeficiencia;
    private Listbox lstViewCamposAluno;

    private final Map<Long, Checkbox> checksDeficiencia = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Textbox> camposComplementares = new LinkedHashMap<Long, Textbox>();

    private int paginaAtual = 1;
    private int totalPaginas = 1;
    private Long alunoIdEdicao;
    private Long alunoIdVisualizacao;

    public void onCreate$winAlunoList() {
        lblErroListAluno.setVisible(false);
        lblErroListAluno.setValue("");

        String flash = consumeFlash("flashAlunoMessage");
        if (flash == null) {
            lblFlashListAluno.setVisible(false);
            lblFlashListAluno.setValue("");
        } else {
            lblFlashListAluno.setVisible(true);
            lblFlashListAluno.setValue(flash);
        }

        paginaAtual = parseIntOrDefault(currentRequest().getParameter("pagina"), 1);
        if (paginaAtual <= 0) {
            paginaAtual = 1;
        }

        carregarLista();
    }

    public void onClick$btnNovoListAluno() {
        openSub("aluno-list", "aluno-form");
    }

    public void onClick$btnMenuListAluno() {
        goShell("dashboard");
    }

    public void onClick$btnExportarListAluno() {
        redirect("/api/relatorios/alunos.txt");
    }

    public void onClick$btnImportarListAluno() {
        try {
            String conteudo = txtImportacaoListAluno.getValue();
            int total = alunoService.importarTxtPipe(conteudo);
            putFlash("flashHomeMessage", "Importacao de aluno concluida: " + total + " registro(s).");
            putFlash("flashAlunoMessage", "Importacao concluida: " + total + " registro(s).");
            goShell("aluno-list");
        } catch (Exception e) {
            showListError("Falha ao importar TXT de aluno.");
        }
    }

    public void onClick$btnPaginaAnteriorAluno() {
        if (paginaAtual > 1) {
            goShell("aluno-list", paginaAtual - 1);
        }
    }

    public void onClick$btnPaginaProximaAluno() {
        if (paginaAtual < totalPaginas) {
            goShell("aluno-list", paginaAtual + 1);
        }
    }

    public void onCreate$winAlunoForm() {
        alunoIdEdicao = parseLongOrNull(currentRequest().getParameter("id"));
        lblErroFormAluno.setVisible(false);
        lblErroFormAluno.setValue("");

        try {
            Aluno aluno = alunoIdEdicao != null ? alunoService.buscarPorId(alunoIdEdicao) : new Aluno();
            if (aluno == null) {
                aluno = new Aluno();
                alunoIdEdicao = null;
            }

            lblTituloFormAluno.setValue(alunoIdEdicao == null ? "Novo Aluno" : "Alterar Aluno");
            popularCombos(aluno);
            popularCamposBasicos(aluno);
            popularDeficiencias(alunoIdEdicao);
            popularCamposComplementares(alunoIdEdicao);
        } catch (Exception e) {
            showFormError("Falha ao carregar formulario de aluno.");
        }
    }

    public void onClick$btnVoltarFormAluno() {
        goShell("aluno-list");
    }

    public void onClick$btnSalvarFormAluno() {
        lblErroFormAluno.setVisible(false);
        lblErroFormAluno.setValue("");

        try {
            Date dataNascimento = toSqlDate(dtNascimentoAluno.getValue());
            Integer nacionalidade = parseComboInteger(cmbNacionalidadeAluno);
            if (nacionalidade == null) {
                nacionalidade = Integer.valueOf(1);
            }

            String paisOrigem = trimToEmpty(txtPaisOrigemAluno.getValue());
            if (paisOrigem.length() == 0) {
                paisOrigem = "BRA";
            }

            Aluno aluno = Aluno.builder()
                    .id(alunoIdEdicao)
                    .idAlunoInep(parseLongOrNull(txtIdAlunoInep.getValue()))
                    .nome(trimToEmpty(txtNomeAluno.getValue()))
                    .cpf(trimToEmpty(txtCpfAluno.getValue()))
                    .dataNascimento(dataNascimento)
                    .corRaca(parseComboInteger(cmbCorRacaAluno))
                    .nacionalidade(nacionalidade)
                    .ufNascimento(trimToEmpty(txtUfNascimentoAluno.getValue()))
                    .municipioNascimento(trimToEmpty(txtMunicipioNascimentoAluno.getValue()))
                    .paisOrigem(paisOrigem)
                    .build();

            long[] opcaoDeficienciaIds = mapSelectedIds(checksDeficiencia);
            Map<Long, String> extras = mapCamposComplementares(camposComplementares);

            if (alunoIdEdicao == null) {
                alunoService.cadastrar(aluno, opcaoDeficienciaIds, extras);
                putFlash("flashHomeMessage", "Cadastro de aluno realizado com sucesso.");
                putFlash("flashAlunoMessage", "Aluno incluido com sucesso.");
            } else {
                alunoService.atualizar(aluno, opcaoDeficienciaIds, extras);
                putFlash("flashAlunoMessage", "Aluno alterado com sucesso.");
            }

            goShell("aluno-list");
        } catch (Exception e) {
            showFormError(e.getMessage() != null ? e.getMessage() : "Falha ao salvar aluno.");
        }
    }

    public void onCreate$winAlunoView() {
        alunoIdVisualizacao = parseLongOrNull(currentRequest().getParameter("id"));
        if (alunoIdVisualizacao == null) {
            goShell("aluno-list");
            return;
        }

        try {
            Aluno aluno = alunoService.buscarPorId(alunoIdVisualizacao);
            if (aluno == null) {
                goShell("aluno-list");
                return;
            }

            lblViewAlunoId.setValue(safe(aluno.getId()));
            lblViewAlunoIdInep.setValue(safe(aluno.getIdAlunoInep()));
            lblViewAlunoNome.setValue(safe(aluno.getNome()));
            lblViewAlunoCpf.setValue(safe(aluno.getCpf()));
            lblViewAlunoNascimento.setValue(safe(aluno.getDataNascimento()));

            String corRaca = aluno.getCorRacaEnum() != null
                    ? aluno.getCorRacaEnum().getDescricao() + " (" + aluno.getCorRaca() + ")"
                    : safe(aluno.getCorRaca());
            lblViewAlunoCorRaca.setValue(corRaca);

            String nacionalidade = aluno.getNacionalidadeEnum() != null
                    ? aluno.getNacionalidadeEnum().getDescricao() + " (" + aluno.getNacionalidade() + ")"
                    : safe(aluno.getNacionalidade());
            lblViewAlunoNacionalidade.setValue(nacionalidade);

            lblViewAlunoUf.setValue(safe(aluno.getUfNascimento()));
            lblViewAlunoMunicipio.setValue(safe(aluno.getMunicipioNascimento()));
            lblViewAlunoPais.setValue(safe(aluno.getPaisOrigem()));
            lblViewAlunoDeficiencia.setValue(safe(aluno.getTiposDeficienciaResumo()));

            preencherCamposView(alunoIdVisualizacao);
        } catch (Exception e) {
            goShell("aluno-list");
        }
    }

    public void onClick$btnVoltarViewAluno() {
        goShell("aluno-list");
    }

    public void onClick$btnEditarViewAluno() {
        openSub("aluno-list", "aluno-form", alunoIdVisualizacao);
    }

    private void carregarLista() {
        try {
            int total = alunoService.contar();
            totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
            if (paginaAtual > totalPaginas) {
                paginaAtual = totalPaginas;
            }

            List<Aluno> alunos = alunoService.listarPaginado(paginaAtual, TAMANHO_PAGINA);
            lstAlunos.getItems().clear();

            for (int i = 0; i < alunos.size(); i++) {
                adicionarLinhaAluno(alunos.get(i));
            }

            lblTotalListAluno.setValue(String.valueOf(total));
            lblPaginacaoListAluno.setValue("Pagina " + paginaAtual + " de " + totalPaginas);
            btnPaginaAnteriorAluno.setDisabled(paginaAtual <= 1);
            btnPaginaProximaAluno.setDisabled(paginaAtual >= totalPaginas);
        } catch (Exception e) {
            showListError("Falha ao carregar listagem de aluno.");
        }
    }

    private void adicionarLinhaAluno(final Aluno aluno) {
        Listitem item = new Listitem();
        item.appendChild(new Listcell(safe(aluno.getId())));
        item.appendChild(new Listcell(safe(aluno.getIdAlunoInep())));
        item.appendChild(new Listcell(safe(aluno.getNome())));
        item.appendChild(new Listcell(safe(aluno.getCpf())));
        item.appendChild(new Listcell(safe(aluno.getDataNascimento())));
        item.appendChild(new Listcell(safe(aluno.getNacionalidade())));

        Hbox acoes = new Hbox();
        acoes.setSpacing("6px");

        Toolbarbutton btnAlterar = new Toolbarbutton("Alterar");
        btnAlterar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                openSub("aluno-list", "aluno-form", aluno.getId());
            }
        });
        acoes.appendChild(btnAlterar);

        Toolbarbutton btnMostrar = new Toolbarbutton("Mostrar");
        btnMostrar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                openSub("aluno-list", "aluno-view", aluno.getId());
            }
        });
        acoes.appendChild(btnMostrar);

        Toolbarbutton btnExcluir = new Toolbarbutton("Excluir");
        btnExcluir.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                excluirAluno(aluno.getId());
            }
        });
        acoes.appendChild(btnExcluir);

        Toolbarbutton btnExportar = new Toolbarbutton("Exportar TXT");
        btnExportar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                exportarAluno(aluno.getId());
            }
        });
        acoes.appendChild(btnExportar);

        Listcell celulaAcoes = new Listcell();
        celulaAcoes.appendChild(acoes);
        item.appendChild(celulaAcoes);
        lstAlunos.appendChild(item);
    }

    private void excluirAluno(Long id) {
        try {
            alunoService.excluir(id);
            lblFlashListAluno.setVisible(true);
            lblFlashListAluno.setValue("Aluno excluido com sucesso.");
            carregarLista();
        } catch (Exception e) {
            showListError("Falha ao excluir aluno.");
        }
    }

    private void exportarAluno(Long id) {
        if (id == null) {
            showListError("Falha ao exportar aluno.");
            return;
        }
        redirect("/api/relatorios/alunos/" + id + ".txt");
    }

    private void popularCombos(Aluno aluno) {
        cmbCorRacaAluno.getItems().clear();
        Comboitem corVazia = new Comboitem("Nao informado");
        corVazia.setValue("");
        cmbCorRacaAluno.appendChild(corVazia);

        CorRacaEnum[] cores = CorRacaEnum.values();
        for (int i = 0; i < cores.length; i++) {
            Comboitem item = new Comboitem(cores[i].getCodigo() + " - " + cores[i].getDescricao());
            item.setValue(String.valueOf(cores[i].getCodigo()));
            cmbCorRacaAluno.appendChild(item);
        }
        selecionarComboPorValor(cmbCorRacaAluno, aluno.getCorRaca() == null ? null : String.valueOf(aluno.getCorRaca()));

        cmbNacionalidadeAluno.getItems().clear();
        NacionalidadeEnum[] nacionalidades = NacionalidadeEnum.values();
        for (int i = 0; i < nacionalidades.length; i++) {
            Comboitem item = new Comboitem(nacionalidades[i].getCodigo() + " - " + nacionalidades[i].getDescricao());
            item.setValue(String.valueOf(nacionalidades[i].getCodigo()));
            cmbNacionalidadeAluno.appendChild(item);
        }
        String nacionalidadeSelecionada = aluno.getNacionalidade() == null ? "1" : String.valueOf(aluno.getNacionalidade());
        selecionarComboPorValor(cmbNacionalidadeAluno, nacionalidadeSelecionada);
    }

    private void popularCamposBasicos(Aluno aluno) {
        txtIdAlunoInep.setValue(safe(aluno.getIdAlunoInep()));
        txtNomeAluno.setValue(safe(aluno.getNome()));
        txtCpfAluno.setValue(safe(aluno.getCpf()));
        dtNascimentoAluno.setValue(aluno.getDataNascimento() != null ? new java.util.Date(aluno.getDataNascimento().getTime()) : null);
        txtUfNascimentoAluno.setValue(safe(aluno.getUfNascimento()));
        txtMunicipioNascimentoAluno.setValue(safe(aluno.getMunicipioNascimento()));
        txtPaisOrigemAluno.setValue(aluno.getPaisOrigem() != null && aluno.getPaisOrigem().trim().length() > 0
                ? aluno.getPaisOrigem()
                : "BRA");
    }

    private void popularDeficiencias(Long alunoId) throws Exception {
        boxDeficienciaAluno.getChildren().clear();
        checksDeficiencia.clear();

        Set<Long> selecionados = new HashSet<Long>();
        if (alunoId != null) {
            selecionados.addAll(alunoService.listarOpcaoDeficienciaIds(alunoId));
        }

        List<OpcaoDominio> opcoes = catalogoService.listarOpcoesPorCategoria(CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA);
        for (int i = 0; i < opcoes.size(); i++) {
            OpcaoDominio opcao = opcoes.get(i);
            Checkbox check = new Checkbox(opcao.getNome());
            check.setChecked(selecionados.contains(opcao.getId()));
            checksDeficiencia.put(opcao.getId(), check);
            boxDeficienciaAluno.appendChild(check);
        }
    }

    private void popularCamposComplementares(Long alunoId) throws Exception {
        boxCamposComplementaresAluno.getChildren().clear();
        camposComplementares.clear();

        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (alunoId != null) {
            valores.putAll(alunoService.carregarCamposComplementaresPorCampoId(alunoId));
        }

        List<LayoutCampo> campos = filtrarCamposComplementares(catalogoService.listarCamposModulo(ModulosLayout.ALUNO_41));
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
            boxCamposComplementaresAluno.appendChild(linha);
            camposComplementares.put(campo.getId(), textbox);
        }
    }

    private void preencherCamposView(Long alunoId) throws Exception {
        lstViewCamposAluno.getItems().clear();

        Map<Long, String> valores = alunoService.carregarCamposComplementaresPorCampoId(alunoId);
        Map<Long, String> rotulos = montarRotulosCampos(ModulosLayout.ALUNO_41);

        if (valores == null || valores.isEmpty()) {
            Listitem vazio = new Listitem();
            vazio.appendChild(new Listcell("Nenhum campo complementar informado."));
            vazio.appendChild(new Listcell(""));
            lstViewCamposAluno.appendChild(vazio);
            return;
        }

        for (Map.Entry<Long, String> entry : valores.entrySet()) {
            Listitem item = new Listitem();
            String rotulo = rotulos.get(entry.getKey());
            item.appendChild(new Listcell(rotulo != null ? rotulo : String.valueOf(entry.getKey())));
            item.appendChild(new Listcell(safe(entry.getValue())));
            lstViewCamposAluno.appendChild(item);
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
            if (numero == 1 || numero == 2 || numero == 3 || numero == 4 || numero == 6 || numero == 7 ||
                    numero == 8 || numero == 9 || numero == 10 || numero == 11 || (numero >= 13 && numero <= 22)) {
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

    private Integer parseComboInteger(Combobox combo) {
        if (combo == null) {
            return null;
        }

        Comboitem selected = combo.getSelectedItem();
        String value = selected != null ? String.valueOf(selected.getValue()) : trimToNull(combo.getValue());
        return parseIntegerOrNull(value);
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
        lblErroListAluno.setVisible(true);
        lblErroListAluno.setValue(message);
    }

    private void showFormError(String message) {
        lblErroFormAluno.setVisible(true);
        lblErroFormAluno.setValue(message);
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
