package br.gov.inep.censo.service;

import br.gov.inep.censo.domain.ModulosLayout;
import br.gov.inep.censo.model.Docente;
import br.gov.inep.censo.repository.DocenteRepository;
import br.gov.inep.censo.repository.LayoutCampoValueRepository;
import br.gov.inep.censo.repository.MunicipioRepository;
import br.gov.inep.censo.spring.SpringBridge;
import br.gov.inep.censo.util.ValidationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Servico de negocio do modulo Docente.
 */
public class DocenteService {

    private final LayoutCampoValueRepository layoutCampoValueRepository;
    private final DocenteRepository docenteRepository;
    private final MunicipioRepository municipioRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    public DocenteService() {
        this(new LayoutCampoValueRepository(),
                SpringBridge.getBean(DocenteRepository.class),
                SpringBridge.getBean(MunicipioRepository.class),
                SpringBridge.getBean(PlatformTransactionManager.class),
                SpringBridge.getBean(EntityManagerFactory.class));
    }

    public DocenteService(LayoutCampoValueRepository layoutCampoValueRepository,
                          DocenteRepository docenteRepository,
                          MunicipioRepository municipioRepository,
                          PlatformTransactionManager transactionManager,
                          EntityManagerFactory entityManagerFactory) {
        this.layoutCampoValueRepository = layoutCampoValueRepository;
        this.docenteRepository = docenteRepository;
        this.municipioRepository = municipioRepository;
        this.transactionManager = transactionManager;
        this.entityManagerFactory = entityManagerFactory;
    }

    public Long cadastrar(Docente docente, Map<Long, String> camposComplementares) throws SQLException {
        validar(docente);
        if (canUseRepositoryWritePath()) {
            final Docente docenteFinal = docente;
            final Map<Long, String> camposFinal = camposComplementares;
            return SpringBridge.inTransaction(transactionManager, entityManagerFactory,
                    new SpringBridge.SqlWork<Long>() {
                        public Long execute(EntityManager entityManager) throws SQLException {
                            Docente salvo = docenteRepository.save(docenteFinal);
                            Long docenteId = salvo != null ? salvo.getId() : docenteFinal.getId();
                            if (docenteId == null) {
                                throw new SQLException("Falha ao gerar ID para docente.");
                            }
                            layoutCampoValueRepository.salvarValoresDocente(entityManager, docenteId, camposFinal);
                            return docenteId;
                        }
                    }, "Falha ao cadastrar docente via repository.");
        }
        throw new SQLException("Infraestrutura Spring Data/Transaction indisponivel para cadastrar docente.");
    }

    public void atualizar(Docente docente, Map<Long, String> camposComplementares) throws SQLException {
        validar(docente);
        if (docente.getId() == null) {
            throw new IllegalArgumentException("ID do docente e obrigatorio para alteracao.");
        }
        if (canUseRepositoryWritePath()) {
            final Docente docenteFinal = docente;
            final Map<Long, String> camposFinal = camposComplementares;
            SpringBridge.inTransaction(transactionManager, entityManagerFactory,
                    new SpringBridge.SqlWork<Void>() {
                        public Void execute(EntityManager entityManager) throws SQLException {
                            docenteRepository.save(docenteFinal);
                            layoutCampoValueRepository.substituirValoresDocente(entityManager, docenteFinal.getId(), camposFinal);
                            return null;
                        }
                    }, "Falha ao atualizar docente via repository.");
            return;
        }
        throw new SQLException("Infraestrutura Spring Data/Transaction indisponivel para atualizar docente.");
    }

    public Docente buscarPorId(Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        if (docenteRepository != null) {
            try {
                return docenteRepository.findOne(id);
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao buscar docente via repository.", e);
            }
        }
        throw new SQLException("DocenteRepository indisponivel para buscar por ID.");
    }

    public List<Docente> listar() throws SQLException {
        if (docenteRepository != null) {
            try {
                return docenteRepository.findAll(new Sort(Sort.Direction.ASC, "nome"));
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar docentes via repository.", e);
            }
        }
        throw new SQLException("DocenteRepository indisponivel para listagem.");
    }

    public List<Docente> listarPaginado(int pagina, int tamanhoPagina) throws SQLException {
        if (docenteRepository != null) {
            int page = pagina <= 0 ? 0 : pagina - 1;
            int size = tamanhoPagina <= 0 ? 10 : tamanhoPagina;
            try {
                return docenteRepository.findAll(
                        new PageRequest(page, size, new Sort(Sort.Direction.ASC, "nome"))).getContent();
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar docentes paginados via repository.", e);
            }
        }
        throw new SQLException("DocenteRepository indisponivel para listagem paginada.");
    }

    public int contar() throws SQLException {
        if (docenteRepository != null) {
            try {
                long total = docenteRepository.count();
                return total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total;
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao contar docentes via repository.", e);
            }
        }
        throw new SQLException("DocenteRepository indisponivel para contagem.");
    }

    public void excluir(Long id) throws SQLException {
        if (id == null) {
            return;
        }
        if (canUseRepositoryWritePath()) {
            final Long idFinal = id;
            SpringBridge.inTransaction(transactionManager, entityManagerFactory,
                    new SpringBridge.SqlWork<Void>() {
                        public Void execute(EntityManager entityManager) throws SQLException {
                            layoutCampoValueRepository.removerValoresDocente(entityManager, idFinal);
                            if (docenteRepository.exists(idFinal)) {
                                docenteRepository.delete(idFinal);
                            }
                            return null;
                        }
                    }, "Falha ao excluir docente via repository.");
            return;
        }
        throw new SQLException("Infraestrutura Spring Data/Transaction indisponivel para excluir docente.");
    }

