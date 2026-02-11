package br.gov.inep.censo.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Metadado de campo de leiaute oficial (CSV).
 */
@Entity
@Table(name = "layout_campo")
public class LayoutCampo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "modulo", nullable = false, length = 30)
    private String modulo;

    @Column(name = "numero_campo", nullable = false)
    private Integer numeroCampo;

    @Column(name = "nome_campo", nullable = false, length = 255)
    private String nomeCampo;

    @Column(name = "obrigatoriedade", length = 20)
    private String obrigatoriedade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Integer getNumeroCampo() {
        return numeroCampo;
    }

    public void setNumeroCampo(Integer numeroCampo) {
        this.numeroCampo = numeroCampo;
    }

    public String getNomeCampo() {
        return nomeCampo;
    }

    public void setNomeCampo(String nomeCampo) {
        this.nomeCampo = nomeCampo;
    }

    public String getObrigatoriedade() {
        return obrigatoriedade;
    }

    public void setObrigatoriedade(String obrigatoriedade) {
        this.obrigatoriedade = obrigatoriedade;
    }
}
