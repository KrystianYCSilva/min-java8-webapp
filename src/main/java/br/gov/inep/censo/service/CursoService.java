package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.CursoDAO;
import br.gov.inep.censo.dao.LayoutCampoDAO;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Curso;
import br.gov.inep.censo.repository.CursoRepository;
import br.gov.inep.censo.util.ValidationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servico de negocio do modulo Curso.
 */
public class CursoService {

    private final CursoDAO cursoDAO;
    private final LayoutCampoDAO layoutCampoDAO;
    private final CursoRepository cursoRepository;

    public CursoService() {
        this(new CursoDAO(), new LayoutCampoDAO(), resolveRepository());
    }

    public CursoService(CursoDAO cursoDAO, LayoutCampoDAO layoutCampoDAO) {
        this(cursoDAO, layoutCampoDAO, null);
    }

    public CursoService(CursoDAO cursoDAO, LayoutCampoDAO layoutCampoDAO, CursoRepository cursoRepository) {
        this.cursoDAO = cursoDAO;
        this.layoutCampoDAO = layoutCampoDAO;
        this.cursoRepository = cursoRepository;
    }

    public Long cadastrar(Curso curso, long[] opcaoIds, Map<Long, String> camposComplementares) throws SQLException {
        validar(curso);
        return cursoDAO.salvar(curso, opcaoIds, camposComplementares);
    }

    public void atualizar(Curso curso, long[] opcaoIds, Map<Long, String> camposComplementares) throws SQLException {
        validar(curso);
        if (curso.getId() == null) {
            throw new IllegalArgumentException("ID do curso e obrigatorio para alteracao.");
        }
        cursoDAO.atualizar(curso, opcaoIds, camposComplementares);
    }

    public Curso buscarPorId(Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        if (cursoRepository != null) {
            try {
                return cursoRepository.findOne(id);
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao buscar curso via repository.", e);
            }
        }
        return cursoDAO.buscarPorId(id);
    }

    public List<Curso> listar() throws SQLException {
        if (cursoRepository != null) {
            try {
                return cursoRepository.findAll(new Sort(Sort.Direction.ASC, "nome"));
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar cursos via repository.", e);
            }
        }
        return cursoDAO.listar();
    }

