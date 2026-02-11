package br.gov.inep.censo.repository;

import br.gov.inep.censo.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
}
