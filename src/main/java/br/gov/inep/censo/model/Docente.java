package br.gov.inep.censo.model;

import br.gov.inep.censo.model.enums.CorRacaEnum;
import br.gov.inep.censo.model.enums.EstadoEnum;
import br.gov.inep.censo.model.enums.NacionalidadeEnum;
import br.gov.inep.censo.model.enums.PaisEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Map;

/**
 * Entidade de docente (Registro 31) com apoio a campos complementares de leiaute.
 */
@Entity
@Table(name = "docente")
public class Docente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_docente_ies", length = 20)
    private String idDocenteIes;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "cpf", nullable = false, length = 11)
    private String cpf;

    @Column(name = "documento_estrangeiro", length = 20)
    private String documentoEstrangeiro;

    @Column(name = "data_nascimento", nullable = false)
    private Date dataNascimento;

    @Column(name = "cor_raca")
    private Integer corRaca;

    @Column(name = "nacionalidade", nullable = false)
    private Integer nacionalidade;

    @Column(name = "pais_origem", nullable = false, length = 3)
    private String paisOrigem;

    @Column(name = "uf_nascimento")
    private Integer ufNascimento;

    @Column(name = "municipio_nascimento", length = 7)
    private String municipioNascimento;

    @Column(name = "docente_deficiencia")
    private Integer docenteDeficiencia;

    @Transient
    private Map<Long, String> camposComplementares;

    @Transient
    private Map<Integer, String> camposRegistro31;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdDocenteIes() {
        return idDocenteIes;
    }

    public void setIdDocenteIes(String idDocenteIes) {
        this.idDocenteIes = idDocenteIes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDocumentoEstrangeiro() {
        return documentoEstrangeiro;
    }

    public void setDocumentoEstrangeiro(String documentoEstrangeiro) {
        this.documentoEstrangeiro = documentoEstrangeiro;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getCorRaca() {
        return corRaca;
    }

    public void setCorRaca(Integer corRaca) {
        this.corRaca = corRaca;
    }

    public Integer getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(Integer nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getPaisOrigem() {
        return paisOrigem;
    }

    public void setPaisOrigem(String paisOrigem) {
        this.paisOrigem = paisOrigem;
    }

    public Integer getUfNascimento() {
        return ufNascimento;
    }

    public void setUfNascimento(Integer ufNascimento) {
        this.ufNascimento = ufNascimento;
    }

    public String getMunicipioNascimento() {
        return municipioNascimento;
    }

    public void setMunicipioNascimento(String municipioNascimento) {
        this.municipioNascimento = municipioNascimento;
    }

    public Integer getDocenteDeficiencia() {
        return docenteDeficiencia;
    }

    public void setDocenteDeficiencia(Integer docenteDeficiencia) {
        this.docenteDeficiencia = docenteDeficiencia;
    }

    public Map<Long, String> getCamposComplementares() {
        return camposComplementares;
    }

    public void setCamposComplementares(Map<Long, String> camposComplementares) {
        this.camposComplementares = camposComplementares;
    }

    public Map<Integer, String> getCamposRegistro31() {
        return camposRegistro31;
    }

    public void setCamposRegistro31(Map<Integer, String> camposRegistro31) {
        this.camposRegistro31 = camposRegistro31;
    }

    public CorRacaEnum getCorRacaEnum() {
        return CorRacaEnum.fromCodigo(corRaca);
    }

    public NacionalidadeEnum getNacionalidadeEnum() {
        return NacionalidadeEnum.fromCodigo(nacionalidade);
    }

    public PaisEnum getPaisOrigemEnum() {
        return PaisEnum.fromCodigo(paisOrigem);
    }

    public EstadoEnum getUfNascimentoEnum() {
        return EstadoEnum.fromCodigo(ufNascimento);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Docente target = new Docente();

        public Builder id(Long id) {
            target.setId(id);
            return this;
        }

        public Builder idDocenteIes(String idDocenteIes) {
            target.setIdDocenteIes(idDocenteIes);
            return this;
        }

        public Builder nome(String nome) {
            target.setNome(nome);
            return this;
        }

        public Builder cpf(String cpf) {
            target.setCpf(cpf);
            return this;
        }

        public Builder documentoEstrangeiro(String documentoEstrangeiro) {
            target.setDocumentoEstrangeiro(documentoEstrangeiro);
            return this;
        }

        public Builder dataNascimento(Date dataNascimento) {
            target.setDataNascimento(dataNascimento);
            return this;
        }

        public Builder corRaca(Integer corRaca) {
            target.setCorRaca(corRaca);
            return this;
        }

        public Builder nacionalidade(Integer nacionalidade) {
            target.setNacionalidade(nacionalidade);
            return this;
        }

        public Builder paisOrigem(String paisOrigem) {
            target.setPaisOrigem(paisOrigem);
            return this;
        }

        public Builder ufNascimento(Integer ufNascimento) {
            target.setUfNascimento(ufNascimento);
            return this;
        }

        public Builder municipioNascimento(String municipioNascimento) {
            target.setMunicipioNascimento(municipioNascimento);
            return this;
        }

        public Builder docenteDeficiencia(Integer docenteDeficiencia) {
            target.setDocenteDeficiencia(docenteDeficiencia);
            return this;
        }

        public Docente build() {
            return target;
        }
    }
}
