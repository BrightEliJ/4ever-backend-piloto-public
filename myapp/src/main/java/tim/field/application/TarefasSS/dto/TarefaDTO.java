package tim.field.application.TarefasSS.dto;

import java.util.Date;

public class TarefaDTO {
    private String task;
    private String grupoAcionado;
    private String status;
    private Long downtime;
    private Date dataCriacao;
    private String eventoMassivo;

    // Construtores
    public TarefaDTO(String task, String grupoAcionado, String status, Long downtime, Date dataCriacao, String eventoMassivo) {
        this.task = task;
        this.grupoAcionado = grupoAcionado;
        this.status = status;
        this.downtime = downtime;
        this.dataCriacao = dataCriacao;
        this.eventoMassivo = eventoMassivo;
    }

    // Getters e Setters
    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }

    public String getGrupoAcionado() { return grupoAcionado; }
    public void setGrupoAcionado(String grupoAcionado) { this.grupoAcionado = grupoAcionado; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getDowntime() { return downtime; }
    public void setDowntime(Long downtime) { this.downtime = downtime; }

    public Date getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Date dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getEventoMassivo() { return eventoMassivo; }
    public void setEventoMassivo(String eventoMassivo) { this.eventoMassivo = eventoMassivo; }
}
