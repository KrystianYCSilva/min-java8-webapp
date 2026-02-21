package br.gov.inep.censo.web.zk.modulo;

import br.gov.inep.censo.domain.CategoriasOpcao;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.model.CursoAluno;
import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.OpcaoDominio;
import br.gov.inep.censo.service.AlunoService;
import br.gov.inep.censo.service.CatalogoService;
import br.gov.inep.censo.service.CursoAlunoService;
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
 * ViewModel MVVM do modulo CursoAluno (Registro 42).
 * Substitui CursoAlunoComposer para list e form.
 */
public class CursoAlunoViewModel extends AbstractBaseViewModel {

    // --- Estado da listagem ---
    private String erroList;
    private boolean erroListVisivel;
    private String flashList;
    private boolean flashListVisivel;

    // Listbox construido programaticamente no @Init
    private Listbox lstCursoAluno;

    // --- Estado do formulario ---
    private String erroForm;
    private boolean erroFormVisivel;

    // Campos basicos do formulario
    private String alunIdSelecionado;
    private String cursoIdSelecionado;
    private String idAlunoIes;
    private String periodoReferencia;
    private String codigoPolo;
    private String turnoSelecionado;
    private String situacaoSelecionada;
    private String cursoOrigem;
    private String semestreConclusao;
    private String alunoParforSelecionado;
    private String segundaLicSelecionada;
    private String tipoSegundaLicSelecionado;
    private String semestreIngresso;
    private String formaVestibularSelecionado;
    private String formaEnemSelecionado;
    private String formaAvaliacaoSeriadaSelecionado;
    private String formaSelecaoSimplificadaSelecionado;
    private String formaEgressoBiLiSelecionado;
    private String formaPecGSelecionado;
    private String formaTransfExOffSelecionado;
    private String formaDecisaoJudicialSelecionado;
    private String formaVagasRemanescentesSelecionado;
    private String formaProgramasEspeciaisSelecionado;

    // Combos
    private List<Comboitem> itensAluno;
    private List<Comboitem> itensCurso;
    private List<Comboitem> itensTurno;
    private List<Comboitem> itensSituacao;
    private List<Comboitem> itensSimNao;
    private List<Comboitem> itensTipoSegundaLic;

    // Campos dinamicos 1..N
    private Vbox boxFinanciamento;
    private Vbox boxApoio;
    private Vbox boxAtividade;
    private Vbox boxReserva;
    private Vbox boxCamposComplementares;
    private final Map<Long, Checkbox> checksFinanciamento = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Checkbox> checksApoio = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Checkbox> checksAtividade = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Checkbox> checksReserva = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Textbox> camposComplementares = new LinkedHashMap<Long, Textbox>();

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

    // =====================================================================
    // @Init
    // =====================================================================

    @Init
    public void init() {
        String req = currentRequest() != null ? currentRequest().getServletPath() : "";
        if (req.contains("curso-aluno-list")) {
            initList();
        } else if (req.contains("curso-aluno-form")) {
            initForm();
        }
    }

    // --- Lista ---