    public Map<Long, String> carregarCamposComplementaresPorCampoId(Long docenteId) throws SQLException {
        return layoutCampoValueRepository.carregarValoresDocentePorCampoId(docenteId);
    }

    public String exportarTodosTxtPipe() throws SQLException {
        List<Docente> docentes = listar();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < docentes.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(exportarLinhaTxtPipe(docentes.get(i)));
        }
        return sb.toString();
    }

    public String exportarPorIdTxtPipe(Long docenteId) throws SQLException {
        Docente docente = buscarPorId(docenteId);
        if (docente == null) {
            return "";
        }
        return exportarLinhaTxtPipe(docente);
    }

    private SQLException toSqlException(String mensagem, RuntimeException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }
        return new SQLException(mensagem, e);
    }

    private boolean canUseRepositoryWritePath() {
        return docenteRepository != null && transactionManager != null && entityManagerFactory != null;
    }

    public int importarTxtPipe(String conteudo) throws SQLException {
        if (conteudo == null || conteudo.trim().length() == 0) {
            return 0;
        }
        Map<Integer, Long> campoIdPorNumero = layoutCampoValueRepository.mapaCampoIdPorNumero(ModulosLayout.DOCENTE_31);
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
            if (tipoRegistro.length() > 0 && !"31".equals(tipoRegistro)) {
                continue;
            }

            Docente docente = new Docente();
            docente.setIdDocenteIes(safeField(campos, 2));
            docente.setNome(safeField(campos, 3));
            docente.setCpf(safeField(campos, 4));
            docente.setDocumentoEstrangeiro(safeField(campos, 5));
            docente.setDataNascimento(parseDateOrNull(safeField(campos, 6)));
            docente.setCorRaca(parseIntegerOrNull(safeField(campos, 7)));
            Integer nacionalidade = parseIntegerOrNull(safeField(campos, 8));
            docente.setNacionalidade(nacionalidade != null ? nacionalidade : Integer.valueOf(1));
            String pais = safeField(campos, 9);
            docente.setPaisOrigem(pais.length() == 0 ? "BRA" : pais);
            docente.setUfNascimento(parseIntegerOrNull(safeField(campos, 10)));
            docente.setMunicipioNascimento(safeField(campos, 11));
            docente.setDocenteDeficiencia(parseIntegerOrNull(safeField(campos, 12)));

            Map<Long, String> complementares = montarComplementaresImportacao(campos, campoIdPorNumero);
            cadastrar(docente, complementares);
            importados++;
        }
        return importados;
    }

    private String exportarLinhaTxtPipe(Docente docente) throws SQLException {
        Map<Integer, String> valores = layoutCampoValueRepository.carregarValoresDocentePorNumero(docente.getId(), ModulosLayout.DOCENTE_31);
        int max = 42;
        String[] campos = new String[max];
        for (int i = 0; i < max; i++) {
            campos[i] = "";
        }

        campos[0] = "31";
        campos[1] = safeValue(docente.getIdDocenteIes());
        campos[2] = safeValue(docente.getNome());
        campos[3] = safeValue(docente.getCpf());
        campos[4] = safeValue(docente.getDocumentoEstrangeiro());
        campos[5] = formatDateYYYYMMDD(docente.getDataNascimento());
        campos[6] = docente.getCorRaca() != null ? String.valueOf(docente.getCorRaca()) : "";
        campos[7] = docente.getNacionalidade() != null ? String.valueOf(docente.getNacionalidade()) : "";
        campos[8] = safeValue(docente.getPaisOrigem());
        campos[9] = docente.getUfNascimento() != null ? String.valueOf(docente.getUfNascimento()) : "";
        campos[10] = safeValue(docente.getMunicipioNascimento());
        campos[11] = docente.getDocenteDeficiencia() != null ? String.valueOf(docente.getDocenteDeficiencia()) : "";

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
            if (numero >= 1 && numero <= 12) {
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

    private void validar(Docente docente) throws SQLException {
        if (docente == null) {
            throw new IllegalArgumentException("Docente nao informado.");
        }
        if (docente.getNome() == null || docente.getNome().trim().length() == 0) {
            throw new IllegalArgumentException("Campo Nome e obrigatorio.");
        }
        if (!ValidationUtils.isCpfFormatoValido(docente.getCpf())) {
            throw new IllegalArgumentException("CPF deve conter 11 digitos numericos.");
        }
        if (docente.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de Nascimento e obrigatoria.");
        }
        if (docente.getNacionalidade() == null) {
            throw new IllegalArgumentException("Nacionalidade e obrigatoria.");
        }
        if (docente.getPaisOrigem() == null || docente.getPaisOrigem().trim().length() == 0) {
            docente.setPaisOrigem("BRA");
        }
        if (docente.getMunicipioNascimento() != null && docente.getMunicipioNascimento().trim().length() > 0) {
            if (docente.getUfNascimento() == null) {
                throw new IllegalArgumentException("UF de nascimento e obrigatoria quando municipio e informado.");
            }
            if (!existeMunicipioNaUf(docente.getMunicipioNascimento(), docente.getUfNascimento())) {
                throw new IllegalArgumentException("Municipio de nascimento nao pertence a UF informada.");
            }
        }
    }

    private boolean existeMunicipioNaUf(String codigoMunicipio, Integer codigoUf) throws SQLException {
        if (codigoMunicipio == null || codigoMunicipio.trim().length() == 0 || codigoUf == null) {
            return false;
        }
        if (municipioRepository != null) {
            try {
                return municipioRepository.existsByCodigoAndCodigoUf(codigoMunicipio.trim(), codigoUf);
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao validar municipio via repository.", e);
            }
        }
        throw new SQLException("MunicipioRepository indisponivel para validacao.");
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


