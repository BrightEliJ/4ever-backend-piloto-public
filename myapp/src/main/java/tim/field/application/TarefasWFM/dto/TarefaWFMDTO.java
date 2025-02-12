package tim.field.application.TarefasWFM.dto;

import java.time.LocalDateTime;

public class TarefaWFMDTO {
    private String apptNumber;
    private String xaPiEvent;
    private LocalDateTime xaPiCreateDate;
    private String status;
    private String regional;
    private String company;
    private String resourceId;
    private String activityType;

    // Construtor
    public TarefaWFMDTO(String apptNumber, String xaPiEvent, LocalDateTime xaPiCreateDate, String status,
                         String regional, String company, String resourceId, String activityType) {
        this.apptNumber = apptNumber;
        this.xaPiEvent = xaPiEvent;
        this.xaPiCreateDate = xaPiCreateDate;
        this.status = status;
        this.regional = regional;
        this.company = company;
        this.resourceId = resourceId;
        this.activityType = activityType;
    }

    // Getters e Setters
    public String getApptNumber() { return apptNumber; }
    public void setApptNumber(String apptNumber) { this.apptNumber = apptNumber; }

    public String getXaPiEvent() { return xaPiEvent; }
    public void setXaPiEvent(String xaPiEvent) { this.xaPiEvent = xaPiEvent; }

    public LocalDateTime getXaPiCreateDate() { return xaPiCreateDate; }
    public void setXaPiCreateDate(LocalDateTime xaPiCreateDate) { this.xaPiCreateDate = xaPiCreateDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRegional() { return regional; }
    public void setRegional(String regional) { this.regional = regional; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
}