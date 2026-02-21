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
import br.gov.inep.censo.web.zk.AbstractBaseViewModel;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.*;

import java.sql.Date;
import java.util.*;

/**
 * ViewModel MVVM do modulo Aluno (Registro 41).
 * Substitui AlunoComposer para os tres ZULs: list, form e view.
 */
public class AlunoViewModel extends AbstractBaseViewModel {

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

    // Listbox construido programaticamente no @Init (lista)
    private Listbox lstAlunos;

    // --- Estado do formulario ---
    private String erroForm;
    private boolean erroFormVisivel;
    private String tituloForm;
    private Long alunoIdEdicao;

    // Campos basicos do formulario (bind bidirecional)
    private String idAlunoInep;
    private String nomeAluno;
    private String cpfAluno;
    private java.util.Date dataNascimento;
    private String corRacaSelecionada;
    private String nacionalidadeSelecionada;
    private String ufNascimento;
    private String municipioNascimento;
    private String paisOrigem;

    // Combos do formulario
    private List<Comboitem> itensCorRaca;
    private List<Comboitem> itensNacionalidade;

    // Campos dinamicos 1..N — construidos programaticamente
    private Vbox boxDeficiencia;
    private Vbox boxCamposComplementares;
    private final Map<Long, Checkbox> checksDeficiencia = new LinkedHashMap<Long, Checkbox>();
    private final Map<Long, Textbox> camposComplementares = new LinkedHashMap<Long, Textbox>();

    // --- Estado da visualizacao ---
    private String viewId;
    private String viewIdInep;
    private String viewNome;
    private String viewCpf;
    private String viewNascimento;
    private String viewCorRaca;
    private String viewNacionalidade;
    private String viewUf;
    private String viewMunicipio;
    private String viewPais;
    private String viewDeficiencia;
    private Listbox lstViewCampos;
    private Long alunoIdVisualizacao;

    private AlunoService alunoService() {
        return (AlunoService) SpringUtil.getBean("alunoService");
    }

    private CatalogoService catalogoService() {
        return (CatalogoService) SpringUtil.getBean("catalogoService");
    }

    // =====================================================================
    // @Init — chamado pelo BindComposer ao criar o ViewModel
    // =====================================================================

    @Init
    public void init() {
        String req = currentRequest() != null ? currentRequest().getServletPath() : "";
        if (req.contains("aluno-list")) {
            initList();
        } else if (req.contains("aluno-form")) {
            initForm();
        } else if (req.contains("aluno-view")) {
            initView();
        }
    }

    // --- Lista ---

    private void initList() {
        erroListVisivel = false;
        erroList = "";

        String flash = consumeFlash("flashAlunoMessage");
        flashListVisivel = flash != null;
        flashList = flash != null ? flash : "";

        paginaAtual = parseIntOrDefault(
                currentRequest() != null ? currentRequest().getParameter("pagina") : null, 1);
        if (paginaAtual <= 0) {
            paginaAtual = 1;
        }

        lstAlunos = new Listbox();
        lstAlunos.setWidth("100%");
        lstAlunos.setMold("paging");
        lstAlunos.setPageSize(TAMANHO_PAGINA);

        Listhead head = new Listhead();
        head.setSizable(true);
        head.appendChild(new Listheader("ID", null, "70px"));
        head.appendChild(new Listheader("ID INEP", null, "90px"));
        head.appendChild(new Listheader("Nome", null, "260px"));
        head.appendChild(new Listheader("CPF", null, "110px"));
        head.appendChild(new Listheader("Nascimento", null, "120px"));
        head.appendChild(new Listheader("Nacionalidade", null, "120px"));
        head.appendChild(new Listheader("Acoes", null, "300px"));
        lstAlunos.appendChild(head);

        carregarLista();
    }

