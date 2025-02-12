package tim.field.application.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ignora campos nulos na resposta
public class GlobalResponse<T> {

    private String status;     // success ou error
    private int code;          // Código HTTP
    private String message;    // Mensagem principal
    private T data;            // Dados de sucesso
    private Map<String, String> errors; // Detalhes dos erros (campo -> mensagem)

    public GlobalResponse(String status, int code, String message, T data, Map<String, String> errors) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao serializar GlobalResponse para JSON", e);
        }
    }

    // Sucesso
    public static <T> GlobalResponse<T> success(String message, T data) {
        return new GlobalResponse<>("success", 200, message, data, null);
    }

    public static <T> GlobalResponse<T> created(String message, T data) {
        return new GlobalResponse<>("success", 201, message, data, null);
    }

    // Erro
    public static <T> GlobalResponse<T> error(String message, int code, Map<String, String> errors) {
        return new GlobalResponse<>("error", code, message, null, errors);
    }

    public static <T> GlobalResponse<T> error(String message, int code) {
        return new GlobalResponse<>("error", code, message, null, null);
    }

    // Getters e Setters
    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
