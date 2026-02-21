package br.gov.inep.censo.web.zk.modulo;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.*;
import br.gov.inep.censo.service.AlunoService;
import br.gov.inep.censo.service.CatalogoService;
import br.gov.inep.censo.service.CursoAlunoService;
import br.gov.inep.censo.service.CursoService;
import br.gov.inep.censo.web.zk.AbstractBaseComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller MVC do modulo CursoAluno (Registro 42).
 */
public class CursoAlunoComposer extends AbstractBaseComposer {

    private static final long serialVersionUID = 1L;

    private CursoAlunoService cursoAlunoService() {
        return (CursoAlunoService) SpringUtil.getBean("cursoAlunoService");
    }

    private AlunoService alunoService() {
        return (AlunoService) SpringUtil.getBean("alunoService");
    }

    private CursoService cursoService() {
        return (CursoService) SpringUtil.getBean("cursoService");
    }

    private CatalogoService catalogoService() {
        return (CatalogoService) SpringUtil.getBean("catalogoService");
    }

    // Lista
    @Wire
    private Label lblErroListCursoAluno;
    @Wire
    private Label lblFlashListCursoAluno;
    @Wire
    private Listbox lstCursoAluno;

    // Formulario
    @Wire
    private Label lblErroFormCursoAluno;
    @Wire
    private Combobox cmbAlunoCursoAluno;
    @Wire
    private Combobox cmbCursoCursoAluno;
    @Wire
    private Textbox txtIdAlunoIesCursoAluno;
    @Wire
    private Textbox txtPeriodoRefCursoAluno;
    @Wire
    private Textbox txtCodigoPoloCursoAluno;
    @Wire
    private Combobox cmbTurnoCursoAluno;
    @Wire
    private Combobox cmbSituacaoCursoAluno;
    @Wire
    private Textbox txtCursoOrigemCursoAluno;
    @Wire
    private Textbox txtSemestreConclusaoCursoAluno;
    @Wire
    private Combobox cmbAlunoParforCursoAluno;
    @Wire
    private Combobox cmbSegundaLicCursoAluno;
    @Wire
    private Combobox cmbTipoSegundaLicCursoAluno;
    @Wire
    private Textbox txtSemestreIngressoCursoAluno;
    @Wire
    private Combobox cmbFormaVestibularCursoAluno;
    @Wire
    private Combobox cmbFormaEnemCursoAluno;
    @Wire
    private Combobox cmbFormaAvaliacaoSeriadaCursoAluno;
    @Wire
    private Combobox cmbFormaSelecaoSimplificadaCursoAluno;
    @Wire
    private Combobox cmbFormaEgressoBiLiCursoAluno;
    @Wire
    private Combobox cmbFormaPecGCursoAluno;
    @Wire
    private Combobox cmbFormaTransfExOfficioCursoAluno;
    @Wire
    private Combobox cmbFormaDecisaoJudicialCursoAluno;
    @Wire
    private Combobox cmbFormaVagasRemanescentesCursoAluno;
    @Wire
    private Combobox cmbFormaProgramasEspeciaisCursoAluno;
    @Wire
    private Vbox boxFinanciamentoCursoAluno;
    @Wire
    private Vbox boxApoioCursoAluno;
    @Wire
    private Vbox boxAtividadeCursoAluno;
    @Wire
    private Vbox boxReservaCursoAluno;
    @Wire
    private Vbox boxCamposComplementaresCursoAluno;

    private final Map<Long, Checkbox> checksFinanciamento = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Checkbox> checksApoioSocial = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Checkbox> checksAtividade = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Checkbox> checksReserva = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Textbox> camposComplementares = new LinkedHashMap<Long, Textbox>();

    @Listen("onCreate = #winCursoAlunoList")
    public void onCreateWinCursoAlunoList() {
        lblErroListCursoAluno.setVisible(false);
        lblErroListCursoAluno.setValue("");

        String flash = consumeFlash("flashCursoAlunoMessage");
        if (flash == null) {
            lblFlashListCursoAluno.setVisible(false);
            lblFlashListCursoAluno.setValue("");
        } else {
            lblFlashListCursoAluno.setVisible(true);
            lblFlashListCursoAluno.setValue(flash);
        }

        carregarLista();
    }

    @Listen("onClick = #btnNovoListCursoAluno")
    public void onClickBtnNovoListCursoAluno() {
        openSub("curso-aluno-list", "curso-aluno-form");
    }

    @Listen("onClick = #btnMenuListCursoAluno")
    public void onClickBtnMenuListCursoAluno() {
        goShell("dashboard");
    }

