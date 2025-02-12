// src/main/java/tim/field/application/util/ResponseUtil.java
package tim.field.application.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> success(String message, Object data) {
        return createResponse("success", 200, message, data);
    }

    public static Map<String, Object> error(String message, int code) {
        return createResponse("error", code, message, null);
    }

    public static Map<String, Object> warning(String message, Object data) {
        return createResponse("warning", 200, message, data);
    }

    private static Map<String, Object> createResponse(String status, int code, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("code", code);
        response.put("message", message);
        response.put("data", data != null ? data : new HashMap<>());
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        return response;
    }

    public static String toJson(Map<String, Object> response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return "{\"status\":\"error\",\"message\":\"Erro ao serializar resposta.\"}";
        }
    }
}
