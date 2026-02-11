package br.gov.inep.censo.repository;

import br.gov.inep.censo.model.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MunicipioRepository extends JpaRepository<Municipio, String> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndCodigoUf(String codigo, Integer codigoUf);

    List<Municipio> findByCodigoUfOrderByNomeAsc(Integer codigoUf);
}
