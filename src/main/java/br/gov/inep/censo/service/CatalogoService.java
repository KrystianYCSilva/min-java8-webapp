package br.gov.inep.censo.service;

import br.gov.inep.censo.model.LayoutCampo;
import br.gov.inep.censo.model.OpcaoDominio;
import br.gov.inep.censo.repository.LayoutCampoRepository;
import br.gov.inep.censo.repository.OpcaoDominioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Servico de leitura para catalogos de opcoes e campos de leiaute.
 */
@Service
public class CatalogoService {

    private final OpcaoDominioRepository opcaoDominioRepository;
    private final LayoutCampoRepository layoutCampoRepository;

    @Autowired
    public CatalogoService(OpcaoDominioRepository opcaoDominioRepository,
                           LayoutCampoRepository layoutCampoRepository) {
        this.opcaoDominioRepository = opcaoDominioRepository;
        this.layoutCampoRepository = layoutCampoRepository;
    }

    public List<OpcaoDominio> listarOpcoesPorCategoria(String categoria) throws SQLException {
        if (opcaoDominioRepository != null) {
            try {
                return opcaoDominioRepository.findByCategoriaOrderByNomeAsc(categoria);
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar opcoes via repository.", e);
            }
        }
        throw new SQLException("OpcaoDominioRepository indisponivel para consulta de opcoes.");
    }

    public List<LayoutCampo> listarCamposModulo(String modulo) throws SQLException {
        if (layoutCampoRepository != null) {
            try {
                return layoutCampoRepository.findByModuloOrderByNumeroCampoAsc(modulo);
            } catch (RuntimeException e) {
                throw toSqlException("Falha ao listar campos de layout via repository.", e);
            }
        }
        throw new SQLException("LayoutCampoRepository indisponivel para consulta de campos.");
    }

    private SQLException toSqlException(String mensagem, RuntimeException e) {
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            return (SQLException) cause;
        }
        return new SQLException(mensagem, e);
    }

}
