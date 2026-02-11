package br.gov.inep.censo.model;

import br.gov.inep.censo.model.enums.EstadoEnum;
import br.gov.inep.censo.model.enums.TipoLaboratorioEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

/**
 * Entidade de IES com foco no Registro 11 (laboratorio) e campos complementares.
 */
@Entity
@Table(name = "ies")
public class Ies implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_ies_inep")
    private Long idIesInep;

    @Column(name = "nome_laboratorio", nullable = false, length = 200)
    private String nomeLaboratorio;

    @Column(name = "registro_laboratorio_ies", length = 14)
    private String registroLaboratorioIes;

    @Column(name = "laboratorio_ativo_ano")
    private Integer laboratorioAtivoAno;

    @Column(name = "descricao_atividades", length = 2000)
    private String descricaoAtividades;

    @Column(name = "palavras_chave", length = 200)
    private String palavrasChave;

    @Column(name = "laboratorio_informatica")
    private Integer laboratorioInformatica;

    @Column(name = "tipo_laboratorio")
    private Integer tipoLaboratorio;

    @Column(name = "codigo_uf_laboratorio")
    private Integer codigoUfLaboratorio;

    @Column(name = "codigo_municipio_laboratorio", length = 7)
    private String codigoMunicipioLaboratorio;

    @Transient
    private Map<Long, String> camposComplementares;

    @Transient
    private Map<Integer, String> camposRegistro11;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdIesInep() {
        return idIesInep;
    }

    public void setIdIesInep(Long idIesInep) {
        this.idIesInep = idIesInep;
    }

    public String getNomeLaboratorio() {
        return nomeLaboratorio;
    }

    public void setNomeLaboratorio(String nomeLaboratorio) {
        this.nomeLaboratorio = nomeLaboratorio;
    }

    public String getRegistroLaboratorioIes() {
        return registroLaboratorioIes;
    }

    public void setRegistroLaboratorioIes(String registroLaboratorioIes) {
        this.registroLaboratorioIes = registroLaboratorioIes;
    }

    public Integer getLaboratorioAtivoAno() {
        return laboratorioAtivoAno;
    }

    public void setLaboratorioAtivoAno(Integer laboratorioAtivoAno) {
        this.laboratorioAtivoAno = laboratorioAtivoAno;
    }

    public String getDescricaoAtividades() {
        return descricaoAtividades;
    }

    public void setDescricaoAtividades(String descricaoAtividades) {
        this.descricaoAtividades = descricaoAtividades;
    }

    public String getPalavrasChave() {
        return palavrasChave;
    }

    public void setPalavrasChave(String palavrasChave) {
        this.palavrasChave = palavrasChave;
    }

    public Integer getLaboratorioInformatica() {
        return laboratorioInformatica;
    }

    public void setLaboratorioInformatica(Integer laboratorioInformatica) {
        this.laboratorioInformatica = laboratorioInformatica;
    }

    public Integer getTipoLaboratorio() {
        return tipoLaboratorio;
    }

    public void setTipoLaboratorio(Integer tipoLaboratorio) {
        this.tipoLaboratorio = tipoLaboratorio;
    }

    public Integer getCodigoUfLaboratorio() {
        return codigoUfLaboratorio;
    }

    public void setCodigoUfLaboratorio(Integer codigoUfLaboratorio) {
        this.codigoUfLaboratorio = codigoUfLaboratorio;
    }

    public String getCodigoMunicipioLaboratorio() {
        return codigoMunicipioLaboratorio;
    }

    public void setCodigoMunicipioLaboratorio(String codigoMunicipioLaboratorio) {
        this.codigoMunicipioLaboratorio = codigoMunicipioLaboratorio;
    }

    public Map<Long, String> getCamposComplementares() {
        return camposComplementares;
    }

    public void setCamposComplementares(Map<Long, String> camposComplementares) {
        this.camposComplementares = camposComplementares;
    }

    public Map<Integer, String> getCamposRegistro11() {
        return camposRegistro11;
    }

    public void setCamposRegistro11(Map<Integer, String> camposRegistro11) {
        this.camposRegistro11 = camposRegistro11;
    }

    public TipoLaboratorioEnum getTipoLaboratorioEnum() {
        return TipoLaboratorioEnum.fromCodigo(tipoLaboratorio);
    }

    public EstadoEnum getUfLaboratorioEnum() {
        return EstadoEnum.fromCodigo(codigoUfLaboratorio);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Ies target = new Ies();

        public Builder id(Long id) {
            target.setId(id);
            return this;
        }

        public Builder idIesInep(Long idIesInep) {
            target.setIdIesInep(idIesInep);
            return this;
        }

        public Builder nomeLaboratorio(String nomeLaboratorio) {
            target.setNomeLaboratorio(nomeLaboratorio);
            return this;
        }

        public Builder registroLaboratorioIes(String registroLaboratorioIes) {
            target.setRegistroLaboratorioIes(registroLaboratorioIes);
            return this;
        }

        public Builder laboratorioAtivoAno(Integer laboratorioAtivoAno) {
            target.setLaboratorioAtivoAno(laboratorioAtivoAno);
            return this;
        }

        public Builder descricaoAtividades(String descricaoAtividades) {
            target.setDescricaoAtividades(descricaoAtividades);
            return this;
        }

        public Builder palavrasChave(String palavrasChave) {
            target.setPalavrasChave(palavrasChave);
            return this;
        }

        public Builder laboratorioInformatica(Integer laboratorioInformatica) {
            target.setLaboratorioInformatica(laboratorioInformatica);
            return this;
        }

        public Builder tipoLaboratorio(Integer tipoLaboratorio) {
            target.setTipoLaboratorio(tipoLaboratorio);
            return this;
        }

        public Builder codigoUfLaboratorio(Integer codigoUfLaboratorio) {
            target.setCodigoUfLaboratorio(codigoUfLaboratorio);
            return this;
        }

        public Builder codigoMunicipioLaboratorio(String codigoMunicipioLaboratorio) {
            target.setCodigoMunicipioLaboratorio(codigoMunicipioLaboratorio);
            return this;
        }

        public Ies build() {
            return target;
        }
    }
}
