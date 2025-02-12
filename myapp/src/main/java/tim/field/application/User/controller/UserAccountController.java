package tim.field.application.User.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Import da exceção
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import tim.field.application.User.dto.UserDetailsDTO;
import tim.field.application.User.service.UserService;
import tim.field.application.exception.UnauthorizedException;
import tim.field.application.security.JwtUtil;
import tim.field.application.util.GlobalResponse;

@RestController
@RequestMapping("/api/user-account")
public class UserAccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/update-user")
    public ResponseEntity<GlobalResponse<Map<String, Object>>> updateUser(
            @Valid @RequestBody UserDetailsDTO userDetailsDTO,
            HttpServletRequest request) {
    
        try {
            // Validação do token e extração do ID do usuário
            Long userId = jwtUtil.extractAndValidateUserId(request);
    
            // Verifica se o usuário do token corresponde ao usuário da requisição
            if (!userId.equals(userDetailsDTO.getUserId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(GlobalResponse.error("Usuário não correspondente ao token de acesso.", HttpStatus.UNAUTHORIZED.value()));
            }
    
            // Atualizar dados do usuário
            userService.updateUser(userDetailsDTO);
    
            // Serializa o DTO em JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String userDetailsJson = "";
            try {
                userDetailsJson = objectMapper.writeValueAsString(userDetailsDTO);
            } catch (JsonProcessingException e) {
                LOGGER.error("Erro ao serializar UserDetailsDTO para JSON", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(GlobalResponse.error("Erro interno ao processar os dados do usuário.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
            }

        // Configura atributos da requisição para o log/auditoria
        setRequestAttributes(request, "UPDATE", "Update data",
                HttpStatus.OK.value(), "{\"DTO\":" + userDetailsJson + "}");
    
            // Retorna a resposta de sucesso
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(GlobalResponse.success("Usuário alterado com sucesso.", null));
        } catch (UnauthorizedException e) {
            LOGGER.warn("Acesso não autorizado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalResponse.error("Token inválido ou ausente.", HttpStatus.UNAUTHORIZED.value()));
        } catch (RuntimeException e) {
            LOGGER.error("Erro inesperado ao atualizar usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro interno ao atualizar usuário.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }    

    // Método auxiliar para configurar atributos de log
    private void setRequestAttributes(HttpServletRequest request, String logType, String event, int status, String payload) {
        request.setAttribute("logType", logType);
        request.setAttribute("event", event);
        request.setAttribute("responseStatus", status);
        request.setAttribute("payload", payload);
    }
}