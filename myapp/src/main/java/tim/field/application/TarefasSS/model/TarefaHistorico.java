package tim.field.application.TarefasSS.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_historico_status_tarefa")
public class TarefaHistorico {

        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "task")
    private String task;

    @Column(name = "matricula")
    private String matricula;

    @Column(name = "nome")
    private String nome;

    @Column(name = "grupo_acionado")
    private String grupoAcionado;

    @Column(name = "acao")
    private String acao;

    @Column(name = "status")
    private String status;

    @Column(name = "data_e_hora_inicio")
    private Date dataInicio;

    @Column(name = "data_e_hora_fim")
    private Date dataFim;

    @Column(name = "sys_created_on")
    private Date sysCreatedOn;

    @Column(name = "sys_created_by")
    private String sysCreatedBy;

    @Column(name = "em_analise_por_historico")
    private String analisePorHistorico;

    @Column(name = "sys_tags")
    private String sysTags;

    @Column(name = "sys_updated_on")
    private Date sysUpdatedOn;

    @Column(name = "sys_updated_by")
    private String sysUpdatedBy;

    @Column(name = "sys_mod_count")
    private Long sysModCount;

    @Column(name = "data_reg")
    private Date dataReg;

    public Date getDataReg() {
        return dataReg;
    }

    public void setDataReg(Date dataReg) {
        this.dataReg = dataReg;
    }

    public Long getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGrupoAcionado() {
        return grupoAcionado;
    }

    public void setGrupoAcionado(String grupoAcionado) {
        this.grupoAcionado = grupoAcionado;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Date getSysCreatedOn() {
        return sysCreatedOn;
    }

    public void setSysCreatedOn(Date sysCreatedOn) {
        this.sysCreatedOn = sysCreatedOn;
    }

    public String getSysCreatedBy() {
        return sysCreatedBy;
    }

    public void setSysCreatedBy(String sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
    }

    public String getanAlisePorHistorico() {
        return analisePorHistorico;
    }

    public void setAlisePorHistorico(String analisePorHistorico) {
        this.analisePorHistorico = analisePorHistorico;
    }

    public String getSysTags() {
        return sysTags;
    }

    public void setSysTags(String sysTags) {
        this.sysTags = sysTags;
    }

    public Date getSysUpdatedOn() {
        return sysUpdatedOn;
    }

    public void setSysUpdatedOn(Date sysUpdatedOn) {
        this.sysUpdatedOn = sysUpdatedOn;
    }

    public String getSysUpdatedBy() {
        return sysUpdatedBy;
    }

    public void setSysUpdatedBy(String sysUpdatedBy) {
        this.sysUpdatedBy = sysUpdatedBy;
    }

    public Long getSysModCount() {
        return sysModCount;
    }

    public void setSysModCount(Long sysModCount) {
        this.sysModCount = sysModCount;
    }
}
