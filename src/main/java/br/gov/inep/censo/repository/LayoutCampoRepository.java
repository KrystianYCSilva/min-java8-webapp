package br.gov.inep.censo.repository;

import br.gov.inep.censo.model.LayoutCampo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LayoutCampoRepository extends JpaRepository<LayoutCampo, Long> {

    List<LayoutCampo> findByModuloOrderByNumeroCampoAsc(String modulo);
}
