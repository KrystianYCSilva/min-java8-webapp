package br.gov.inep.censo.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

/**
 * Entidade de vinculo aluno-curso (Registro 42).
 */
@Entity
@Table(name = "curso_aluno")
public class CursoAluno implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Transient
    private Long alunoId;

    @Transient
    private Long cursoId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "id_aluno_ies", nullable = false, length = 30)
    private String idAlunoIes;

    @Column(name = "periodo_referencia", nullable = false, length = 4)
    private String periodoReferencia;

    @Column(name = "codigo_polo_ead", length = 12)
    private String codigoPoloEad;

    @Column(name = "turno_aluno")
    private Integer turnoAluno;

    @Column(name = "situacao_vinculo")
    private Integer situacaoVinculo;

    @Column(name = "curso_origem", length = 12)
    private String cursoOrigem;

    @Column(name = "semestre_conclusao", length = 6)
    private String semestreConclusao;

    @Column(name = "aluno_parfor")
    private Integer alunoParfor;

    @Column(name = "segunda_licenciatura_formacao")
    private Integer segundaLicenciaturaFormacao;

    @Column(name = "tipo_segunda_licenciatura_formacao")
    private Integer tipoSegundaLicenciaturaFormacao;

    @Column(name = "semestre_ingresso", length = 6)
    private String semestreIngresso;

    @Column(name = "forma_ingresso_vestibular")
    private Integer formaIngressoVestibular;

    @Column(name = "forma_ingresso_enem")
    private Integer formaIngressoEnem;

    @Column(name = "forma_ingresso_avaliacao_seriada")
    private Integer formaIngressoAvaliacaoSeriada;

    @Column(name = "forma_ingresso_selecao_simplificada")
    private Integer formaIngressoSelecaoSimplificada;

    @Column(name = "forma_ingresso_egresso_bi_li")
    private Integer formaIngressoEgressoBiLi;

    @Column(name = "forma_ingresso_pec_g")
    private Integer formaIngressoPecG;

    @Column(name = "forma_ingresso_transferencia_exofficio")
    private Integer formaIngressoTransferenciaExofficio;

    @Column(name = "forma_ingresso_decisao_judicial")
    private Integer formaIngressoDecisaoJudicial;

    @Column(name = "forma_ingresso_vagas_remanescentes")
    private Integer formaIngressoVagasRemanescentes;

    @Column(name = "forma_ingresso_programas_especiais")
    private Integer formaIngressoProgramasEspeciais;

    @Transient
    private String alunoNome;

    @Transient
    private String cursoNome;

    @Transient
    private String codigoCursoEmec;

    @Transient
    private String financiamentosResumo;

    @Transient
    private String apoioSocialResumo;

    @Transient
    private String atividadesResumo;

    @Transient
    private String reservasResumo;

    @Transient
    private Map<Long, String> camposComplementares;

    @Transient
    private Map<Integer, String> camposRegistro42;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlunoId() {
        if (alunoId == null && aluno != null) {
            return aluno.getId();
        }
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
        if (aluno != null) {
            Long alunoRelacionamentoId = aluno.getId();
            if (alunoRelacionamentoId == null || !alunoRelacionamentoId.equals(alunoId)) {
                this.aluno = null;
            }
        }
    }

    public Long getCursoId() {
        if (cursoId == null && curso != null) {
            return curso.getId();
        }
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
        if (curso != null) {
            Long cursoRelacionamentoId = curso.getId();
            if (cursoRelacionamentoId == null || !cursoRelacionamentoId.equals(cursoId)) {
                this.curso = null;
            }
        }
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
        this.alunoId = aluno == null ? null : aluno.getId();
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
        this.cursoId = curso == null ? null : curso.getId();
    }

    public String getIdAlunoIes() {
        return idAlunoIes;
    }

    public void setIdAlunoIes(String idAlunoIes) {
        this.idAlunoIes = idAlunoIes;
    }

    public String getPeriodoReferencia() {
        return periodoReferencia;
    }

    public void setPeriodoReferencia(String periodoReferencia) {
        this.periodoReferencia = periodoReferencia;
    }

    public String getCodigoPoloEad() {
        return codigoPoloEad;
    }

    public void setCodigoPoloEad(String codigoPoloEad) {
        this.codigoPoloEad = codigoPoloEad;
    }

    public Integer getTurnoAluno() {
        return turnoAluno;
    }

    public void setTurnoAluno(Integer turnoAluno) {
        this.turnoAluno = turnoAluno;
    }

    public Integer getSituacaoVinculo() {
        return situacaoVinculo;
    }

    public void setSituacaoVinculo(Integer situacaoVinculo) {
        this.situacaoVinculo = situacaoVinculo;
    }

    public String getCursoOrigem() {
        return cursoOrigem;
    }

    public void setCursoOrigem(String cursoOrigem) {
        this.cursoOrigem = cursoOrigem;
    }

    public String getSemestreConclusao() {
        return semestreConclusao;
    }

    public void setSemestreConclusao(String semestreConclusao) {
        this.semestreConclusao = semestreConclusao;
    }

    public Integer getAlunoParfor() {
        return alunoParfor;
    }

    public void setAlunoParfor(Integer alunoParfor) {
        this.alunoParfor = alunoParfor;
    }

    public Integer getSegundaLicenciaturaFormacao() {
        return segundaLicenciaturaFormacao;
    }

    public void setSegundaLicenciaturaFormacao(Integer segundaLicenciaturaFormacao) {
        this.segundaLicenciaturaFormacao = segundaLicenciaturaFormacao;
    }

    public Integer getTipoSegundaLicenciaturaFormacao() {
        return tipoSegundaLicenciaturaFormacao;
    }

    public void setTipoSegundaLicenciaturaFormacao(Integer tipoSegundaLicenciaturaFormacao) {
        this.tipoSegundaLicenciaturaFormacao = tipoSegundaLicenciaturaFormacao;
    }

    public String getSemestreIngresso() {
        return semestreIngresso;
    }

    public void setSemestreIngresso(String semestreIngresso) {
        this.semestreIngresso = semestreIngresso;
    }

    public Integer getFormaIngressoVestibular() {
        return formaIngressoVestibular;
    }

    public void setFormaIngressoVestibular(Integer formaIngressoVestibular) {
        this.formaIngressoVestibular = formaIngressoVestibular;
    }

    public Integer getFormaIngressoEnem() {
        return formaIngressoEnem;
    }

    public void setFormaIngressoEnem(Integer formaIngressoEnem) {
        this.formaIngressoEnem = formaIngressoEnem;
    }

    public Integer getFormaIngressoAvaliacaoSeriada() {
        return formaIngressoAvaliacaoSeriada;
    }

    public void setFormaIngressoAvaliacaoSeriada(Integer formaIngressoAvaliacaoSeriada) {
        this.formaIngressoAvaliacaoSeriada = formaIngressoAvaliacaoSeriada;
    }

    public Integer getFormaIngressoSelecaoSimplificada() {
        return formaIngressoSelecaoSimplificada;
    }

    public void setFormaIngressoSelecaoSimplificada(Integer formaIngressoSelecaoSimplificada) {
        this.formaIngressoSelecaoSimplificada = formaIngressoSelecaoSimplificada;
    }

    public Integer getFormaIngressoEgressoBiLi() {
        return formaIngressoEgressoBiLi;
    }

    public void setFormaIngressoEgressoBiLi(Integer formaIngressoEgressoBiLi) {
        this.formaIngressoEgressoBiLi = formaIngressoEgressoBiLi;
    }

    public Integer getFormaIngressoPecG() {
        return formaIngressoPecG;
    }

    public void setFormaIngressoPecG(Integer formaIngressoPecG) {
        this.formaIngressoPecG = formaIngressoPecG;
    }

    public Integer getFormaIngressoTransferenciaExofficio() {
        return formaIngressoTransferenciaExofficio;
    }

    public void setFormaIngressoTransferenciaExofficio(Integer formaIngressoTransferenciaExofficio) {
        this.formaIngressoTransferenciaExofficio = formaIngressoTransferenciaExofficio;
    }

    public Integer getFormaIngressoDecisaoJudicial() {
        return formaIngressoDecisaoJudicial;
    }

    public void setFormaIngressoDecisaoJudicial(Integer formaIngressoDecisaoJudicial) {
        this.formaIngressoDecisaoJudicial = formaIngressoDecisaoJudicial;
    }

    public Integer getFormaIngressoVagasRemanescentes() {
        return formaIngressoVagasRemanescentes;
    }

    public void setFormaIngressoVagasRemanescentes(Integer formaIngressoVagasRemanescentes) {
        this.formaIngressoVagasRemanescentes = formaIngressoVagasRemanescentes;
    }

    public Integer getFormaIngressoProgramasEspeciais() {
        return formaIngressoProgramasEspeciais;
    }

    public void setFormaIngressoProgramasEspeciais(Integer formaIngressoProgramasEspeciais) {
        this.formaIngressoProgramasEspeciais = formaIngressoProgramasEspeciais;
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public void setAlunoNome(String alunoNome) {
        this.alunoNome = alunoNome;
    }

    public String getCursoNome() {
        return cursoNome;
    }

    public void setCursoNome(String cursoNome) {
        this.cursoNome = cursoNome;
    }

    public String getCodigoCursoEmec() {
        return codigoCursoEmec;
    }

    public void setCodigoCursoEmec(String codigoCursoEmec) {
        this.codigoCursoEmec = codigoCursoEmec;
    }

    public String getFinanciamentosResumo() {
        return financiamentosResumo;
    }

    public void setFinanciamentosResumo(String financiamentosResumo) {
        this.financiamentosResumo = financiamentosResumo;
    }

    public String getApoioSocialResumo() {
        return apoioSocialResumo;
    }

    public void setApoioSocialResumo(String apoioSocialResumo) {
        this.apoioSocialResumo = apoioSocialResumo;
    }

    public String getAtividadesResumo() {
        return atividadesResumo;
    }

    public void setAtividadesResumo(String atividadesResumo) {
        this.atividadesResumo = atividadesResumo;
    }

    public String getReservasResumo() {
        return reservasResumo;
    }

    public void setReservasResumo(String reservasResumo) {
        this.reservasResumo = reservasResumo;
    }

    public Map<Long, String> getCamposComplementares() {
        return camposComplementares;
    }

    public void setCamposComplementares(Map<Long, String> camposComplementares) {
        this.camposComplementares = camposComplementares;
    }

    public Map<Integer, String> getCamposRegistro42() {
        return camposRegistro42;
    }

    public void setCamposRegistro42(Map<Integer, String> camposRegistro42) {
        this.camposRegistro42 = camposRegistro42;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final CursoAluno target = new CursoAluno();

        public Builder id(Long id) {
            target.setId(id);
            return this;
        }

        public Builder alunoId(Long alunoId) {
            target.setAlunoId(alunoId);
            return this;
        }

        public Builder cursoId(Long cursoId) {
            target.setCursoId(cursoId);
            return this;
        }

        public Builder idAlunoIes(String idAlunoIes) {
            target.setIdAlunoIes(idAlunoIes);
            return this;
        }

        public Builder periodoReferencia(String periodoReferencia) {
            target.setPeriodoReferencia(periodoReferencia);
            return this;
        }

        public Builder codigoPoloEad(String codigoPoloEad) {
            target.setCodigoPoloEad(codigoPoloEad);
            return this;
        }

        public Builder turnoAluno(Integer turnoAluno) {
            target.setTurnoAluno(turnoAluno);
            return this;
        }

        public Builder situacaoVinculo(Integer situacaoVinculo) {
            target.setSituacaoVinculo(situacaoVinculo);
            return this;
        }

        public Builder cursoOrigem(String cursoOrigem) {
            target.setCursoOrigem(cursoOrigem);
            return this;
        }

        public Builder semestreConclusao(String semestreConclusao) {
            target.setSemestreConclusao(semestreConclusao);
            return this;
        }

        public Builder alunoParfor(Integer alunoParfor) {
            target.setAlunoParfor(alunoParfor);
            return this;
        }

        public Builder segundaLicenciaturaFormacao(Integer segundaLicenciaturaFormacao) {
            target.setSegundaLicenciaturaFormacao(segundaLicenciaturaFormacao);
            return this;
        }

        public Builder tipoSegundaLicenciaturaFormacao(Integer tipoSegundaLicenciaturaFormacao) {
            target.setTipoSegundaLicenciaturaFormacao(tipoSegundaLicenciaturaFormacao);
            return this;
        }

        public Builder semestreIngresso(String semestreIngresso) {
            target.setSemestreIngresso(semestreIngresso);
            return this;
        }

        public Builder formaIngressoVestibular(Integer formaIngressoVestibular) {
            target.setFormaIngressoVestibular(formaIngressoVestibular);
            return this;
        }

        public Builder formaIngressoEnem(Integer formaIngressoEnem) {
            target.setFormaIngressoEnem(formaIngressoEnem);
            return this;
        }

        public Builder formaIngressoAvaliacaoSeriada(Integer formaIngressoAvaliacaoSeriada) {
            target.setFormaIngressoAvaliacaoSeriada(formaIngressoAvaliacaoSeriada);
            return this;
        }

        public Builder formaIngressoSelecaoSimplificada(Integer formaIngressoSelecaoSimplificada) {
            target.setFormaIngressoSelecaoSimplificada(formaIngressoSelecaoSimplificada);
            return this;
        }

        public Builder formaIngressoEgressoBiLi(Integer formaIngressoEgressoBiLi) {
            target.setFormaIngressoEgressoBiLi(formaIngressoEgressoBiLi);
            return this;
        }

        public Builder formaIngressoPecG(Integer formaIngressoPecG) {
            target.setFormaIngressoPecG(formaIngressoPecG);
            return this;
        }

        public Builder formaIngressoTransferenciaExofficio(Integer formaIngressoTransferenciaExofficio) {
            target.setFormaIngressoTransferenciaExofficio(formaIngressoTransferenciaExofficio);
            return this;
        }

        public Builder formaIngressoDecisaoJudicial(Integer formaIngressoDecisaoJudicial) {
            target.setFormaIngressoDecisaoJudicial(formaIngressoDecisaoJudicial);
            return this;
        }

        public Builder formaIngressoVagasRemanescentes(Integer formaIngressoVagasRemanescentes) {
            target.setFormaIngressoVagasRemanescentes(formaIngressoVagasRemanescentes);
            return this;
        }

        public Builder formaIngressoProgramasEspeciais(Integer formaIngressoProgramasEspeciais) {
            target.setFormaIngressoProgramasEspeciais(formaIngressoProgramasEspeciais);
            return this;
        }

        public CursoAluno build() {
            if (target.getAlunoParfor() == null) {
                target.setAlunoParfor(Integer.valueOf(0));
            }
            if (target.getSegundaLicenciaturaFormacao() == null) {
                target.setSegundaLicenciaturaFormacao(Integer.valueOf(0));
            }
            if (target.getFormaIngressoVestibular() == null) {
                target.setFormaIngressoVestibular(Integer.valueOf(0));
            }
            if (target.getFormaIngressoEnem() == null) {
                target.setFormaIngressoEnem(Integer.valueOf(0));
            }
            if (target.getFormaIngressoAvaliacaoSeriada() == null) {
                target.setFormaIngressoAvaliacaoSeriada(Integer.valueOf(0));
            }
            if (target.getFormaIngressoSelecaoSimplificada() == null) {
                target.setFormaIngressoSelecaoSimplificada(Integer.valueOf(0));
            }
            if (target.getFormaIngressoEgressoBiLi() == null) {
                target.setFormaIngressoEgressoBiLi(Integer.valueOf(0));
            }
            if (target.getFormaIngressoPecG() == null) {
                target.setFormaIngressoPecG(Integer.valueOf(0));
            }
            if (target.getFormaIngressoTransferenciaExofficio() == null) {
                target.setFormaIngressoTransferenciaExofficio(Integer.valueOf(0));
            }
            if (target.getFormaIngressoDecisaoJudicial() == null) {
                target.setFormaIngressoDecisaoJudicial(Integer.valueOf(0));
            }
            if (target.getFormaIngressoVagasRemanescentes() == null) {
                target.setFormaIngressoVagasRemanescentes(Integer.valueOf(0));
            }
            if (target.getFormaIngressoProgramasEspeciais() == null) {
                target.setFormaIngressoProgramasEspeciais(Integer.valueOf(0));
            }
            return target;
        }
    }
}
