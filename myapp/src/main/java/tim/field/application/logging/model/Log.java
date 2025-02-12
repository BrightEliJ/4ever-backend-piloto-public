package tim.field.application.logging.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import tim.field.application.User.model.User;

@Entity
@Table(name = "logs", schema = "public")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "log_type", nullable = false, length = 50)
    private String logType; // e.g., ACCESS, REQUEST

    @Column(name = "event", length = 255)
    private String event; // Description of the log event

    @Column(name = "endpoint", length = 500)
    private String endpoint; // Accessed endpoint

    @Column(name = "http_method", length = 10)
    private String httpMethod; // GET, POST, etc.

    @Column(name = "response_status")
    private Short responseStatus; // HTTP status code

    @Column(name = "client_ip", length = 45)
    private String clientIp; // IPv4 or IPv6 address

    @Column(name = "user_agent", length = 500)
    private String userAgent; // User-agent string

    @Column(name = "execution_time", precision = 10, scale = 3)
    private BigDecimal executionTime; // Execution time in milliseconds

    @Column(name = "payload", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload; // JSON payload for auditing (optional)

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Short getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Short responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public BigDecimal getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(BigDecimal executionTime) {
        this.executionTime = executionTime;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}