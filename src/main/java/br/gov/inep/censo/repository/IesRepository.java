package br.gov.inep.censo.repository;

import br.gov.inep.censo.model.Ies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IesRepository extends JpaRepository<Ies, Long> {
}
