package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.AlunoDAO;
import br.gov.inep.censo.dao.LayoutCampoDAO;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Aluno;
import br.gov.inep.censo.repository.AlunoRepository;
import br.gov.inep.censo.util.ValidationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servico de negocio do modulo Aluno.
 */
public class AlunoService {

    private final AlunoDAO alunoDAO;
    private final LayoutCampoDAO layoutCampoDAO;
    private final AlunoRepository alunoRepository;

    public AlunoService() {
        this(new AlunoDAO(), new LayoutCampoDAO(), resolveRepository());
    }

    public AlunoService(AlunoDAO alunoDAO, LayoutCampoDAO layoutCampoDAO) {
        this(alunoDAO, layoutCampoDAO, null);
    }

    public AlunoService(AlunoDAO alunoDAO, LayoutCampoDAO layoutCampoDAO, AlunoRepository alunoRepository) {
        this.alunoDAO = alunoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
        this.alunoRepository = alunoRepository;
    }

    public Long cadastrar(Aluno aluno, long[] opcaoIds, Map<Long, String> camposComplementares) throws SQLException {
        validar(aluno);
        return alunoDAO.salvar(aluno, opcaoIds, camposComplementares);
    }

    public void atualizar(Aluno aluno, long[] opcaoIds, Map<Long, String> camposComplementares) throws SQLException {
        validar(aluno);
        if (aluno.getId() == null) {
            throw new IllegalArgumentException("ID do aluno e obrigatorio para alteracao.");
        }
        alunoDAO.atualizar(aluno, opcaoIds, camposComplementares);
    }

    public Aluno buscarPorId(Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        if (alunoRepository != null) {
            try {
                return alunoRepository.findOne(id);
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao buscar aluno via repository.", e);
            }
        }
        return alunoDAO.buscarPorId(id);
    }

    public List<Aluno> listar() throws SQLException {
        if (alunoRepository != null) {
            try {
                return alunoRepository.findAll(new Sort(Sort.Direction.ASC, "nome"));
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar alunos via repository.", e);
            }
        }
        return alunoDAO.listar();
    }

