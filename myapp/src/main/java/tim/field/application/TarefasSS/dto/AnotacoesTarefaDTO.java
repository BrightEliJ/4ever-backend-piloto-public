package tim.field.application.TarefasSS.dto;

import java.util.Date;

public class AnotacoesTarefaDTO {
    private Long id;
    private String task;
    private String tipoAnotacao;
    private String anotacao;
    private String usuarioAnotacao;
    private Date dataAnotacao;

    public AnotacoesTarefaDTO(Long id, String task, String tipoAnotacao, String anotacao, String usuarioAnotacao, Date dataAnotacao) {
        this.id = id;
        this.task = task;
        this.tipoAnotacao = tipoAnotacao;
        this.anotacao = anotacao;
        this.usuarioAnotacao = usuarioAnotacao;
        this.dataAnotacao = dataAnotacao;
    }

    // Getters e Setters
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

    public String getTipoAnotacao() {
        return tipoAnotacao;
    }

    public void setTipoAnotacao(String tipoAnotacao) {
        this.tipoAnotacao = tipoAnotacao;
    }

    public String getAnotacao() {
        return anotacao;
    }

    public void setAnotacao(String anotacao) {
        this.anotacao = anotacao;
    }

    public String getUsuarioAnotacao() {
        return usuarioAnotacao;
    }

    public void setUsuarioAnotacao(String usuarioAnotacao) {
        this.usuarioAnotacao = usuarioAnotacao;
    }

    public Date getDataAnotacao() {
        return dataAnotacao;
    }

    public void setDataAnotacao(Date dataAnotacao) {
        this.dataAnotacao = dataAnotacao;
    }
}