    public List<Curso> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        if (cursoRepository != null) {
            int page = pagina <= 0 ? 0 : pagina - 1;
            int size = tamanhoPagina <= 0 ? 10 : tamanhoPagina;
            try {
                return cursoRepository.findAll(
                        new PageRequest(page, size, new Sort(Sort.Direction.ASC, "nome"))).getContent();
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar cursos paginados via repository.", e);
            }
        }
        return cursoDAO.listarPaginado(pagina, tamanhoPagina);
    }

    public int contar() throws SQLException {
        if (cursoRepository != null) {
            try {
                long total = cursoRepository.count();
                return total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao contar cursos via repository.", e);
            }
        }
        return cursoDAO.contar();
    }

    public void excluir(Long id) throws SQLException {
        cursoDAO.excluir(id);
    }

    public List<Long> listarOpcaoRecursoAssistivoIds(Long cursoId) throws SQLException {
        return cursoDAO.listarOpcaoRecursoAssistivoIds(cursoId);
    }

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long cursoId) throws SQLException {
        return cursoDAO.carregarCamposComplementaresPorCampoId(cursoId);
    }

    public String exportarTodosTxtPipe() throws SQLException {
        List<Curso> cursos = listar();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cursos.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(exportarLinhaTxtPipe(cursos.get(i)));
        }
        return sb.toString();
    }

    public String exportarPorIdTxtPipe(Long cursoId) throws SQLException {
        Curso curso = buscarPorId(cursoId);
        if (curso == null) {
            return "";
        }
        return exportarLinhaTxtPipe(curso);
    }

    private SQLException toSqlException(String mensagem, RuntimeException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }
        return new SQLException(mensagem, e);
    }

    private static CursoRepository resolveRepository() {
        try {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            if (context == null) {
                return null;
            }
            return context.getBean(CursoRepository.class);
        } catch (Exception e) {
            return null;
        }
    }

    public int importarTxtPipe(String conteudo) throws SQLException {
        if (conteudo == null || conteudo.trim().length() == 0) {
            return 0;
        }
        Map<Integer, Long> campoIdPorNumero = layoutCampoDAO.mapaCampoIdPorNumero(ModulosLayout.CURSO_21);
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
            if (tipoRegistro.length() > 0 && !"21".equals(tipoRegistro)) {
                continue;
            }

            Curso curso = new Curso();
            curso.setCodigoCursoEmec(safeField(campos, 2));
            curso.setCursoTeveAlunoVinculado(parseIntegerOrDefault(safeField(campos, 3), 1));
            curso.setNome("Curso " + curso.getCodigoCursoEmec());
            curso.setNivelAcademico("GRADUACAO");
            curso.setFormatoOferta("PRESENCIAL");

            long[] recursosAssistivos = mapearOpcoesRecursosAssistivos(campos);
            Map<Long, String> complementares = montarComplementaresImportacao(campos, campoIdPorNumero);
            cadastrar(curso, recursosAssistivos, complementares);
            importados++;
        }
        return importados;
    }

    private String exportarLinhaTxtPipe(Curso curso) throws SQLException {
        Map<Integer, String> valores = cursoDAO.carregarCamposRegistro21PorNumero(curso.getId());
        Set<String> codigos = new HashSet<String>(cursoDAO.listarOpcaoRecursoAssistivoCodigos(curso.getId()));

        int max = 67;
        String[] campos = new String[max];
        for (int i = 0; i < max; i++) {
            campos[i] = "";
        }

        campos[0] = "21";
        campos[1] = safeValue(curso.getCodigoCursoEmec());
        campos[2] = curso.getCursoTeveAlunoVinculado() != null ? String.valueOf(curso.getCursoTeveAlunoVinculado()) : "";

        if (codigos.contains("BRAILLE")) campos[53] = "1";
        if (codigos.contains("AUDIO")) campos[54] = "1";
        if (codigos.contains("INFORMATICA_ACESSIVEL")) campos[55] = "1";
        if (codigos.contains("CARACTERE_AMPLIADO")) campos[56] = "1";
        if (codigos.contains("MATERIAL_TATIL")) campos[57] = "1";
        if (codigos.contains("ACESSIBILIDADE_COMUNICACAO")) campos[58] = "1";
        if (codigos.contains("TRADUTOR_LIBRAS")) campos[59] = "1";
        if (codigos.contains("GUIA_INTERPRETE")) campos[60] = "1";
        if (codigos.contains("MATERIAL_LIBRAS")) campos[61] = "1";
        if (codigos.contains("DISCIPLINA_LIBRAS")) campos[62] = "1";
        if (codigos.contains("IMPRESSO_ACESSIVEL")) campos[63] = "1";
        if (codigos.contains("DIGITAL_ACESSIVEL")) campos[64] = "1";

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
            if (numero == 1 || numero == 2 || numero == 3 || (numero >= 54 && numero <= 65)) {
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

    private long[] mapearOpcoesRecursosAssistivos(String[] campos) throws SQLException {
        List<Long> disponiveis = layoutOpcoesRecursosAssistivos();
        java.util.ArrayList<Long> selecionadas = new java.util.ArrayList<Long>();
        int[] numeros = new int[]{54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65};
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

    private List<Long> layoutOpcoesRecursosAssistivos() throws SQLException {
        List<Long> ids = new ArrayList<Long>();
        List<br.gov.inep.censo.model.OpcaoDominio> itens = new CatalogoService().listarOpcoesPorCategoria(
                br.gov.inep.censo.domain.CategoriasOpcao.CURSO_RECURSO_TECNOLOGIA_ASSISTIVA);
        addByCode(itens, ids, "BRAILLE");
        addByCode(itens, ids, "AUDIO");
        addByCode(itens, ids, "INFORMATICA_ACESSIVEL");
        addByCode(itens, ids, "CARACTERE_AMPLIADO");
        addByCode(itens, ids, "MATERIAL_TATIL");
        addByCode(itens, ids, "ACESSIBILIDADE_COMUNICACAO");
        addByCode(itens, ids, "TRADUTOR_LIBRAS");
        addByCode(itens, ids, "GUIA_INTERPRETE");
        addByCode(itens, ids, "MATERIAL_LIBRAS");
        addByCode(itens, ids, "DISCIPLINA_LIBRAS");
        addByCode(itens, ids, "IMPRESSO_ACESSIVEL");
        addByCode(itens, ids, "DIGITAL_ACESSIVEL");
        return ids;
    }

    private void addByCode(List<br.gov.inep.censo.model.OpcaoDominio> itens, List<Long> ids, String code) {
        for (int i = 0; i < itens.size(); i++) {
            if (code.equals(itens.get(i).getCodigo())) {
                ids.add(itens.get(i).getId());
                return;
            }
        }
    }

    private void validar(Curso curso) {
        if (curso == null) {
            throw new IllegalArgumentException("Curso nao informado.");
        }
        if (curso.getCodigoCursoEmec() == null || curso.getCodigoCursoEmec().trim().length() == 0) {
            throw new IllegalArgumentException("Codigo do Curso no E-MEC e obrigatorio.");
        }
        if (curso.getNome() == null || curso.getNome().trim().length() == 0) {
            throw new IllegalArgumentException("Nome do curso e obrigatorio.");
        }
        if (curso.getNivelAcademico() == null || curso.getNivelAcademico().trim().length() == 0) {
            throw new IllegalArgumentException("Nivel Academico e obrigatorio.");
        }
        if (curso.getFormatoOferta() == null || curso.getFormatoOferta().trim().length() == 0) {
            throw new IllegalArgumentException("Formato de oferta e obrigatorio.");
        }
        if (curso.getCursoTeveAlunoVinculado() == null) {
            throw new IllegalArgumentException("Campo curso teve aluno vinculado e obrigatorio.");
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

    private Integer parseIntegerOrDefault(String value, int defaultValue) {
        if (!ValidationUtils.isNumeric(value)) {
            return Integer.valueOf(defaultValue);
        }
        return Integer.valueOf(Integer.parseInt(value));
    }
}