    public List<Aluno> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        if (alunoRepository != null) {
            int page = pagina <= 0 ? 0 : pagina - 1;
            int size = tamanhoPagina <= 0 ? 10 : tamanhoPagina;
            try {
                return alunoRepository.findAll(
                        new PageRequest(page, size, new Sort(Sort.Direction.ASC, "nome"))).getContent();
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar alunos paginados via repository.", e);
            }
        }
        return alunoDAO.listarPaginado(pagina, tamanhoPagina);
    }

    public int contar() throws SQLException {
        if (alunoRepository != null) {
            try {
                long total = alunoRepository.count();
                return total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao contar alunos via repository.", e);
            }
        }
        return alunoDAO.contar();
    }

    public void excluir(Long id) throws SQLException {
        alunoDAO.excluir(id);
    }

    public List<Long> listarOpcaoDeficienciaIds(Long alunoId) throws SQLException {
        return alunoDAO.listarOpcaoDeficienciaIds(alunoId);
    }

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long alunoId) throws SQLException {
        return alunoDAO.carregarCamposComplementaresPorCampoId(alunoId);
    }

    public String exportarTodosTxtPipe() throws SQLException {
        List<Aluno> alunos = listar();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < alunos.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(exportarLinhaTxtPipe(alunos.get(i)));
        }
        return sb.toString();
    }

    public String exportarPorIdTxtPipe(Long alunoId) throws SQLException {
        Aluno aluno = buscarPorId(alunoId);
        if (aluno == null) {
            return "";
        }
        return exportarLinhaTxtPipe(aluno);
    }

    private SQLException toSqlException(String mensagem, RuntimeException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }
        return new SQLException(mensagem, e);
    }

    private static AlunoRepository resolveRepository() {
        try {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            if (context == null) {
                return null;
            }
            return context.getBean(AlunoRepository.class);
        } catch (Exception e) {
            return null;
        }
    }

    public int importarTxtPipe(String conteudo) throws SQLException {
        if (conteudo == null || conteudo.trim().length() == 0) {
            return 0;
        }
        Map<Integer, Long> campoIdPorNumero = layoutCampoDAO.mapaCampoIdPorNumero(ModulosLayout.ALUNO_41);
        String[] linhas = conteudo.split("\\r?\\n");
        int importados = 0;
        for (int i = 0; i < linhas.length; i++) {
            String linha = linhas[i];
            if (linha == null || linha.trim().length() == 0) {
                continue;
            }
            String[] campos = linha.split("\\|", -1);
            if (campos.length == 0) {
                continue;
            }
            String tipoRegistro = safeField(campos, 1);
            if (tipoRegistro.length() > 0 && !"41".equals(tipoRegistro)) {
                continue;
            }

            Aluno aluno = new Aluno();
            aluno.setIdAlunoInep(parseLongOrNull(safeField(campos, 2)));
            aluno.setNome(safeField(campos, 3));
            aluno.setCpf(safeField(campos, 4));
            aluno.setDataNascimento(parseDateOrNull(safeField(campos, 6)));
            aluno.setCorRaca(parseIntegerOrNull(safeField(campos, 7)));
            Integer nacionalidade = parseIntegerOrNull(safeField(campos, 8));
            aluno.setNacionalidade(nacionalidade != null ? nacionalidade : Integer.valueOf(1));
            aluno.setUfNascimento(safeField(campos, 9));
            aluno.setMunicipioNascimento(safeField(campos, 10));
            String pais = safeField(campos, 11);
            aluno.setPaisOrigem(pais.length() == 0 ? "BRA" : pais);

            long[] opcoesDeficiencia = mapearOpcoesDeficiencia(campos);
            Map<Long, String> complementares = montarComplementaresImportacao(campos, campoIdPorNumero);
            cadastrar(aluno, opcoesDeficiencia, complementares);
            importados++;
        }
        return importados;
    }

    private String exportarLinhaTxtPipe(Aluno aluno) throws SQLException {
        Map<Integer, String> valores = alunoDAO.carregarCamposRegistro41PorNumero(aluno.getId());
        Set<String> codigosDeficiencia = new HashSet<String>(alunoDAO.listarOpcaoDeficienciaCodigos(aluno.getId()));

        int max = 23;
        String[] campos = new String[max];
        for (int i = 0; i < max; i++) {
            campos[i] = "";
        }

        campos[0] = "41";
        campos[1] = aluno.getIdAlunoInep() != null ? String.valueOf(aluno.getIdAlunoInep()) : "";
        campos[2] = safeValue(aluno.getNome());
        campos[3] = safeValue(aluno.getCpf());
        campos[5] = formatDateYYYYMMDD(aluno.getDataNascimento());
        campos[6] = aluno.getCorRaca() != null ? String.valueOf(aluno.getCorRaca()) : "";
        campos[7] = aluno.getNacionalidade() != null ? String.valueOf(aluno.getNacionalidade()) : "";
        campos[8] = safeValue(aluno.getUfNascimento());
        campos[9] = safeValue(aluno.getMunicipioNascimento());
        campos[10] = safeValue(aluno.getPaisOrigem());

        if (codigosDeficiencia.contains("CEGUEIRA")) campos[12] = "1";
        if (codigosDeficiencia.contains("BAIXA_VISAO")) campos[13] = "1";
        if (codigosDeficiencia.contains("VISAO_MONOCULAR")) campos[14] = "1";
        if (codigosDeficiencia.contains("SURDEZ")) campos[15] = "1";
        if (codigosDeficiencia.contains("DEF_AUDITIVA")) campos[16] = "1";
        if (codigosDeficiencia.contains("DEF_FISICA")) campos[17] = "1";
        if (codigosDeficiencia.contains("SURDOCEGUEIRA")) campos[18] = "1";
        if (codigosDeficiencia.contains("DEF_INTELECTUAL")) campos[19] = "1";
        if (codigosDeficiencia.contains("TEA")) campos[20] = "1";
        if (codigosDeficiencia.contains("ALTAS_HABILIDADES")) campos[21] = "1";

        if (valores != null) {
            for (Map.Entry<Integer, String> entry : valores.entrySet()) {
                int numero = entry.getKey().intValue();
                if (numero >= 1 && numero <= max && campos[numero - 1].length() == 0) {
                    campos[numero - 1] = safeValue(entry.getValue());
                }
            }
        }

        StringBuilder linha = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                linha.append('|');
            }
            linha.append(campos[i] != null ? campos[i] : "");
        }
        return linha.toString();
    }

    private Map<Long, String> montarComplementaresImportacao(String[] campos, Map<Integer, Long> campoIdPorNumero) {
        java.util.LinkedHashMap<Long, String> map = new java.util.LinkedHashMap<Long, String>();
        for (int numero = 1; numero <= campos.length; numero++) {
            if (numero == 1 || numero == 2 || numero == 3 || numero == 4 || numero == 6 || numero == 7 ||
                    numero == 8 || numero == 9 || numero == 10 || numero == 11 ||
                    (numero >= 13 && numero <= 22)) {
                continue;
            }
            String valor = safeField(campos, numero);
            if (valor.length() == 0) {
                continue;
            }
            Long campoId = campoIdPorNumero.get(Integer.valueOf(numero));
            if (campoId != null) {
                map.put(campoId, valor);
            }
        }
        return map;
    }

    private long[] mapearOpcoesDeficiencia(String[] campos) throws SQLException {
        List<Long> disponiveis = layoutOpcoesDeficiencia();
        java.util.ArrayList<Long> selecionadas = new java.util.ArrayList<Long>();
        int[] numeros = new int[]{13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
        for (int i = 0; i < numeros.length && i < disponiveis.size(); i++) {
            String valor = safeField(campos, numeros[i]);
            if ("1".equals(valor)) {
                selecionadas.add(disponiveis.get(i));
            }
        }
        long[] ids = new long[selecionadas.size()];
        for (int i = 0; i < selecionadas.size(); i++) {
            ids[i] = selecionadas.get(i).longValue();
        }
        return ids;
    }

    private List<Long> layoutOpcoesDeficiencia() throws SQLException {
        List<Long> ids = new ArrayList<Long>();
        List<br.gov.inep.censo.model.OpcaoDominio> itens = new CatalogoService().listarOpcoesPorCategoria(
                br.gov.inep.censo.domain.CategoriasOpcao.ALUNO_TIPO_DEFICIENCIA);
        for (int i = 0; i < itens.size(); i++) {
            String codigo = itens.get(i).getCodigo();
            if ("CEGUEIRA".equals(codigo)) ids.add(itens.get(i).getId());
        }
        for (int i = 0; i < itens.size(); i++) if ("BAIXA_VISAO".equals(itens.get(i).getCodigo())) ids.add(itens.get(i).getId());
        for (int i = 0; i < itens.size(); i++) if ("VISAO_MONOCULAR".equals(itens.get(i).getCodigo())) ids.add(itens.get(i).getId());
        for (int i = 0; i < itens.size(); i++) if ("SURDEZ".equals(itens.get(i).getCodigo())) ids.add(itens.get(i).getId());
        for (int i = 0; i < itens.size(); i++) if ("DEF_AUDITIVA".equals(itens.get(i).getCodigo())) ids.add(itens.get(i).getId());
        for (int i = 0; i < itens.size(); i++) if ("DEF_FISICA".equals(itens.get(i).getCodigo())) ids.add(itens.get(i).getId());
        for (int i = 0; i < itens.size(); i++) if ("SURDOCEGUEIRA".equals(itens.get(i).getCodigo())) ids.add(itens.get(i).getId());
        for (int i = 0; i < itens.size(); i++) if ("DEF_INTELECTUAL".equals(itens.get(i).getCodigo())) ids.add(itens.get(i).getId());
        for (int i = 0; i < itens.size(); i++) if ("TEA".equals(itens.get(i).getCodigo())) ids.add(itens.get(i).getId());
        for (int i = 0; i < itens.size(); i++) if ("ALTAS_HABILIDADES".equals(itens.get(i).getCodigo())) ids.add(itens.get(i).getId());
        return ids;
    }

    private void validar(Aluno aluno) {
        if (aluno == null) {
            throw new IllegalArgumentException("Aluno nao informado.");
        }
        if (aluno.getNome() == null || aluno.getNome().trim().length() == 0) {
            throw new IllegalArgumentException("Campo Nome e obrigatorio.");
        }
        if (!ValidationUtils.isCpfFormatoValido(aluno.getCpf())) {
            throw new IllegalArgumentException("CPF deve conter 11 digitos numericos.");
        }
        if (aluno.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de Nascimento e obrigatoria.");
        }
        if (aluno.getNacionalidade() == null) {
            throw new IllegalArgumentException("Nacionalidade e obrigatoria.");
        }
        if (aluno.getPaisOrigem() == null || aluno.getPaisOrigem().trim().length() == 0) {
            aluno.setPaisOrigem("BRA");
        }
    }

    private String safeField(String[] campos, int numeroCampo) {
        int idx = numeroCampo - 1;
        if (idx < 0 || idx >= campos.length) {
            return "";
        }
        String value = campos[idx];
        return value == null ? "" : value.trim();
    }

    private String safeValue(String value) {
        return value == null ? "" : value.trim();
    }

    private Long parseLongOrNull(String value) {
        if (!ValidationUtils.isNumeric(value)) {
            return null;
        }
        return Long.valueOf(Long.parseLong(value));
    }

    private Integer parseIntegerOrNull(String value) {
        if (!ValidationUtils.isNumeric(value)) {
            return null;
        }
        return Integer.valueOf(Integer.parseInt(value));
    }

    private Date parseDateOrNull(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        String cleaned = value.trim();
        try {
            if (cleaned.length() == 8) {
                java.util.Date parsed;
                if (ValidationUtils.isNumeric(cleaned.substring(0, 4))) {
                    parsed = new SimpleDateFormat("yyyyMMdd").parse(cleaned);
                } else {
                    parsed = new SimpleDateFormat("ddMMyyyy").parse(cleaned);
                }
                return new Date(parsed.getTime());
            }
            if (cleaned.length() == 10 && cleaned.charAt(4) == '-') {
                return Date.valueOf(cleaned);
            }
            return null;
        } catch (ParseException e) {
            return null;
        }
    }

    private String formatDateYYYYMMDD(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyyMMdd").format(new java.util.Date(date.getTime()));
    }
}
