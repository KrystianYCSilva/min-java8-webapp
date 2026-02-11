package br.gov.inep.censo.service;

import br.gov.inep.censo.dao.IesDAO;
import br.gov.inep.censo.dao.LayoutCampoDAO;
import br.gov.inep.censo.dao.MunicipioDAO;
import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Ies;
import br.gov.inep.censo.repository.IesRepository;
import br.gov.inep.censo.util.ValidationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Servico de negocio do modulo IES.
 */
public class IesService {

    private final IesDAO iesDAO;
    private final LayoutCampoDAO layoutCampoDAO;
    private final MunicipioDAO municipioDAO;
    private final IesRepository iesRepository;

    public IesService() {
        this(new IesDAO(), new LayoutCampoDAO(), new MunicipioDAO(), resolveRepository());
    }

    public IesService(IesDAO iesDAO, LayoutCampoDAO layoutCampoDAO, MunicipioDAO municipioDAO) {
        this(iesDAO, layoutCampoDAO, municipioDAO, null);
    }

    public IesService(IesDAO iesDAO,
                      LayoutCampoDAO layoutCampoDAO,
                      MunicipioDAO municipioDAO,
                      IesRepository iesRepository) {
        this.iesDAO = iesDAO;
        this.layoutCampoDAO = layoutCampoDAO;
        this.municipioDAO = municipioDAO;
        this.iesRepository = iesRepository;
    }

    public Long cadastrar(Ies ies, Map<Long, String> camposComplementares) throws SQLException {
        validar(ies);
        return iesDAO.salvar(ies, camposComplementares);
    }

    public void atualizar(Ies ies, Map<Long, String> camposComplementares) throws SQLException {
        validar(ies);
        if (ies.getId() == null) {
            throw new IllegalArgumentException("ID da IES e obrigatorio para alteracao.");
        }
        iesDAO.atualizar(ies, camposComplementares);
    }

    public Ies buscarPorId(Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        if (iesRepository != null) {
            try {
                return iesRepository.findOne(id);
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao buscar ies via repository.", e);
            }
        }
        return iesDAO.buscarPorId(id);
    }

    public List<Ies> listar() throws SQLException {
        if (iesRepository != null) {
            try {
                return iesRepository.findAll(new Sort(Sort.Direction.ASC, "nomeLaboratorio"));
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar ies via repository.", e);
            }
        }
        return iesDAO.listar();
    }