    @Listen("onCreate = #winCursoAlunoForm")
    public void onCreateWinCursoAlunoForm() {
        lblErroFormCursoAluno.setVisible(false);
        lblErroFormCursoAluno.setValue("");

        try {
            popularCombosBase();
            popularCombosOpcionais();
            popularOpcoes1N();
            popularCamposComplementares();
        } catch (Exception e) {
            showFormError("Falha ao carregar formulario do Registro 42.");
        }
    }

    @Listen("onClick = #btnVoltarFormCursoAluno")
    public void onClickBtnVoltarFormCursoAluno() {
        goShell("curso-aluno-list");
    }

    @Listen("onClick = #btnSalvarFormCursoAluno")
    public void onClickBtnSalvarFormCursoAluno() {
        lblErroFormCursoAluno.setVisible(false);
        lblErroFormCursoAluno.setValue("");

        try {
            Long alunoId = parseComboLong(cmbAlunoCursoAluno);
            Long cursoId = parseComboLong(cmbCursoCursoAluno);
            if (alunoId == null || cursoId == null) {
                throw new IllegalArgumentException("Selecione aluno e curso.");
            }

            CursoAluno cursoAluno = CursoAluno.builder()
                    .alunoId(alunoId)
                    .cursoId(cursoId)
                    .idAlunoIes(trimToEmpty(txtIdAlunoIesCursoAluno.getValue()))
                    .periodoReferencia(trimToEmpty(txtPeriodoRefCursoAluno.getValue()))
                    .codigoPoloEad(trimToNull(txtCodigoPoloCursoAluno.getValue()))
                    .turnoAluno(parseComboInteger(cmbTurnoCursoAluno))
                    .situacaoVinculo(parseComboInteger(cmbSituacaoCursoAluno))
                    .cursoOrigem(trimToNull(txtCursoOrigemCursoAluno.getValue()))
                    .semestreConclusao(trimToNull(txtSemestreConclusaoCursoAluno.getValue()))
                    .alunoParfor(Integer.valueOf(parseComboIntOrDefault(cmbAlunoParforCursoAluno, 0)))
                    .segundaLicenciaturaFormacao(Integer.valueOf(parseComboIntOrDefault(cmbSegundaLicCursoAluno, 0)))
                    .tipoSegundaLicenciaturaFormacao(parseComboInteger(cmbTipoSegundaLicCursoAluno))
                    .semestreIngresso(trimToNull(txtSemestreIngressoCursoAluno.getValue()))
                    .formaIngressoVestibular(Integer.valueOf(parseComboIntOrDefault(cmbFormaVestibularCursoAluno, 0)))
                    .formaIngressoEnem(Integer.valueOf(parseComboIntOrDefault(cmbFormaEnemCursoAluno, 0)))
                    .formaIngressoAvaliacaoSeriada(Integer.valueOf(parseComboIntOrDefault(cmbFormaAvaliacaoSeriadaCursoAluno, 0)))
                    .formaIngressoSelecaoSimplificada(Integer.valueOf(parseComboIntOrDefault(cmbFormaSelecaoSimplificadaCursoAluno, 0)))
                    .formaIngressoEgressoBiLi(Integer.valueOf(parseComboIntOrDefault(cmbFormaEgressoBiLiCursoAluno, 0)))
                    .formaIngressoPecG(Integer.valueOf(parseComboIntOrDefault(cmbFormaPecGCursoAluno, 0)))
                    .formaIngressoTransferenciaExofficio(Integer.valueOf(parseComboIntOrDefault(cmbFormaTransfExOfficioCursoAluno, 0)))
                    .formaIngressoDecisaoJudicial(Integer.valueOf(parseComboIntOrDefault(cmbFormaDecisaoJudicialCursoAluno, 0)))
                    .formaIngressoVagasRemanescentes(Integer.valueOf(parseComboIntOrDefault(cmbFormaVagasRemanescentesCursoAluno, 0)))
                    .formaIngressoProgramasEspeciais(Integer.valueOf(parseComboIntOrDefault(cmbFormaProgramasEspeciaisCursoAluno, 0)))
                    .build();

            long[] financiamentos = mapSelectedIds(checksFinanciamento);
            long[] apoio = mapSelectedIds(checksApoioSocial);
            long[] atividades = mapSelectedIds(checksAtividade);
            long[] reservas = mapSelectedIds(checksReserva);
            long[] opcaoIds = mergeArrays(financiamentos, apoio, atividades, reservas);

            Map<Long, String> extras = mapCamposComplementares(camposComplementares);
            cursoAlunoService().cadastrar(cursoAluno, opcaoIds, extras);
            putFlash("flashCursoAlunoMessage", "Registro 42 salvo com sucesso.");
            goShell("curso-aluno-list");
        } catch (Exception e) {
            showFormError(e.getMessage() != null ? e.getMessage() : "Falha ao salvar Registro 42.");
        }
    }

