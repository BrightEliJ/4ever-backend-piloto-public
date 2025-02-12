package tim.field.application.TarefasSS.dto;

import java.util.List;
import java.util.Date;

public class TmrRequest {
    private String grupoAcionado;
    private List<String> statusList;
    private Date startDate;
    private Date endDate;

    // Getters e Setters

    public String getGrupoAcionado() {
        return grupoAcionado;
    }

    public void setGrupoAcionado(String grupoAcionado) {
        this.grupoAcionado = grupoAcionado;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date pStartDate) {
        this.startDate = pStartDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date pEndDate) {
        this.endDate = pEndDate;
    }
}

