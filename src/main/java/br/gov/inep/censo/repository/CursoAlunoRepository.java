package br.gov.inep.censo.repository;

import br.gov.inep.censo.model.CursoAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CursoAlunoRepository extends JpaRepository<CursoAluno, Long> {

    @Query("select distinct ca from CursoAluno ca join fetch ca.aluno join fetch ca.curso order by ca.id desc")
    List<CursoAluno> findAllWithAlunoAndCursoOrderByIdDesc();
}