    private void initList() {
        erroListVisivel = false;
        erroList = "";

        String flash = consumeFlash("flashCursoAlunoMessage");
        flashListVisivel = flash != null;
        flashList = flash != null ? flash : "";

        lstCursoAluno = new Listbox();
        lstCursoAluno.setWidth("100%");

        Listhead head = new Listhead();
        head.setSizable(true);
        head.appendChild(new Listheader("ID", null, "60px"));
        head.appendChild(new Listheader("Periodo", null, "80px"));
        head.appendChild(new Listheader("Aluno", null, "180px"));
        head.appendChild(new Listheader("ID Aluno IES", null, "110px"));
        head.appendChild(new Listheader("Curso", null, "170px"));
        head.appendChild(new Listheader("Codigo Curso E-MEC", null, "120px"));
        head.appendChild(new Listheader("Turno", null, "60px"));
        head.appendChild(new Listheader("Situacao", null, "70px"));
        head.appendChild(new Listheader("Semestre Ingresso", null, "120px"));
        head.appendChild(new Listheader("Financiamentos", null, "130px"));
        head.appendChild(new Listheader("Apoio Social", null, "120px"));
        head.appendChild(new Listheader("Atividades", null, "120px"));
        head.appendChild(new Listheader("Reservas", null, "120px"));
        lstCursoAluno.appendChild(head);

        carregarLista();
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
            erroListVisivel = true;
            erroList = "Falha ao carregar listagem do Registro 42.";
        }
    }

    // --- Formulario ---

    private void initForm() {
        erroFormVisivel = false;
        erroForm = "";
        try {
            popularCombosAlunoCurso();
            popularCombosForm();
            popularOpcoes1N();
            popularCamposComplementaresForm();
            periodoReferencia = "2025";
            // defaults SimNao = "0"
            alunoParforSelecionado = "0";
            segundaLicSelecionada = "0";
            formaVestibularSelecionado = "0";
            formaEnemSelecionado = "0";
            formaAvaliacaoSeriadaSelecionado = "0";
            formaSelecaoSimplificadaSelecionado = "0";
            formaEgressoBiLiSelecionado = "0";
            formaPecGSelecionado = "0";
            formaTransfExOffSelecionado = "0";
            formaDecisaoJudicialSelecionado = "0";
            formaVagasRemanescentesSelecionado = "0";
            formaProgramasEspeciaisSelecionado = "0";
        } catch (Exception e) {
            erroFormVisivel = true;
            erroForm = "Falha ao carregar formulario do Registro 42.";
        }
    }

    private void popularCombosAlunoCurso() throws Exception {
        itensAluno = new ArrayList<Comboitem>();
        Comboitem alunoPlaceholder = new Comboitem("Selecione...");
        alunoPlaceholder.setValue("");
        itensAluno.add(alunoPlaceholder);
        List<Aluno> alunos = alunoService().listar();
        for (int i = 0; i < alunos.size(); i++) {
            Aluno aluno = alunos.get(i);
            Comboitem item = new Comboitem(aluno.getNome() + " - CPF " + safe(aluno.getCpf()));
            item.setValue(String.valueOf(aluno.getId()));
            itensAluno.add(item);
        }

        itensCurso = new ArrayList<Comboitem>();
        Comboitem cursoPlaceholder = new Comboitem("Selecione...");
        cursoPlaceholder.setValue("");
        itensCurso.add(cursoPlaceholder);
        List<Curso> cursos = cursoService().listar();
        for (int i = 0; i < cursos.size(); i++) {
            Curso curso = cursos.get(i);
            Comboitem item = new Comboitem(safe(curso.getCodigoCursoEmec()) + " - " + safe(curso.getNome()));
            item.setValue(String.valueOf(curso.getId()));
            itensCurso.add(item);
        }
    }

    private void popularCombosForm() {
        itensTurno = montarComboComVazio(
                "1 - Matutino", "1", "2 - Vespertino", "2",
                "3 - Noturno", "3", "4 - Integral", "4");
        itensSituacao = montarComboComVazio(
                "1 - Cursando", "1", "2 - Matricula trancada", "2",
                "3 - Desvinculado", "3", "4 - Formado", "4", "5 - Transferido", "5");
        itensSimNao = new ArrayList<Comboitem>();
        Comboitem nao = new Comboitem("0 - Nao");
        nao.setValue("0");
        itensSimNao.add(nao);
        Comboitem sim = new Comboitem("1 - Sim");
        sim.setValue("1");
        itensSimNao.add(sim);
        itensTipoSegundaLic = montarComboComVazio(
                "1 - Segunda Licenciatura", "1", "2 - Formacao Pedagogica", "2");
    }

    private List<Comboitem> montarComboComVazio(String... labelValuePairs) {
        List<Comboitem> itens = new ArrayList<Comboitem>();
        Comboitem vazio = new Comboitem("Nao informado");
        vazio.setValue("");
        itens.add(vazio);
        for (int i = 0; i + 1 < labelValuePairs.length; i += 2) {
            Comboitem item = new Comboitem(labelValuePairs[i]);
            item.setValue(labelValuePairs[i + 1]);
            itens.add(item);
        }
        return itens;
    }

    private void popularOpcoes1N() throws Exception {
        boxFinanciamento = new Vbox();
        boxFinanciamento.setSpacing("6px");
        popularCheckboxes(catalogoService().listarOpcoesPorCategoria(
                CategoriasOpcao.CURSO_ALUNO_TIPO_FINANCIAMENTO), boxFinanciamento, checksFinanciamento);

        boxApoio = new Vbox();
        boxApoio.setSpacing("6px");
        popularCheckboxes(catalogoService().listarOpcoesPorCategoria(
                CategoriasOpcao.CURSO_ALUNO_APOIO_SOCIAL), boxApoio, checksApoio);

        boxAtividade = new Vbox();
        boxAtividade.setSpacing("6px");
        popularCheckboxes(catalogoService().listarOpcoesPorCategoria(
                CategoriasOpcao.CURSO_ALUNO_ATIVIDADE_EXTRACURRICULAR), boxAtividade, checksAtividade);

        boxReserva = new Vbox();
        boxReserva.setSpacing("6px");
        popularCheckboxes(catalogoService().listarOpcoesPorCategoria(
                CategoriasOpcao.CURSO_ALUNO_RESERVA_VAGA), boxReserva, checksReserva);
    }

    private void popularCheckboxes(List<OpcaoDominio> opcoes, Vbox container, Map<Long, Checkbox> target) {
        target.clear();
        for (int i = 0; i < opcoes.size(); i++) {
            OpcaoDominio opcao = opcoes.get(i);
            Checkbox check = new Checkbox(opcao.getNome());
            target.put(opcao.getId(), check);
            container.appendChild(check);
        }
    }

    private void popularCamposComplementaresForm() throws Exception {
        boxCamposComplementares = new Vbox();
        boxCamposComplementares.setSpacing("6px");
        camposComplementares.clear();
        List<LayoutCampo> campos = filtrarCamposComplementares(
                catalogoService().listarCamposModulo(ModulosLayout.ALUNO_42));
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
            if ((numero >= 2 && numero <= 23) || (numero >= 29 && numero <= 39) ||
                    (numero >= 41 && numero <= 46) || (numero >= 48 && numero <= 55) ||
                    (numero >= 60 && numero <= 72)) {
                continue;
            }
            filtrados.add(campo);
        }
        return filtrados;
    }

    // =====================================================================
    // @Commands
    // =====================================================================

    @Command
    public void novo() {
        openSub("curso-aluno-list", "curso-aluno-form");
    }

    @Command
    public void voltar() {
        goShell("curso-aluno-list");
    }

    @Command
    @NotifyChange({"erroForm","erroFormVisivel"})
    public void salvar() {
        erroFormVisivel = false;
        erroForm = "";
        try {
            Long alunoId = parseLongOrNull(alunIdSelecionado);
            Long cursoId = parseLongOrNull(cursoIdSelecionado);
            if (alunoId == null || cursoId == null) {
                throw new IllegalArgumentException("Selecione aluno e curso.");
            }

            CursoAluno cursoAluno = CursoAluno.builder()
                    .alunoId(alunoId)
                    .cursoId(cursoId)
                    .idAlunoIes(trimToEmpty(idAlunoIes))
                    .periodoReferencia(trimToEmpty(periodoReferencia))
                    .codigoPoloEad(trimToNull(codigoPolo))
                    .turnoAluno(parseIntegerOrNull(turnoSelecionado))
                    .situacaoVinculo(parseIntegerOrNull(situacaoSelecionada))
                    .cursoOrigem(trimToNull(cursoOrigem))
                    .semestreConclusao(trimToNull(semestreConclusao))
                    .alunoParfor(Integer.valueOf(parseIntOrDefault(alunoParforSelecionado, 0)))
                    .segundaLicenciaturaFormacao(Integer.valueOf(parseIntOrDefault(segundaLicSelecionada, 0)))
                    .tipoSegundaLicenciaturaFormacao(parseIntegerOrNull(tipoSegundaLicSelecionado))
                    .semestreIngresso(trimToNull(semestreIngresso))
                    .formaIngressoVestibular(Integer.valueOf(parseIntOrDefault(formaVestibularSelecionado, 0)))
                    .formaIngressoEnem(Integer.valueOf(parseIntOrDefault(formaEnemSelecionado, 0)))
                    .formaIngressoAvaliacaoSeriada(Integer.valueOf(parseIntOrDefault(formaAvaliacaoSeriadaSelecionado, 0)))
                    .formaIngressoSelecaoSimplificada(Integer.valueOf(parseIntOrDefault(formaSelecaoSimplificadaSelecionado, 0)))
                    .formaIngressoEgressoBiLi(Integer.valueOf(parseIntOrDefault(formaEgressoBiLiSelecionado, 0)))
                    .formaIngressoPecG(Integer.valueOf(parseIntOrDefault(formaPecGSelecionado, 0)))
                    .formaIngressoTransferenciaExofficio(Integer.valueOf(parseIntOrDefault(formaTransfExOffSelecionado, 0)))
                    .formaIngressoDecisaoJudicial(Integer.valueOf(parseIntOrDefault(formaDecisaoJudicialSelecionado, 0)))
                    .formaIngressoVagasRemanescentes(Integer.valueOf(parseIntOrDefault(formaVagasRemanescentesSelecionado, 0)))
                    .formaIngressoProgramasEspeciais(Integer.valueOf(parseIntOrDefault(formaProgramasEspeciaisSelecionado, 0)))
                    .build();

            long[] financiamentos = mapSelectedIds(checksFinanciamento);
            long[] apoio = mapSelectedIds(checksApoio);
            long[] atividades = mapSelectedIds(checksAtividade);
            long[] reservas = mapSelectedIds(checksReserva);
            long[] opcaoIds = mergeArrays(financiamentos, apoio, atividades, reservas);

            Map<Long, String> extras = mapCamposComplementares(camposComplementares);
            cursoAlunoService().cadastrar(cursoAluno, opcaoIds, extras);
            putFlash("flashCursoAlunoMessage", "Registro 42 salvo com sucesso.");
            goShell("curso-aluno-list");
        } catch (Exception e) {
            erroFormVisivel = true;
            erroForm = e.getMessage() != null ? e.getMessage() : "Falha ao salvar Registro 42.";
        }
    }

    private long[] mergeArrays(long[] a, long[] b, long[] c, long[] d) {
        int size = a.length + b.length + c.length + d.length;
        long[] merged = new long[size];
        int idx = 0;
        for (int i = 0; i < a.length; i++) { merged[idx++] = a[i]; }
        for (int i = 0; i < b.length; i++) { merged[idx++] = b[i]; }
        for (int i = 0; i < c.length; i++) { merged[idx++] = c[i]; }
        for (int i = 0; i < d.length; i++) { merged[idx++] = d[i]; }
        return merged;
    }

    // =====================================================================
    // Getters / Setters
    // =====================================================================

    public String getErroList()             { return erroList; }
    public boolean isErroListVisivel()      { return erroListVisivel; }
    public String getFlashList()            { return flashList; }
    public boolean isFlashListVisivel()     { return flashListVisivel; }
    public Listbox getLstCursoAluno()       { return lstCursoAluno; }

    public String getErroForm()             { return erroForm; }
    public boolean isErroFormVisivel()      { return erroFormVisivel; }

    public List<Comboitem> getItensAluno()  { return itensAluno; }
    public String getAlunIdSelecionado()    { return alunIdSelecionado; }
    public void   setAlunIdSelecionado(String v){ this.alunIdSelecionado = v; }

    public List<Comboitem> getItensCurso()  { return itensCurso; }
    public String getCursoIdSelecionado()   { return cursoIdSelecionado; }
    public void   setCursoIdSelecionado(String v){ this.cursoIdSelecionado = v; }

    public String getIdAlunoIes()           { return idAlunoIes; }
    public void   setIdAlunoIes(String v)   { this.idAlunoIes = v; }
    public String getPeriodoReferencia()    { return periodoReferencia; }
    public void   setPeriodoReferencia(String v){ this.periodoReferencia = v; }
    public String getCodigoPolo()           { return codigoPolo; }
    public void   setCodigoPolo(String v)   { this.codigoPolo = v; }

    public List<Comboitem> getItensTurno()  { return itensTurno; }
    public String getTurnoSelecionado()     { return turnoSelecionado; }
    public void   setTurnoSelecionado(String v){ this.turnoSelecionado = v; }

    public List<Comboitem> getItensSituacao() { return itensSituacao; }
    public String getSituacaoSelecionada()  { return situacaoSelecionada; }
    public void   setSituacaoSelecionada(String v){ this.situacaoSelecionada = v; }

    public String getCursoOrigem()          { return cursoOrigem; }
    public void   setCursoOrigem(String v)  { this.cursoOrigem = v; }
    public String getSemestreConclusao()    { return semestreConclusao; }
    public void   setSemestreConclusao(String v){ this.semestreConclusao = v; }

    public List<Comboitem> getItensSimNao() { return itensSimNao; }

    public String getAlunoParforSelecionado()    { return alunoParforSelecionado; }
    public void   setAlunoParforSelecionado(String v){ this.alunoParforSelecionado = v; }
    public String getSegundaLicSelecionada()     { return segundaLicSelecionada; }
    public void   setSegundaLicSelecionada(String v){ this.segundaLicSelecionada = v; }

    public List<Comboitem> getItensTipoSegundaLic() { return itensTipoSegundaLic; }
    public String getTipoSegundaLicSelecionado()    { return tipoSegundaLicSelecionado; }
    public void   setTipoSegundaLicSelecionado(String v){ this.tipoSegundaLicSelecionado = v; }

    public String getSemestreIngresso()     { return semestreIngresso; }
    public void   setSemestreIngresso(String v){ this.semestreIngresso = v; }

    public String getFormaVestibularSelecionado() { return formaVestibularSelecionado; }
    public void   setFormaVestibularSelecionado(String v){ this.formaVestibularSelecionado = v; }
    public String getFormaEnemSelecionado() { return formaEnemSelecionado; }
    public void   setFormaEnemSelecionado(String v){ this.formaEnemSelecionado = v; }
    public String getFormaAvaliacaoSeriadaSelecionado() { return formaAvaliacaoSeriadaSelecionado; }
    public void   setFormaAvaliacaoSeriadaSelecionado(String v){ this.formaAvaliacaoSeriadaSelecionado = v; }
    public String getFormaSelecaoSimplificadaSelecionado() { return formaSelecaoSimplificadaSelecionado; }
    public void   setFormaSelecaoSimplificadaSelecionado(String v){ this.formaSelecaoSimplificadaSelecionado = v; }
    public String getFormaEgressoBiLiSelecionado() { return formaEgressoBiLiSelecionado; }
    public void   setFormaEgressoBiLiSelecionado(String v){ this.formaEgressoBiLiSelecionado = v; }
    public String getFormaPecGSelecionado() { return formaPecGSelecionado; }
    public void   setFormaPecGSelecionado(String v){ this.formaPecGSelecionado = v; }
    public String getFormaTransfExOffSelecionado() { return formaTransfExOffSelecionado; }
    public void   setFormaTransfExOffSelecionado(String v){ this.formaTransfExOffSelecionado = v; }
    public String getFormaDecisaoJudicialSelecionado() { return formaDecisaoJudicialSelecionado; }
    public void   setFormaDecisaoJudicialSelecionado(String v){ this.formaDecisaoJudicialSelecionado = v; }
    public String getFormaVagasRemanescentesSelecionado() { return formaVagasRemanescentesSelecionado; }
    public void   setFormaVagasRemanescentesSelecionado(String v){ this.formaVagasRemanescentesSelecionado = v; }
    public String getFormaProgramasEspeciaisSelecionado() { return formaProgramasEspeciaisSelecionado; }
    public void   setFormaProgramasEspeciaisSelecionado(String v){ this.formaProgramasEspeciaisSelecionado = v; }

    public Vbox getBoxFinanciamento()           { return boxFinanciamento; }
    public Vbox getBoxApoio()                   { return boxApoio; }
    public Vbox getBoxAtividade()               { return boxAtividade; }
    public Vbox getBoxReserva()                 { return boxReserva; }
    public Vbox getBoxCamposComplementares()    { return boxCamposComplementares; }
}
