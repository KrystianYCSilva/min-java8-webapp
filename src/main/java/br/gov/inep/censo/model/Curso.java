package br.gov.inep.censo.model;

import br.gov.inep.censo.model.enums.FormatoOfertaEnum;
import br.gov.inep.censo.model.enums.NivelAcademicoEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

/**
 * Entidade de curso (Registro 21) com suporte a campos normalizados e complementares.
 */
@Entity
@Table(name = "curso")
public class Curso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo_curso_emec", nullable = false, length = 12)
    private String codigoCursoEmec;

    @Column(name = "nome", nullable = false, length = 160)
    private String nome;

    @Column(name = "nivel_academico", nullable = false, length = 30)
    private String nivelAcademico;

    @Column(name = "formato_oferta", nullable = false, length = 20)
    private String formatoOferta;

    @Column(name = "curso_teve_aluno_vinculado", nullable = false)
    private Integer cursoTeveAlunoVinculado;

    @Transient
    private String recursosTecnologiaAssistivaResumo;

    @Transient
    private Map<Long, String> camposComplementares;

    @Transient
    private Map<Integer, String> camposRegistro21;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoCursoEmec() {
        return codigoCursoEmec;
    }

    public void setCodigoCursoEmec(String codigoCursoEmec) {
        this.codigoCursoEmec = codigoCursoEmec;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNivelAcademico() {
        return nivelAcademico;
    }

    public void setNivelAcademico(String nivelAcademico) {
        this.nivelAcademico = nivelAcademico;
    }

    public NivelAcademicoEnum getNivelAcademicoEnum() {
        return NivelAcademicoEnum.fromCodigo(nivelAcademico);
    }

    public String getFormatoOferta() {
        return formatoOferta;
    }

    public void setFormatoOferta(String formatoOferta) {
        this.formatoOferta = formatoOferta;
    }

    public FormatoOfertaEnum getFormatoOfertaEnum() {
        return FormatoOfertaEnum.fromCodigo(formatoOferta);
    }

    public Integer getCursoTeveAlunoVinculado() {
        return cursoTeveAlunoVinculado;
    }

    public void setCursoTeveAlunoVinculado(Integer cursoTeveAlunoVinculado) {
        this.cursoTeveAlunoVinculado = cursoTeveAlunoVinculado;
    }

    public String getRecursosTecnologiaAssistivaResumo() {
        return recursosTecnologiaAssistivaResumo;
    }

    public void setRecursosTecnologiaAssistivaResumo(String recursosTecnologiaAssistivaResumo) {
        this.recursosTecnologiaAssistivaResumo = recursosTecnologiaAssistivaResumo;
    }

    public Map<Long, String> getCamposComplementares() {
        return camposComplementares;
    }

    public void setCamposComplementares(Map<Long, String> camposComplementares) {
        this.camposComplementares = camposComplementares;
    }

    public Map<Integer, String> getCamposRegistro21() {
        return camposRegistro21;
    }

    public void setCamposRegistro21(Map<Integer, String> camposRegistro21) {
        this.camposRegistro21 = camposRegistro21;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Curso target = new Curso();

        public Builder id(Long id) {
            target.setId(id);
            return this;
        }

        public Builder codigoCursoEmec(String codigoCursoEmec) {
            target.setCodigoCursoEmec(codigoCursoEmec);
            return this;
        }

        public Builder nome(String nome) {
            target.setNome(nome);
            return this;
        }

        public Builder nivelAcademico(String nivelAcademico) {
            target.setNivelAcademico(nivelAcademico);
            return this;
        }

        public Builder formatoOferta(String formatoOferta) {
            target.setFormatoOferta(formatoOferta);
            return this;
        }

        public Builder cursoTeveAlunoVinculado(Integer cursoTeveAlunoVinculado) {
            target.setCursoTeveAlunoVinculado(cursoTeveAlunoVinculado);
            return this;
        }

        public Curso build() {
            return target;
        }
    }
}
