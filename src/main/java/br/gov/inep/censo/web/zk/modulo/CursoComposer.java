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
import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
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
import java.util.Set;
import java.util.HashSet;

/**
 * Controller MVC do modulo Curso (Registro 21).
 */
public class CursoComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_PAGINA = 10;

    private final CursoService cursoService = new CursoService();
    private final CatalogoService catalogoService = new CatalogoService();

    // Lista
    private Label lblErroListCurso;
    private Label lblFlashListCurso;
    private Label lblTotalListCurso;
    private Label lblPaginacaoListCurso;
    private Textbox txtImportacaoListCurso;
    private Listbox lstCursos;
    private Button btnPaginaAnteriorCurso;
    private Button btnPaginaProximaCurso;

    // Formulario
    private Label lblErroFormCurso;
    private Label lblTituloFormCurso;
    private Textbox txtCodigoCursoEmec;
    private Textbox txtNomeCurso;
    private Combobox cmbNivelAcademicoCurso;
    private Combobox cmbFormatoOfertaCurso;
    private Combobox cmbTeveAlunoVinculadoCurso;
    private Vbox boxRecursosCurso;
    private Vbox boxCamposComplementaresCurso;

    // Visualizacao
    private Label lblViewCursoId;
    private Label lblViewCursoCodigo;
    private Label lblViewCursoNome;
    private Label lblViewCursoNivel;
    private Label lblViewCursoFormato;
    private Label lblViewCursoVinculado;
    private Label lblViewCursoRecursos;
    private Listbox lstViewCamposCurso;

    private final Map<Long, Checkbox> checksRecursos = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Textbox> camposComplementares = new LinkedHashMap<Long, Textbox>();

    private int paginaAtual = 1;
    private int totalPaginas = 1;
    private Long cursoIdEdicao;
    private Long cursoIdVisualizacao;

    public void onCreate$winCursoList() {
        lblErroListCurso.setVisible(false);
        lblErroListCurso.setValue("");

        String flash = consumeFlash("flashCursoMessage");
        if (flash == null) {
            lblFlashListCurso.setVisible(false);
            lblFlashListCurso.setValue("");
        } else {
            lblFlashListCurso.setVisible(true);
            lblFlashListCurso.setValue(flash);
        }

        paginaAtual = parseIntOrDefault(currentRequest().getParameter("pagina"), 1);
        if (paginaAtual <= 0) {
            paginaAtual = 1;
        }

        carregarLista();
    }

    public void onClick$btnNovoListCurso() {
        openSub("curso-list", "curso-form");
    }

    public void onClick$btnMenuListCurso() {
        goShell("dashboard");
    }

    public void onClick$btnExportarListCurso() {
        redirect("/api/relatorios/cursos.txt");
    }

    public void onClick$btnImportarListCurso() {
        try {
            String conteudo = txtImportacaoListCurso.getValue();
            int total = cursoService.importarTxtPipe(conteudo);
            putFlash("flashHomeMessage", "Importacao de curso concluida: " + total + " registro(s).");
            putFlash("flashCursoMessage", "Importacao concluida: " + total + " registro(s).");
            goShell("curso-list");
        } catch (Exception e) {
            showListError("Falha ao importar TXT de curso.");
        }
    }

    public void onClick$btnPaginaAnteriorCurso() {
        if (paginaAtual > 1) {
            goShell("curso-list", paginaAtual - 1);
        }
    }

    public void onClick$btnPaginaProximaCurso() {
        if (paginaAtual < totalPaginas) {
            goShell("curso-list", paginaAtual + 1);
        }
    }

    public void onCreate$winCursoForm() {
        cursoIdEdicao = parseLongOrNull(currentRequest().getParameter("id"));
        lblErroFormCurso.setVisible(false);
        lblErroFormCurso.setValue("");

        try {
            Curso curso = cursoIdEdicao != null ? cursoService.buscarPorId(cursoIdEdicao) : new Curso();
            if (curso == null) {
                curso = new Curso();
                cursoIdEdicao = null;
            }

            lblTituloFormCurso.setValue(cursoIdEdicao == null ? "Novo Curso" : "Alterar Curso");
            popularCombos(curso);
            popularCamposBasicos(curso);
            popularRecursos(cursoIdEdicao);
            popularCamposComplementares(cursoIdEdicao);
        } catch (Exception e) {
            showFormError("Falha ao carregar formulario de curso.");
        }
    }

    public void onClick$btnVoltarFormCurso() {
        goShell("curso-list");
    }

    public void onClick$btnSalvarFormCurso() {
        lblErroFormCurso.setVisible(false);
        lblErroFormCurso.setValue("");

        try {
            Curso curso = Curso.builder()
                    .id(cursoIdEdicao)
                    .codigoCursoEmec(trimToEmpty(txtCodigoCursoEmec.getValue()))
                    .nome(trimToEmpty(txtNomeCurso.getValue()))
                    .nivelAcademico(parseComboString(cmbNivelAcademicoCurso))
                    .formatoOferta(parseComboString(cmbFormatoOfertaCurso))
                    .cursoTeveAlunoVinculado(Integer.valueOf(parseComboIntOrDefault(cmbTeveAlunoVinculadoCurso, 1)))
                    .build();

            long[] recursos = mapSelectedIds(checksRecursos);
            Map<Long, String> extras = mapCamposComplementares(camposComplementares);

            if (cursoIdEdicao == null) {
                cursoService.cadastrar(curso, recursos, extras);
                putFlash("flashCursoMessage", "Curso incluido com sucesso.");
            } else {
                cursoService.atualizar(curso, recursos, extras);
                putFlash("flashCursoMessage", "Curso alterado com sucesso.");
            }

            goShell("curso-list");
        } catch (Exception e) {
            showFormError(e.getMessage() != null ? e.getMessage() : "Falha ao salvar curso.");
        }
    }

    public void onCreate$winCursoView() {
        cursoIdVisualizacao = parseLongOrNull(currentRequest().getParameter("id"));
        if (cursoIdVisualizacao == null) {
            goShell("curso-list");
            return;
        }

        try {
            Curso curso = cursoService.buscarPorId(cursoIdVisualizacao);
            if (curso == null) {
                goShell("curso-list");
                return;
            }

            lblViewCursoId.setValue(safe(curso.getId()));
            lblViewCursoCodigo.setValue(safe(curso.getCodigoCursoEmec()));
            lblViewCursoNome.setValue(safe(curso.getNome()));

            String nivel = curso.getNivelAcademicoEnum() != null
                    ? curso.getNivelAcademicoEnum().getDescricao() + " (" + curso.getNivelAcademicoEnum().getCodigo() + ")"
                    : safe(curso.getNivelAcademico());
            lblViewCursoNivel.setValue(nivel);

            String formato = curso.getFormatoOfertaEnum() != null
                    ? curso.getFormatoOfertaEnum().getDescricao() + " (" + curso.getFormatoOfertaEnum().getCodigo() + ")"
                    : safe(curso.getFormatoOferta());
            lblViewCursoFormato.setValue(formato);

            lblViewCursoVinculado.setValue(safe(curso.getCursoTeveAlunoVinculado()));
            lblViewCursoRecursos.setValue(safe(curso.getRecursosTecnologiaAssistivaResumo()));

            preencherCamposView(cursoIdVisualizacao);
        } catch (Exception e) {
            goShell("curso-list");
        }
    }

    public void onClick$btnVoltarViewCurso() {
        goShell("curso-list");
    }

    public void onClick$btnEditarViewCurso() {
        openSub("curso-list", "curso-form", cursoIdVisualizacao);
    }

    private void carregarLista() {
        try {
            int total = cursoService.contar();
            totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
            if (paginaAtual > totalPaginas) {
                paginaAtual = totalPaginas;
            }

            List<Curso> cursos = cursoService.listarPaginado(paginaAtual, TAMANHO_PAGINA);
            lstCursos.getItems().clear();

            for (int i = 0; i < cursos.size(); i++) {
                adicionarLinhaCurso(cursos.get(i));
            }

            lblTotalListCurso.setValue(String.valueOf(total));
            lblPaginacaoListCurso.setValue("Pagina " + paginaAtual + " de " + totalPaginas);
            btnPaginaAnteriorCurso.setDisabled(paginaAtual <= 1);
            btnPaginaProximaCurso.setDisabled(paginaAtual >= totalPaginas);
        } catch (Exception e) {
            showListError("Falha ao carregar listagem de curso.");
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
        btnAlterar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                openSub("curso-list", "curso-form", curso.getId());
            }
        });
        acoes.appendChild(btnAlterar);

        Toolbarbutton btnMostrar = new Toolbarbutton("Mostrar");
        btnMostrar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                openSub("curso-list", "curso-view", curso.getId());
            }
        });
        acoes.appendChild(btnMostrar);

        Toolbarbutton btnExcluir = new Toolbarbutton("Excluir");
        btnExcluir.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                excluirCurso(curso.getId());
            }
        });
        acoes.appendChild(btnExcluir);

        Toolbarbutton btnExportar = new Toolbarbutton("Exportar TXT");
        btnExportar.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                exportarCurso(curso.getId());
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
            cursoService.excluir(id);
            lblFlashListCurso.setVisible(true);
            lblFlashListCurso.setValue("Curso excluido com sucesso.");
            carregarLista();
        } catch (Exception e) {
            showListError("Falha ao excluir curso.");
        }
    }

    private void exportarCurso(Long id) {
        if (id == null) {
            showListError("Falha ao exportar curso.");
            return;
        }
        redirect("/api/relatorios/cursos/" + id + ".txt");
    }

    private void popularCombos(Curso curso) {
        cmbNivelAcademicoCurso.getItems().clear();
        NivelAcademicoEnum[] niveis = NivelAcademicoEnum.values();
        for (int i = 0; i < niveis.length; i++) {
            Comboitem item = new Comboitem(niveis[i].getDescricao());
            item.setValue(niveis[i].getCodigo());
            cmbNivelAcademicoCurso.appendChild(item);
        }
        selecionarComboPorValor(cmbNivelAcademicoCurso, curso.getNivelAcademico());

        cmbFormatoOfertaCurso.getItems().clear();
        FormatoOfertaEnum[] formatos = FormatoOfertaEnum.values();
        for (int i = 0; i < formatos.length; i++) {
            Comboitem item = new Comboitem(formatos[i].getDescricao());
            item.setValue(formatos[i].getCodigo());
            cmbFormatoOfertaCurso.appendChild(item);
        }
        selecionarComboPorValor(cmbFormatoOfertaCurso, curso.getFormatoOferta());

        cmbTeveAlunoVinculadoCurso.getItems().clear();
        Comboitem sim = new Comboitem("1 - Sim");
        sim.setValue("1");
        cmbTeveAlunoVinculadoCurso.appendChild(sim);
        Comboitem nao = new Comboitem("0 - Nao");
        nao.setValue("0");
        cmbTeveAlunoVinculadoCurso.appendChild(nao);
        String selecionado = curso.getCursoTeveAlunoVinculado() == null ? "1" : String.valueOf(curso.getCursoTeveAlunoVinculado());
        selecionarComboPorValor(cmbTeveAlunoVinculadoCurso, selecionado);
    }

    private void popularCamposBasicos(Curso curso) {
        txtCodigoCursoEmec.setValue(safe(curso.getCodigoCursoEmec()));
        txtNomeCurso.setValue(safe(curso.getNome()));
    }

    private void popularRecursos(Long cursoId) throws Exception {
        boxRecursosCurso.getChildren().clear();
        checksRecursos.clear();

        Set<Long> selecionados = new HashSet<Long>();
        if (cursoId != null) {
            selecionados.addAll(cursoService.listarOpcaoRecursoAssistivoIds(cursoId));
        }

        List<OpcaoDominio> opcoes = catalogoService.listarOpcoesPorCategoria(
                CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA);
        for (int i = 0; i < opcoes.size(); i++) {
            OpcaoDominio opcao = opcoes.get(i);
            Checkbox check = new Checkbox(opcao.getNome());
            check.setChecked(selecionados.contains(opcao.getId()));
            checksRecursos.put(opcao.getId(), check);
            boxRecursosCurso.appendChild(check);
        }
    }

    private void popularCamposComplementares(Long cursoId) throws Exception {
        boxCamposComplementaresCurso.getChildren().clear();
        camposComplementares.clear();

        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (cursoId != null) {
            valores.putAll(cursoService.carregarCamposComplementaresPorCampoId(cursoId));
        }

        List<LayoutCampo> campos = filtrarCamposComplementares(catalogoService.listarCamposModulo(ModulosLayout.CURSO_21));
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
            boxCamposComplementaresCurso.appendChild(linha);
            camposComplementares.put(campo.getId(), textbox);
        }
    }

    private void preencherCamposView(Long cursoId) throws Exception {
        lstViewCamposCurso.getItems().clear();

        Map<Long, String> valores = cursoService.carregarCamposComplementaresPorCampoId(cursoId);
        Map<Long, String> rotulos = montarRotulosCampos(ModulosLayout.CURSO_21);

        if (valores == null || valores.isEmpty()) {
            Listitem vazio = new Listitem();
            vazio.appendChild(new Listcell("Nenhum campo complementar informado."));
            vazio.appendChild(new Listcell(""));
            lstViewCamposCurso.appendChild(vazio);
            return;
        }

        for (Map.Entry<Long, String> entry : valores.entrySet()) {
            Listitem item = new Listitem();
            String rotulo = rotulos.get(entry.getKey());
            item.appendChild(new Listcell(rotulo != null ? rotulo : String.valueOf(entry.getKey())));
            item.appendChild(new Listcell(safe(entry.getValue())));
            lstViewCamposCurso.appendChild(item);
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

    private int parseComboIntOrDefault(Combobox combo, int defaultValue) {
        String value = parseComboString(combo);
        return parseIntOrDefault(value, defaultValue);
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
        lblErroListCurso.setVisible(true);
        lblErroListCurso.setValue(message);
    }

    private void showFormError(String message) {
        lblErroFormCurso.setVisible(true);
        lblErroFormCurso.setValue(message);
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