    private void carregarLista() {
        try {
            List<CursoAluno> vinculos = cursoAlunoService().listar();
            lstCursoAluno.getItems().clear();

            for (int i = 0; i < vinculos.size(); i++) {
                CursoAluno item = vinculos.get(i);
                Listitem linha = new Listitem();
                linha.appendChild(new Listcell(safe(item.getId())));
                linha.appendChild(new Listcell(safe(item.getPeriodoReferencia())));
                linha.appendChild(new Listcell(safe(item.getAlunoNome())));
                linha.appendChild(new Listcell(safe(item.getIdAlunoIes())));
                linha.appendChild(new Listcell(safe(item.getCursoNome())));
                linha.appendChild(new Listcell(safe(item.getCodigoCursoEmec())));
                linha.appendChild(new Listcell(safe(item.getTurnoAluno())));
                linha.appendChild(new Listcell(safe(item.getSituacaoVinculo())));
                linha.appendChild(new Listcell(safe(item.getSemestreIngresso())));
                linha.appendChild(new Listcell(safe(item.getFinanciamentosResumo())));
                linha.appendChild(new Listcell(safe(item.getApoioSocialResumo())));
                linha.appendChild(new Listcell(safe(item.getAtividadesResumo())));
                linha.appendChild(new Listcell(safe(item.getReservasResumo())));
                lstCursoAluno.appendChild(linha);
            }
        } catch (Exception e) {
            lblErroListCursoAluno.setVisible(true);
            lblErroListCursoAluno.setValue("Falha ao carregar listagem do Registro 42.");
        }
    }

    private void popularCombosBase() throws Exception {
        cmbAlunoCursoAluno.getItems().clear();
        Comboitem alunoPlaceholder = new Comboitem("Selecione...");
        alunoPlaceholder.setValue("");
        cmbAlunoCursoAluno.appendChild(alunoPlaceholder);

        List<Aluno> alunos = alunoService().listar();
        for (int i = 0; i < alunos.size(); i++) {
            Aluno aluno = alunos.get(i);
            Comboitem item = new Comboitem(aluno.getNome() + " - CPF " + safe(aluno.getCpf()));
            item.setValue(String.valueOf(aluno.getId()));
            cmbAlunoCursoAluno.appendChild(item);
        }

        cmbCursoCursoAluno.getItems().clear();
        Comboitem cursoPlaceholder = new Comboitem("Selecione...");
        cursoPlaceholder.setValue("");
        cmbCursoCursoAluno.appendChild(cursoPlaceholder);

        List<Curso> cursos = cursoService().listar();
        for (int i = 0; i < cursos.size(); i++) {
            Curso curso = cursos.get(i);
            Comboitem item = new Comboitem(safe(curso.getCodigoCursoEmec()) + " - " + safe(curso.getNome()));
            item.setValue(String.valueOf(curso.getId()));
            cmbCursoCursoAluno.appendChild(item);
        }

        txtPeriodoRefCursoAluno.setValue("2025");
    }

    private void popularCombosOpcionais() {
        popularComboComVazio(cmbTurnoCursoAluno, "1 - Matutino", "1", "2 - Vespertino", "2", "3 - Noturno", "3", "4 - Integral", "4");
        popularComboComVazio(cmbSituacaoCursoAluno,
                "1 - Cursando", "1",
                "2 - Matricula trancada", "2",
                "3 - Desvinculado", "3",
                "4 - Formado", "4",
                "5 - Transferido", "5");

        popularComboSimNao(cmbAlunoParforCursoAluno, true);
        popularComboSimNao(cmbSegundaLicCursoAluno, true);
        popularComboComVazio(cmbTipoSegundaLicCursoAluno,
                "1 - Segunda Licenciatura", "1",
                "2 - Formacao Pedagogica", "2");

        popularComboSimNao(cmbFormaVestibularCursoAluno, true);
        popularComboSimNao(cmbFormaEnemCursoAluno, true);
        popularComboSimNao(cmbFormaAvaliacaoSeriadaCursoAluno, true);
        popularComboSimNao(cmbFormaSelecaoSimplificadaCursoAluno, true);
        popularComboSimNao(cmbFormaEgressoBiLiCursoAluno, true);
        popularComboSimNao(cmbFormaPecGCursoAluno, true);
        popularComboSimNao(cmbFormaTransfExOfficioCursoAluno, true);
        popularComboSimNao(cmbFormaDecisaoJudicialCursoAluno, true);
        popularComboSimNao(cmbFormaVagasRemanescentesCursoAluno, true);
        popularComboSimNao(cmbFormaProgramasEspeciaisCursoAluno, true);
    }

