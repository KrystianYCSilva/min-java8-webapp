package br.gov.inep.censo.repository;

import br.gov.inep.censo.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocenteRepository extends JpaRepository<Docente, Long> {
}
