package tim.field.application.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tim.field.application.util.GlobalResponse;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn("Erro de validação detectado.");

        // Monta os erros com campo -> mensagem
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GlobalResponse.error("Erro de validação.", HttpStatus.BAD_REQUEST.value(), errors));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GlobalResponse<Object>> handleRuntimeException(RuntimeException ex) {
        logger.error("Erro de aplicação detectado: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GlobalResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse<Object>> handleGeneralException(Exception ex) {
        logger.error("Erro inesperado: ", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalResponse.error("Erro interno do servidor.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    public ResponseEntity<GlobalResponse<?>> unauthorizedResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GlobalResponse.error(message, HttpStatus.UNAUTHORIZED.value()));
    }
    
}