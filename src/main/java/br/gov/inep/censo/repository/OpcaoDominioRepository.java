package br.gov.inep.censo.repository;

import br.gov.inep.censo.model.OpcaoDominio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpcaoDominioRepository extends JpaRepository<OpcaoDominio, Long> {

    List<OpcaoDominio> findByCategoriaOrderByNomeAsc(String categoria);
}
