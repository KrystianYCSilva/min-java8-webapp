package br.gov.inep.censo.repository;

import br.gov.inep.censo.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}
