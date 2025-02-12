package tim.field.application.TarefasWFM.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "backlog_activities")
public class BacklogActivities {

    @Id
    @Column(name = "activityId") // Força o uso do nome exato no banco
    private Long activityId;    

    @Column(name = "XA_PI_CREATE_DATE")
    private LocalDateTime xaPiCreateDate;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "A_TIME_OF_BOOKING")
    private LocalDateTime aTimeOfBooking;

    @Column(name = "apptNumber")
    private String apptNumber;

    @Column(name = "XA_PI_EVENT")
    private String xaPiEvent;

    @Column(name = "activityType")
    private String activityType;

    @Column(name = "status")
    private String status;

    @Column(name = "resourceInternalId")
    private String resourceInternalId;

    @Column(name = "resourceId")
    private String resourceId;

    @Column(name = "XA_ORIGIN_BUCKET")
    private String xaOriginBucket;

    @Column(name = "regional")
    private String regional;

    @Column(name = "contract")
    private String contract;

    @Column(name = "company")
    private String company;

    @Column(name = "stateProvince")
    private String stateProvince;

    @Column(name = "city")
    private String city;

    @Column(name = "XA_EXECUTOR_USER")
    private String xaExecutorUser;

    @Column(name = "startTime")
    private String startTime;

    @Column(name = "endTime")
    private String endTime;

    @Column(name = "XA_PI_ALARM_TYPE")
    private String xaPiAlarmType;

    @Column(name = "XA_PI_FAIL_TYPE")
    private String xaPiFailType;

    @Column(name = "XA_PI_CM")
    private String xaPiCm;

    @Column(name = "XA_PI_END_ID")
    private String xaPiEndId;

    @Column(name = "XA_PI_NETWORK_ELEMENT")
    private String xaPiNetworkElement;

    @Column(name = "XA_PI_NE_TYPE")
    private String xaPiNeType;

    @Column(name = "XA_PI_NETWORK")
    private String xaPiNetwork;

    @Column(name = "XA_PI_NOTDONE_REASON")
    private String xaPiNotdoneReason;

    @Column(name = "XA_PI_OP")
    private String xaPiOp;

    @Column(name = "XA_PI_OPENING_NOTE")
    private String xaPiOpeningNote;

    @Column(name = "XA_PI_PRIORITY")
    private String xaPiPriority;

    @Column(name = "XA_PI_RESPONSABLE")
    private String xaPiResponsable;

    @Column(name = "XA_PI_SUB_AREA")
    private String xaPiSubArea;

    @Column(name = "XA_PI_SUSPEND_REASON")
    private String xaPiSuspendReason;

    @Column(name = "XA_PI_TRAM_REASON")
    private String xaPiTramReason;

    @Column(name = "XA_PI_TRAM_SUS")
    private String xaPiTramSus;

    @Column(name = "XA_PI_CRITERION")
    private String xaPiCriterion;

    @Column(name = "XA_PI_GMG_START")
    private LocalDateTime xaPiGmgStart;

    @Column(name = "XA_PI_GMG_END")
    private LocalDateTime xaPiGmgEnd;

    @Column(name = "XA_PI_GMG_STATUS")
    private String xaPiGmgStatus;

    @Column(name = "XA_PI_GMG_OWNER")
    private String xaPiGmgOwner;

    @Column(name = "XA_PI_GMG_DESC")
    private String xaPiGmgDesc;

    @Column(name = "XA_DISP_PRI")
    private String xaDispPri;

    @Column(name = "XA_DISP_PRI_FAIXA")
    private String xaDispPriFaixa;

    @Column(name = "workZone")
    private String workZone;

    @Column(name = "XA_WORKZONE_KEY")
    private String xaWorkzoneKey;

    @Column(name = "XA_FIRST_ROUTING_TIME")
    private LocalDateTime xaFirstRoutingTime;

    @Column(name = "A_AUTO_ROUTED_TO_DATE")
    private LocalDate aAutoRoutedToDate;

    @Column(name = "XA_PI_SS_GSBI_CLASS")
    private String xaPiSsGsbiClass;

    @Column(name = "XA_PI_SS_GSBI_CLASS_NAME")
    private String xaPiSsGsbiClassName;

    @Column(name = "XA_PI_SS_EQP_SEG_NET")
    private String xaPiSsEqpSegNet;

    @Column(name = "XA_PI_SS_EQP_FUNCTION")
    private String xaPiSsEqpFunction;

    @Column(name = "XA_PI_INDUSTRIAL_BUILDINGS")
    private String xaPiIndustrialBuildings;

    @Column(name = "XA_PI_NTT_USER_RULE")
    private String xaPiNttUserRule;

    @Column(name = "activity_group")
    private String activityGroup;

    @Column(name = "XA_PI_ID_TICKET_CA")
    private String xaPiIdTicketCa;

    @Column(name = "XA_PI_CLOSE_NOTE")
    private String xaPiCloseNote;

    @Column(name = "XA_PI_FAIL_CAUSE")
    private String xaPiFailCause;

    @Column(name = "XA_PI_FAIL_SOLUTION")
    private String xaPiFailSolution;

    @Column(name = "collect_date")
    private LocalDate collectDate;

    // Implementação de equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BacklogActivities that = (BacklogActivities) o;
        return Objects.equals(xaPiCreateDate, that.xaPiCreateDate) &&
                Objects.equals(date, that.date) &&
                Objects.equals(aTimeOfBooking, that.aTimeOfBooking) &&
                Objects.equals(activityId, that.activityId) &&
                Objects.equals(apptNumber, that.apptNumber) &&
                Objects.equals(xaPiEvent, that.xaPiEvent) &&
                Objects.equals(activityType, that.activityType) &&
                Objects.equals(status, that.status) &&
                Objects.equals(resourceInternalId, that.resourceInternalId) &&
                Objects.equals(resourceId, that.resourceId) &&
                Objects.equals(xaOriginBucket, that.xaOriginBucket) &&
                Objects.equals(regional, that.regional) &&
                Objects.equals(contract, that.contract) &&
                Objects.equals(company, that.company) &&
                Objects.equals(stateProvince, that.stateProvince) &&
                Objects.equals(city, that.city) &&
                Objects.equals(xaExecutorUser, that.xaExecutorUser) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(xaPiAlarmType, that.xaPiAlarmType) &&
                Objects.equals(xaPiFailType, that.xaPiFailType) &&
                Objects.equals(xaPiCm, that.xaPiCm) &&
                Objects.equals(xaPiEndId, that.xaPiEndId) &&
                Objects.equals(xaPiNetworkElement, that.xaPiNetworkElement) &&
                Objects.equals(xaPiNeType, that.xaPiNeType) &&
                Objects.equals(xaPiNetwork, that.xaPiNetwork) &&
                Objects.equals(xaPiNotdoneReason, that.xaPiNotdoneReason) &&
                Objects.equals(xaPiOp, that.xaPiOp) &&
                Objects.equals(xaPiOpeningNote, that.xaPiOpeningNote) &&
                Objects.equals(xaPiPriority, that.xaPiPriority) &&
                Objects.equals(xaPiResponsable, that.xaPiResponsable) &&
                Objects.equals(xaPiSubArea, that.xaPiSubArea) &&
                Objects.equals(xaPiSuspendReason, that.xaPiSuspendReason) &&
                Objects.equals(xaPiTramReason, that.xaPiTramReason) &&
                Objects.equals(xaPiTramSus, that.xaPiTramSus) &&
                Objects.equals(xaPiCriterion, that.xaPiCriterion) &&
                Objects.equals(xaPiGmgStart, that.xaPiGmgStart) &&
                Objects.equals(xaPiGmgEnd, that.xaPiGmgEnd) &&
                Objects.equals(xaPiGmgStatus, that.xaPiGmgStatus) &&
                Objects.equals(xaPiGmgOwner, that.xaPiGmgOwner) &&
                Objects.equals(xaPiGmgDesc, that.xaPiGmgDesc) &&
                Objects.equals(xaDispPri, that.xaDispPri) &&
                Objects.equals(xaDispPriFaixa, that.xaDispPriFaixa) &&
                Objects.equals(workZone, that.workZone) &&
                Objects.equals(xaWorkzoneKey, that.xaWorkzoneKey) &&
                Objects.equals(xaFirstRoutingTime, that.xaFirstRoutingTime) &&
                Objects.equals(aAutoRoutedToDate, that.aAutoRoutedToDate) &&
                Objects.equals(xaPiSsGsbiClass, that.xaPiSsGsbiClass) &&
                Objects.equals(xaPiSsGsbiClassName, that.xaPiSsGsbiClassName) &&
                Objects.equals(xaPiSsEqpSegNet, that.xaPiSsEqpSegNet) &&
                Objects.equals(xaPiSsEqpFunction, that.xaPiSsEqpFunction) &&
                Objects.equals(xaPiIndustrialBuildings, that.xaPiIndustrialBuildings) &&
                Objects.equals(xaPiNttUserRule, that.xaPiNttUserRule) &&
                Objects.equals(activityGroup, that.activityGroup) &&
                Objects.equals(xaPiIdTicketCa, that.xaPiIdTicketCa) &&
                Objects.equals(xaPiCloseNote, that.xaPiCloseNote) &&
                Objects.equals(xaPiFailCause, that.xaPiFailCause) &&
                Objects.equals(xaPiFailSolution, that.xaPiFailSolution) &&
                Objects.equals(collectDate, that.collectDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, xaPiCreateDate, date, aTimeOfBooking, apptNumber, xaPiEvent, activityType, status, resourceInternalId, resourceId,
                xaOriginBucket, regional, contract, company, stateProvince, city, xaExecutorUser, startTime, endTime, xaPiAlarmType, xaPiFailType, xaPiCm,
                xaPiEndId, xaPiNetworkElement, xaPiNeType, xaPiNetwork, xaPiNotdoneReason, xaPiOp, xaPiOpeningNote, xaPiPriority, xaPiResponsable, xaPiSubArea,
                xaPiSuspendReason, xaPiTramReason, xaPiTramSus, xaPiCriterion, xaPiGmgStart, xaPiGmgEnd, xaPiGmgStatus, xaPiGmgOwner, xaPiGmgDesc, xaDispPri,
                xaDispPriFaixa, workZone, xaWorkzoneKey, xaFirstRoutingTime, aAutoRoutedToDate, xaPiSsGsbiClass, xaPiSsGsbiClassName, xaPiSsEqpSegNet,
                xaPiSsEqpFunction, xaPiIndustrialBuildings, xaPiNttUserRule, activityGroup, xaPiIdTicketCa, xaPiCloseNote, xaPiFailCause, xaPiFailSolution, collectDate);
    }

    //Getters e setters


    public LocalDateTime getXaPiCreateDate() {
        return xaPiCreateDate;
    }

    public void setXaPiCreateDate(LocalDateTime xaPiCreateDate) {
        this.xaPiCreateDate = xaPiCreateDate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getaTimeOfBooking() {
        return aTimeOfBooking;
    }

    public void setaTimeOfBooking(LocalDateTime aTimeOfBooking) {
        this.aTimeOfBooking = aTimeOfBooking;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getApptNumber() {
        return apptNumber;
    }

    public void setApptNumber(String apptNumber) {
        this.apptNumber = apptNumber;
    }

    public String getXaPiEvent() {
        return xaPiEvent;
    }

    public void setXaPiEvent(String xaPiEvent) {
        this.xaPiEvent = xaPiEvent;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResourceInternalId() {
        return resourceInternalId;
    }

    public void setResourceInternalId(String resourceInternalId) {
        this.resourceInternalId = resourceInternalId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getXaOriginBucket() {
        return xaOriginBucket;
    }

    public void setXaOriginBucket(String xaOriginBucket) {
        this.xaOriginBucket = xaOriginBucket;
    }

    public String getRegional() {
        return regional;
    }

    public void setRegional(String regional) {
        this.regional = regional;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getXaExecutorUser() {
        return xaExecutorUser;
    }

    public void setXaExecutorUser(String xaExecutorUser) {
        this.xaExecutorUser = xaExecutorUser;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getXaPiAlarmType() {
        return xaPiAlarmType;
    }

    public void setXaPiAlarmType(String xaPiAlarmType) {
        this.xaPiAlarmType = xaPiAlarmType;
    }

    public String getXaPiFailType() {
        return xaPiFailType;
    }

    public void setXaPiFailType(String xaPiFailType) {
        this.xaPiFailType = xaPiFailType;
    }

    public String getXaPiCm() {
        return xaPiCm;
    }

    public void setXaPiCm(String xaPiCm) {
        this.xaPiCm = xaPiCm;
    }

    public String getXaPiEndId() {
        return xaPiEndId;
    }

    public void setXaPiEndId(String xaPiEndId) {
        this.xaPiEndId = xaPiEndId;
    }

    public String getXaPiNetworkElement() {
        return xaPiNetworkElement;
    }

    public void setXaPiNetworkElement(String xaPiNetworkElement) {
        this.xaPiNetworkElement = xaPiNetworkElement;
    }

    public String getXaPiNeType() {
        return xaPiNeType;
    }

    public void setXaPiNeType(String xaPiNeType) {
        this.xaPiNeType = xaPiNeType;
    }

    public String getXaPiNetwork() {
        return xaPiNetwork;
    }

    public void setXaPiNetwork(String xaPiNetwork) {
        this.xaPiNetwork = xaPiNetwork;
    }

    public String getXaPiNotdoneReason() {
        return xaPiNotdoneReason;
    }

    public void setXaPiNotdoneReason(String xaPiNotdoneReason) {
        this.xaPiNotdoneReason = xaPiNotdoneReason;
    }

    public String getXaPiOp() {
        return xaPiOp;
    }

    public void setXaPiOp(String xaPiOp) {
        this.xaPiOp = xaPiOp;
    }

    public String getXaPiOpeningNote() {
        return xaPiOpeningNote;
    }

    public void setXaPiOpeningNote(String xaPiOpeningNote) {
        this.xaPiOpeningNote = xaPiOpeningNote;
    }

    public String getXaPiPriority() {
        return xaPiPriority;
    }

    public void setXaPiPriority(String xaPiPriority) {
        this.xaPiPriority = xaPiPriority;
    }

    public String getXaPiResponsable() {
        return xaPiResponsable;
    }

    public void setXaPiResponsable(String xaPiResponsable) {
        this.xaPiResponsable = xaPiResponsable;
    }

    public String getXaPiSubArea() {
        return xaPiSubArea;
    }

    public void setXaPiSubArea(String xaPiSubArea) {
        this.xaPiSubArea = xaPiSubArea;
    }

    public String getXaPiSuspendReason() {
        return xaPiSuspendReason;
    }

    public void setXaPiSuspendReason(String xaPiSuspendReason) {
        this.xaPiSuspendReason = xaPiSuspendReason;
    }

    public String getXaPiTramReason() {
        return xaPiTramReason;
    }

    public void setXaPiTramReason(String xaPiTramReason) {
        this.xaPiTramReason = xaPiTramReason;
    }

    public String getXaPiTramSus() {
        return xaPiTramSus;
    }

    public void setXaPiTramSus(String xaPiTramSus) {
        this.xaPiTramSus = xaPiTramSus;
    }

    public String getXaPiCriterion() {
        return xaPiCriterion;
    }

    public void setXaPiCriterion(String xaPiCriterion) {
        this.xaPiCriterion = xaPiCriterion;
    }

    public LocalDateTime getXaPiGmgStart() {
        return xaPiGmgStart;
    }

    public void setXaPiGmgStart(LocalDateTime xaPiGmgStart) {
        this.xaPiGmgStart = xaPiGmgStart;
    }

    public LocalDateTime getXaPiGmgEnd() {
        return xaPiGmgEnd;
    }

    public void setXaPiGmgEnd(LocalDateTime xaPiGmgEnd) {
        this.xaPiGmgEnd = xaPiGmgEnd;
    }

    public String getXaPiGmgStatus() {
        return xaPiGmgStatus;
    }

    public void setXaPiGmgStatus(String xaPiGmgStatus) {
        this.xaPiGmgStatus = xaPiGmgStatus;
    }

    public String getXaPiGmgOwner() {
        return xaPiGmgOwner;
    }

    public void setXaPiGmgOwner(String xaPiGmgOwner) {
        this.xaPiGmgOwner = xaPiGmgOwner;
    }

    public String getXaPiGmgDesc() {
        return xaPiGmgDesc;
    }

    public void setXaPiGmgDesc(String xaPiGmgDesc) {
        this.xaPiGmgDesc = xaPiGmgDesc;
    }

    public String getXaDispPri() {
        return xaDispPri;
    }

    public void setXaDispPri(String xaDispPri) {
        this.xaDispPri = xaDispPri;
    }

    public String getXaDispPriFaixa() {
        return xaDispPriFaixa;
    }

    public void setXaDispPriFaixa(String xaDispPriFaixa) {
        this.xaDispPriFaixa = xaDispPriFaixa;
    }

    public String getWorkZone() {
        return workZone;
    }

    public void setWorkZone(String workZone) {
        this.workZone = workZone;
    }

    public String getXaWorkzoneKey() {
        return xaWorkzoneKey;
    }

    public void setXaWorkzoneKey(String xaWorkzoneKey) {
        this.xaWorkzoneKey = xaWorkzoneKey;
    }

    public LocalDateTime getXaFirstRoutingTime() {
        return xaFirstRoutingTime;
    }

    public void setXaFirstRoutingTime(LocalDateTime xaFirstRoutingTime) {
        this.xaFirstRoutingTime = xaFirstRoutingTime;
    }

    public LocalDate getaAutoRoutedToDate() {
        return aAutoRoutedToDate;
    }

    public void setaAutoRoutedToDate(LocalDate aAutoRoutedToDate) {
        this.aAutoRoutedToDate = aAutoRoutedToDate;
    }

    public String getXaPiSsGsbiClass() {
        return xaPiSsGsbiClass;
    }

    public void setXaPiSsGsbiClass(String xaPiSsGsbiClass) {
        this.xaPiSsGsbiClass = xaPiSsGsbiClass;
    }

    public String getXaPiSsGsbiClassName() {
        return xaPiSsGsbiClassName;
    }

    public void setXaPiSsGsbiClassName(String xaPiSsGsbiClassName) {
        this.xaPiSsGsbiClassName = xaPiSsGsbiClassName;
    }

    public String getXaPiSsEqpSegNet() {
        return xaPiSsEqpSegNet;
    }

    public void setXaPiSsEqpSegNet(String xaPiSsEqpSegNet) {
        this.xaPiSsEqpSegNet = xaPiSsEqpSegNet;
    }

    public String getXaPiSsEqpFunction() {
        return xaPiSsEqpFunction;
    }

    public void setXaPiSsEqpFunction(String xaPiSsEqpFunction) {
        this.xaPiSsEqpFunction = xaPiSsEqpFunction;
    }

    public String getXaPiIndustrialBuildings() {
        return xaPiIndustrialBuildings;
    }

    public void setXaPiIndustrialBuildings(String xaPiIndustrialBuildings) {
        this.xaPiIndustrialBuildings = xaPiIndustrialBuildings;
    }

    public String getXaPiNttUserRule() {
        return xaPiNttUserRule;
    }

    public void setXaPiNttUserRule(String xaPiNttUserRule) {
        this.xaPiNttUserRule = xaPiNttUserRule;
    }

    public String getActivityGroup() {
        return activityGroup;
    }

    public void setActivityGroup(String activityGroup) {
        this.activityGroup = activityGroup;
    }

    public String getXaPiIdTicketCa() {
        return xaPiIdTicketCa;
    }

    public void setXaPiIdTicketCa(String xaPiIdTicketCa) {
        this.xaPiIdTicketCa = xaPiIdTicketCa;
    }

    public String getXaPiCloseNote() {
        return xaPiCloseNote;
    }

    public void setXaPiCloseNote(String xaPiCloseNote) {
        this.xaPiCloseNote = xaPiCloseNote;
    }

    public String getXaPiFailCause() {
        return xaPiFailCause;
    }

    public void setXaPiFailCause(String xaPiFailCause) {
        this.xaPiFailCause = xaPiFailCause;
    }

    public String getXaPiFailSolution() {
        return xaPiFailSolution;
    }

    public void setXaPiFailSolution(String xaPiFailSolution) {
        this.xaPiFailSolution = xaPiFailSolution;
    }

    public LocalDate getCollectDate() {
        return collectDate;
    }

    public void setCollectDate(LocalDate collectDate) {
        this.collectDate = collectDate;
    }

}
