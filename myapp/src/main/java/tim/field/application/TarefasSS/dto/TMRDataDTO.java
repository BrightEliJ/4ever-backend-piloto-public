package tim.field.application.TarefasSS.dto;

import java.util.Date;

public class TMRDataDTO {

    private Long id;
    private String task;
    private String calculatedValue;
    private String grupo;
    private String status;
    private Date dataCriacao;
    private Date dataFechamento;

    // Construtores, getters e setters

    public TMRDataDTO() {
    }

    public TMRDataDTO(Long id, String task, String calculatedValue, String grupo, String status, Date dataCriacao, Date dataFechamento) {
        this.id = id;
        this.task = task;
        this.calculatedValue = calculatedValue;
        this.grupo = grupo;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataFechamento = dataFechamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getCalculatedValue() {
        return calculatedValue;
    }

    public void setCalculatedValue(String calculatedValue) {
        this.calculatedValue = calculatedValue;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }
}
