package tim.field.application.logging.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tim.field.application.User.model.User;
import tim.field.application.logging.model.Log;
import tim.field.application.logging.repository.LogRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public void logEvent(User user, String logType, String event, String endpoint, String httpMethod, Short responseStatus,
                         String clientIp, String userAgent, BigDecimal executionTime, String payload) {
        Log log = new Log();
        log.setUser(user);
        log.setLogType(logType);
        log.setEvent(event);
        log.setEndpoint(endpoint);
        log.setHttpMethod(httpMethod);
        log.setResponseStatus(responseStatus);
        log.setClientIp(clientIp);
        log.setUserAgent(userAgent);
        log.setExecutionTime(executionTime);
        log.setTimestamp(LocalDateTime.now());
        log.setPayload(payload);

        logRepository.save(log);
    }
}