    private void popularOpcoes1N() throws Exception {
        popularCheckboxes(catalogoService().listarOpcoesPorCategoria(CategoriasOpcao.CURSO_ALUNO_TIPO_FINANCIAMENTO),
                boxFinanciamentoCursoAluno, checksFinanciamento);
        popularCheckboxes(catalogoService().listarOpcoesPorCategoria(CategoriasOpcao.CURSO_ALUNO_APOIO_SOCIAL),
                boxApoioCursoAluno, checksApoioSocial);
        popularCheckboxes(catalogoService().listarOpcoesPorCategoria(CategoriasOpcao.CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR),
                boxAtividadeCursoAluno, checksAtividade);
        popularCheckboxes(catalogoService().listarOpcoesPorCategoria(CategoriasOpcao.CURSO_ALUNO_RESERVA_VAGA),
                boxReservaCursoAluno, checksReserva);
    }

    private void popularCheckboxes(List<OpcaoDominio> opcoes, Vbox container, Map<Long, Checkbox> target) {
        container.getChildren().clear();
        target.clear();

        for (int i = 0; i < opcoes.size(); i++) {
            OpcaoDominio opcao = opcoes.get(i);
            Checkbox check = new Checkbox(opcao.getNome());
            target.put(opcao.getId(), check);
            container.appendChild(check);
        }
    }

    private void popularCamposComplementares() throws Exception {
        boxCamposComplementaresCursoAluno.getChildren().clear();
        camposComplementares.clear();

        List<LayoutCampo> campos = filtrarCamposComplementares(catalogoService().listarCamposModulo(ModulosLayout.ALUNO_42));
        for (int i = 0; i < campos.size(); i++) {
            LayoutCampo campo = campos.get(i);

            Hbox linha = new Hbox();
            linha.setSpacing("6px");

            Label label = new Label("[" + campo.getNumeroCampo() + "] " + campo.getNomeCampo());
            label.setWidth("360px");

            Textbox textbox = new Textbox();
            textbox.setWidth("420px");

            linha.appendChild(label);
            linha.appendChild(textbox);
            boxCamposComplementaresCursoAluno.appendChild(linha);
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
            if ((numero >= 2 && numero <= 23) || (numero >= 29 && numero <= 39) ||
                    (numero >= 41 && numero <= 46) || (numero >= 48 && numero <= 55) ||
                    (numero >= 60 && numero <= 72)) {
                continue;
            }
            filtrados.add(campo);
        }
        return filtrados;
    }

    private void popularComboSimNao(Combobox combo, boolean defaultZero) {
        combo.getItems().clear();
        Comboitem nao = new Comboitem("0 - Nao");
        nao.setValue("0");
        combo.appendChild(nao);

        Comboitem sim = new Comboitem("1 - Sim");
        sim.setValue("1");
        combo.appendChild(sim);

        combo.setSelectedItem(defaultZero ? nao : sim);
    }

    private void popularComboComVazio(Combobox combo, String... labelValuePairs) {
        combo.getItems().clear();
        Comboitem vazio = new Comboitem("Nao informado");
        vazio.setValue("");
        combo.appendChild(vazio);
        combo.setSelectedItem(vazio);

        for (int i = 0; i + 1 < labelValuePairs.length; i += 2) {
            Comboitem item = new Comboitem(labelValuePairs[i]);
            item.setValue(labelValuePairs[i + 1]);
            combo.appendChild(item);
        }
    }

    private String parseComboString(Combobox combo) {
        if (combo == null) {
            return null;
        }
        Comboitem selected = combo.getSelectedItem();
        String value = selected != null ? String.valueOf(selected.getValue()) : trimToNull(combo.getValue());
        return trimToNull(value);
    }

    private Long parseComboLong(Combobox combo) {
        return parseLongOrNull(parseComboString(combo));
    }

    private Integer parseComboInteger(Combobox combo) {
        return parseIntegerOrNull(parseComboString(combo));
    }

    private int parseComboIntOrDefault(Combobox combo, int defaultValue) {
        return parseIntOrDefault(parseComboString(combo), defaultValue);
    }

    private long[] mergeArrays(long[] a, long[] b, long[] c, long[] d) {
        int size = a.length + b.length + c.length + d.length;
        long[] merged = new long[size];
        int idx = 0;

        for (int i = 0; i < a.length; i++) {
            merged[idx++] = a[i];
        }
        for (int i = 0; i < b.length; i++) {
            merged[idx++] = b[i];
        }
        for (int i = 0; i < c.length; i++) {
            merged[idx++] = c[i];
        }
        for (int i = 0; i < d.length; i++) {
            merged[idx++] = d[i];
        }
        return merged;
    }

    private void showFormError(String message) {
        lblErroFormCursoAluno.setVisible(true);
        lblErroFormCursoAluno.setValue(message);
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