    private void carregarLista() {
        try {
            int total = alunoService().contar();
            totalPaginas = total == 0 ? 1 : ((total + TAMANHO_PAGINA - 1) / TAMANHO_PAGINA);
            if (paginaAtual > totalPaginas) {
                paginaAtual = totalPaginas;
            }

            lstAlunos.getItems().clear();
            List<Aluno> alunos = alunoService().listarPaginado(paginaAtual, TAMANHO_PAGINA);
            for (int i = 0; i < alunos.size(); i++) {
                adicionarLinhaAluno(alunos.get(i));
            }

            totalLabel = String.valueOf(total);
            paginacaoLabel = "Pagina " + paginaAtual + " de " + totalPaginas;
            anteriorDisabled = paginaAtual <= 1;
            proximaDisabled = paginaAtual >= totalPaginas;
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao carregar listagem de aluno.";
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
        btnAlterar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        openSub("aluno-list", "aluno-form", aluno.getId());
                    }
                });
        acoes.appendChild(btnAlterar);

        Toolbarbutton btnMostrar = new Toolbarbutton("Mostrar");
        btnMostrar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        openSub("aluno-list", "aluno-view", aluno.getId());
                    }
                });
        acoes.appendChild(btnMostrar);

        Toolbarbutton btnExcluir = new Toolbarbutton("Excluir");
        btnExcluir.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        excluirAluno(aluno.getId());
                    }
                });
        acoes.appendChild(btnExcluir);

        Toolbarbutton btnExportar = new Toolbarbutton("Exportar TXT");
        btnExportar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CLICK,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(org.zkoss.zk.ui.event.Event event) {
                        redirect("/api/relatorios/alunos/" + aluno.getId() + ".txt");
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
            alunoService().excluir(id);
            flashListVisivel = true;
            flashList = "Aluno excluido com sucesso.";
            carregarLista();
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao excluir aluno.";
        }
    }

    // --- Formulario ---

    private void initForm() {
        alunoIdEdicao = parseLongOrNull(currentRequest() != null ? currentRequest().getParameter("id") : null);
        erroFormVisivel = false;
        erroForm = "";

        try {
            Aluno aluno = alunoIdEdicao != null ? alunoService().buscarPorId(alunoIdEdicao) : new Aluno();
            if (aluno == null) {
                aluno = new Aluno();
                alunoIdEdicao = null;
            }

            tituloForm = alunoIdEdicao == null ? "Novo Aluno" : "Alterar Aluno";

            // Campos basicos
            idAlunoInep = safe(aluno.getIdAlunoInep());
            nomeAluno = safe(aluno.getNome());
            cpfAluno = safe(aluno.getCpf());
            dataNascimento = aluno.getDataNascimento() != null
                    ? new java.util.Date(aluno.getDataNascimento().getTime()) : null;
            ufNascimento = safe(aluno.getUfNascimento());
            municipioNascimento = safe(aluno.getMunicipioNascimento());
            paisOrigem = aluno.getPaisOrigem() != null && aluno.getPaisOrigem().trim().length() > 0
                    ? aluno.getPaisOrigem() : "BRA";

            // Combos
            itensCorRaca = montarItensCorRaca();
            corRacaSelecionada = aluno.getCorRaca() == null ? null : String.valueOf(aluno.getCorRaca());

            itensNacionalidade = montarItensNacionalidade();
            nacionalidadeSelecionada = aluno.getNacionalidade() == null ? "1" : String.valueOf(aluno.getNacionalidade());

            // Campos dinamicos
            boxDeficiencia = new Vbox();
            boxDeficiencia.setSpacing("6px");
            popularDeficiencias(alunoIdEdicao);

            boxCamposComplementares = new Vbox();
            boxCamposComplementares.setSpacing("6px");
            popularCamposComplementares(alunoIdEdicao);
        } catch (Exception e) {
            erroFormVisivel = true;
            erroForm = "Falha ao carregar formulario de aluno.";
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

    private void popularDeficiencias(Long alunoId) throws Exception {
        checksDeficiencia.clear();
        Set<Long> selecionados = new HashSet<Long>();
        if (alunoId != null) {
            selecionados.addAll(alunoService().listarOpcaoDeficienciaIds(alunoId));
        }
        List<OpcaoDominio> opcoes = catalogoService().listarOpcoesPorCategoria(CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA);
        for (int i = 0; i < opcoes.size(); i++) {
            OpcaoDominio opcao = opcoes.get(i);
            Checkbox check = new Checkbox(opcao.getNome());
            check.setChecked(selecionados.contains(opcao.getId()));
            checksDeficiencia.put(opcao.getId(), check);
            boxDeficiencia.appendChild(check);
        }
    }

    private void popularCamposComplementares(Long alunoId) throws Exception {
        camposComplementares.clear();
        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (alunoId != null) {
            valores.putAll(alunoService().carregarCamposComplementaresPorCampoId(alunoId));
        }
        List<LayoutCampo> campos = filtrarCamposComplementaresAluno(
                catalogoService().listarCamposModulo(ModulosLayout.ALUNO_41));
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

    private List<LayoutCampo> filtrarCamposComplementaresAluno(List<LayoutCampo> campos) {
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

    // --- Visualizacao ---

    private void initView() {
        alunoIdVisualizacao = parseLongOrNull(currentRequest() != null ? currentRequest().getParameter("id") : null);
        if (alunoIdVisualizacao == null) {
            goShell("aluno-list");
            return;
        }
        try {
            Aluno aluno = alunoService().buscarPorId(alunoIdVisualizacao);
            if (aluno == null) {
                goShell("aluno-list");
                return;
            }
            viewId = safe(aluno.getId());
            viewIdInep = safe(aluno.getIdAlunoInep());
            viewNome = safe(aluno.getNome());
            viewCpf = safe(aluno.getCpf());
            viewNascimento = safe(aluno.getDataNascimento());
            viewCorRaca = aluno.getCorRacaEnum() != null
                    ? aluno.getCorRacaEnum().getDescricao() + " (" + aluno.getCorRaca() + ")"
                    : safe(aluno.getCorRaca());
            viewNacionalidade = aluno.getNacionalidadeEnum() != null
                    ? aluno.getNacionalidadeEnum().getDescricao() + " (" + aluno.getNacionalidade() + ")"
                    : safe(aluno.getNacionalidade());
            viewUf = safe(aluno.getUfNascimento());
            viewMunicipio = safe(aluno.getMunicipioNascimento());
            viewPais = safe(aluno.getPaisOrigem());
            viewDeficiencia = safe(aluno.getTiposDeficienciaResumo());

            lstViewCampos = new Listbox();
            lstViewCampos.setWidth("100%");
            Listhead head = new Listhead();
            head.appendChild(new Listheader("Campo", null, "55%"));
            head.appendChild(new Listheader("Valor", null, "45%"));
            lstViewCampos.appendChild(head);
            preencherCamposView(alunoIdVisualizacao);
        } catch (Exception e) {
            goShell("aluno-list");
        }
    }

    private void preencherCamposView(Long alunoId) throws Exception {
        Map<Long, String> valores = alunoService().carregarCamposComplementaresPorCampoId(alunoId);
        Map<Long, String> rotulos = montarRotulosCampos(ModulosLayout.ALUNO_41);
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
        openSub("aluno-list", "aluno-form");
    }

    @Command
    public void exportarTodos() {
        redirect("/api/relatorios/alunos.txt");
    }

    @Command
    @NotifyChange({"lstAlunos","erroList","erroListVisivel","flashList","flashListVisivel",
                   "totalLabel","paginacaoLabel","anteriorDisabled","proximaDisabled"})
    public void importar(@org.zkoss.bind.annotation.BindingParam("conteudo") String conteudo) {
        try {
            int total = alunoService().importarTxtPipe(conteudo);
            putFlash("flashHomeMessage", "Importacao de aluno concluida: " + total + " registro(s).");
            putFlash("flashAlunoMessage", "Importacao concluida: " + total + " registro(s).");
            goShell("aluno-list");
        } catch (Exception e) {
            erroListVisivel = true;
            erroList = "Falha ao importar TXT de aluno.";
        }
    }

    @Command
    public void paginaAnterior() {
        if (paginaAtual > 1) {
            goShell("aluno-list", paginaAtual - 1);
        }
    }

    @Command
    public void proximaPagina() {
        if (paginaAtual < totalPaginas) {
            goShell("aluno-list", paginaAtual + 1);
        }
    }

    @Command
    public void voltar() {
        goShell("aluno-list");
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
            String pais = trimToEmpty(paisOrigem);
            if (pais.length() == 0) {
                pais = "BRA";
            }
            Aluno aluno = Aluno.builder()
                    .id(alunoIdEdicao)
                    .idAlunoInep(parseLongOrNull(idAlunoInep))
                    .nome(trimToEmpty(nomeAluno))
                    .cpf(trimToEmpty(cpfAluno))
                    .dataNascimento(dataNasc)
                    .corRaca(parseIntegerOrNull(corRacaSelecionada))
                    .nacionalidade(nac)
                    .ufNascimento(trimToEmpty(ufNascimento))
                    .municipioNascimento(trimToEmpty(municipioNascimento))
                    .paisOrigem(pais)
                    .build();

            long[] opcaoDeficienciaIds = mapSelectedIds(checksDeficiencia);
            Map<Long, String> extras = mapCamposComplementares(camposComplementares);

            if (alunoIdEdicao == null) {
                alunoService().cadastrar(aluno, opcaoDeficienciaIds, extras);
                putFlash("flashHomeMessage", "Cadastro de aluno realizado com sucesso.");
                putFlash("flashAlunoMessage", "Aluno incluido com sucesso.");
            } else {
                alunoService().atualizar(aluno, opcaoDeficienciaIds, extras);
                putFlash("flashAlunoMessage", "Aluno alterado com sucesso.");
            }
            goShell("aluno-list");
        } catch (Exception e) {
            erroFormVisivel = true;
            erroForm = e.getMessage() != null ? e.getMessage() : "Falha ao salvar aluno.";
        }
    }

    @Command
    public void voltarView() {
        goShell("aluno-list");
    }

    @Command
    public void editarView() {
        openSub("aluno-list", "aluno-form", alunoIdVisualizacao);
    }

    // =====================================================================
    // Getters
    // =====================================================================

    public String getErroList()           { return erroList; }
    public boolean isErroListVisivel()    { return erroListVisivel; }
    public String getFlashList()          { return flashList; }
    public boolean isFlashListVisivel()   { return flashListVisivel; }
    public String getTotalLabel()         { return totalLabel; }
    public String getPaginacaoLabel()     { return paginacaoLabel; }
    public boolean isAnteriorDisabled()   { return anteriorDisabled; }
    public boolean isProximaDisabled()    { return proximaDisabled; }
    public Listbox getLstAlunos()         { return lstAlunos; }

    public String getErroForm()           { return erroForm; }
    public boolean isErroFormVisivel()    { return erroFormVisivel; }
    public String getTituloForm()         { return tituloForm; }
    public String getIdAlunoInep()        { return idAlunoInep; }
    public void   setIdAlunoInep(String v){ this.idAlunoInep = v; }
    public String getNomeAluno()          { return nomeAluno; }
    public void   setNomeAluno(String v)  { this.nomeAluno = v; }
    public String getCpfAluno()           { return cpfAluno; }
    public void   setCpfAluno(String v)   { this.cpfAluno = v; }
    public java.util.Date getDataNascimento()          { return dataNascimento; }
    public void   setDataNascimento(java.util.Date v)  { this.dataNascimento = v; }
    public String getCorRacaSelecionada()              { return corRacaSelecionada; }
    public void   setCorRacaSelecionada(String v)      { this.corRacaSelecionada = v; }
    public String getNacionalidadeSelecionada()        { return nacionalidadeSelecionada; }
    public void   setNacionalidadeSelecionada(String v){ this.nacionalidadeSelecionada = v; }
    public String getUfNascimento()       { return ufNascimento; }
    public void   setUfNascimento(String v){ this.ufNascimento = v; }
    public String getMunicipioNascimento()           { return municipioNascimento; }
    public void   setMunicipioNascimento(String v)   { this.municipioNascimento = v; }
    public String getPaisOrigem()         { return paisOrigem; }
    public void   setPaisOrigem(String v) { this.paisOrigem = v; }
    public Vbox   getBoxDeficiencia()     { return boxDeficiencia; }
    public Vbox   getBoxCamposComplementares() { return boxCamposComplementares; }

    public String getViewId()             { return viewId; }
    public String getViewIdInep()         { return viewIdInep; }
    public String getViewNome()           { return viewNome; }
    public String getViewCpf()            { return viewCpf; }
    public String getViewNascimento()     { return viewNascimento; }
    public String getViewCorRaca()        { return viewCorRaca; }
    public String getViewNacionalidade()  { return viewNacionalidade; }
    public String getViewUf()             { return viewUf; }
    public String getViewMunicipio()      { return viewMunicipio; }
    public String getViewPais()           { return viewPais; }
    public String getViewDeficiencia()    { return viewDeficiencia; }
    public Listbox getLstViewCampos()     { return lstViewCampos; }
}