    public List<Ies> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        if (iesRepository != null) {
            int page = pagina <= 0 ? 0 : pagina - 1;
            int size = tamanhoPagina <= 0 ? 10 : tamanhoPagina;
            try {
                return iesRepository.findAll(
                        new PageRequest(page, size, new Sort(Sort.Direction.ASC, "nomeLaboratorio"))).getContent();
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar ies paginadas via repository.", e);
            }
        }
        return iesDAO.listarPaginado(pagina, tamanhoPagina);
    }

    public int contar() throws SQLException {
        if (iesRepository != null) {
            try {
                long total = iesRepository.count();
                return total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao contar ies via repository.", e);
            }
        }
        return iesDAO.contar();
    }

    public void excluir(Long id) throws SQLException {
        iesDAO.excluir(id);
    }

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long iesId) throws SQLException {
        return iesDAO.carregarCamposComplementaresPorCampoId(iesId);
    }

    public String exportarTodosTxtPipe() throws SQLException {
        List<Ies> itens = listar();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < itens.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(exportarLinhaTxtPipe(itens.get(i)));
        }
        return sb.toString();
    }

    public String exportarPorIdTxtPipe(Long id) throws SQLException {
        Ies ies = buscarPorId(id);
        if (ies == null) {
            return "";
        }
        return exportarLinhaTxtPipe(ies);
    }

    private SQLException toSqlException(String mensagem, RuntimeException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }
        return new SQLException(mensagem, e);
    }

    private static IesRepository resolveRepository() {
        try {
            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            if (context == null) {
                return null;
            }
            return context.getBean(IesRepository.class);
        } catch (Exception e) {
            return null;
        }
    }

    public int importarTxtPipe(String conteudo) throws SQLException {
        if (conteudo == null || conteudo.trim().length() == 0) {
            return 0;
        }
        Map<Integer, Long> campoIdPorNumero = layoutCampoDAO.mapaCampoIdPorNumero(ModulosLayout.IES_11);
        String[] linhas = conteudo.split("\\r?\\n");
        int importados = 0;
        Long idIesHeader = null;
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
            if ("10".equals(tipoRegistro)) {
                if (ValidationUtils.isNumeric(safeField(campos, 2))) {
                    idIesHeader = Long.valueOf(Long.parseLong(safeField(campos, 2)));
                }
                continue;
            }
            if (tipoRegistro.length() > 0 && !"11".equals(tipoRegistro)) {
                continue;
            }

            Ies ies = new Ies();
            ies.setIdIesInep(idIesHeader);
            ies.setNomeLaboratorio(safeField(campos, 2));
            ies.setRegistroLaboratorioIes(safeField(campos, 3));
            ies.setLaboratorioAtivoAno(parseIntegerOrDefault(safeField(campos, 4), 1));
            ies.setDescricaoAtividades(safeField(campos, 5));
            ies.setPalavrasChave(safeField(campos, 6));
            ies.setLaboratorioInformatica(parseIntegerOrNull(safeField(campos, 7)));

            Integer uf = parseIntegerOrNull(safeField(campos, 27));
            String municipio = safeField(campos, 28);
            if (uf == null && municipio.length() == 0) {
                uf = parseIntegerOrNull(safeField(campos, 17));
                municipio = safeField(campos, 18);
            }
            ies.setCodigoUfLaboratorio(uf);
            ies.setCodigoMunicipioLaboratorio(municipio);
            ies.setTipoLaboratorio(parseIntegerOrNull(safeField(campos, 29)));

            Map<Long, String> complementares = montarComplementaresImportacao(campos, campoIdPorNumero);
            cadastrar(ies, complementares);
            importados++;
        }
        return importados;
    }

    private String exportarLinhaTxtPipe(Ies ies) throws SQLException {
        Map<Integer, String> valores = iesDAO.carregarCamposRegistro11PorNumero(ies.getId());
        int max = 29;
        String[] campos = new String[max];
        for (int i = 0; i < max; i++) {
            campos[i] = "";
        }

        campos[0] = "11";
        campos[1] = safeValue(ies.getNomeLaboratorio());
        campos[2] = safeValue(ies.getRegistroLaboratorioIes());
        campos[3] = ies.getLaboratorioAtivoAno() != null ? String.valueOf(ies.getLaboratorioAtivoAno()) : "";
        campos[4] = safeValue(ies.getDescricaoAtividades());
        campos[5] = safeValue(ies.getPalavrasChave());
        campos[6] = ies.getLaboratorioInformatica() != null ? String.valueOf(ies.getLaboratorioInformatica()) : "";
        campos[26] = ies.getCodigoUfLaboratorio() != null ? String.valueOf(ies.getCodigoUfLaboratorio()) : "";
        campos[27] = safeValue(ies.getCodigoMunicipioLaboratorio());
        campos[28] = ies.getTipoLaboratorio() != null ? String.valueOf(ies.getTipoLaboratorio()) : "";

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
            if (numero >= 1 && numero <= 7) {
                continue;
            }
            if (numero == 17 || numero == 18 || numero == 27 || numero == 28) {
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

    private void validar(Ies ies) throws SQLException {
        if (ies == null) {
            throw new IllegalArgumentException("IES nao informada.");
        }
        if (ies.getNomeLaboratorio() == null || ies.getNomeLaboratorio().trim().length() == 0) {
            throw new IllegalArgumentException("Nome do laboratorio e obrigatorio.");
        }
        if (ies.getLaboratorioAtivoAno() == null) {
            ies.setLaboratorioAtivoAno(Integer.valueOf(1));
        }
        if (ies.getCodigoMunicipioLaboratorio() != null && ies.getCodigoMunicipioLaboratorio().trim().length() > 0) {
            if (ies.getCodigoUfLaboratorio() == null) {
                throw new IllegalArgumentException("UF do laboratorio e obrigatoria quando municipio e informado.");
            }
            if (!municipioDAO.existeCodigoNaUf(ies.getCodigoMunicipioLaboratorio(), ies.getCodigoUfLaboratorio())) {
                throw new IllegalArgumentException("Municipio do laboratorio nao pertence a UF informada.");
            }
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

    private Integer parseIntegerOrNull(String value) {
        if (!ValidationUtils.isNumeric(value)) {
            return null;
        }
        return Integer.valueOf(Integer.parseInt(value));
    }

    private Integer parseIntegerOrDefault(String value, int defaultValue) {
        if (!ValidationUtils.isNumeric(value)) {
            return Integer.valueOf(defaultValue);
        }
        return Integer.valueOf(Integer.parseInt(value));
    }
}
