package tim.field.application.User.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import tim.field.application.User.dto.PasswordResetConfirmDTO;
import tim.field.application.User.dto.PasswordResetRequestDTO;
import tim.field.application.User.dto.RegisterUserDTO;
import tim.field.application.User.dto.TokenRequest;
import tim.field.application.User.dto.UserDetailsDTO;
import tim.field.application.User.dto.UserResponseDTO;
import tim.field.application.User.service.UserService;
import tim.field.application.security.JwtUtil;
import tim.field.application.security.tokensJWT.TokenBlacklistService;
import tim.field.application.util.GlobalResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint para registrar um novo usuário
    @PostMapping("/register")
    public ResponseEntity<GlobalResponse<Map<String, Object>>> registerUser(
            @Valid @RequestBody RegisterUserDTO registerUserDTO,
            HttpServletRequest request) {
        try {
            // Registro do novo usuário
            UserResponseDTO savedUser = userService.registerNewUser(registerUserDTO);
    
            // Configura o contexto do log
            setRequestAttributes(request, "AUTH", "User registered successfully",
                    HttpStatus.CREATED.value(), "{\"userId\":" + savedUser.getId() + "}");
    
            // Inclui o URL do QR Code na resposta
            Map<String, Object> data = Map.of(
                    "userId", savedUser.getId(),
                    "qrCodeUrl", savedUser.getQrCodeUrl() // Certifique-se de que a classe UserResponseDTO tenha esse campo
            );
    
            // Retorna a resposta com o QR Code
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(GlobalResponse.created("Usuário registrado com sucesso.", data));
        } catch (RuntimeException e) {
            LOGGER.warn("Registration error: {}", e.getMessage());
    
            try {
                String errorJson = new ObjectMapper().writeValueAsString(Map.of("error", e.getMessage()));
                setRequestAttributes(request, "AUTH", "Registration error",
                        HttpStatus.BAD_REQUEST.value(), errorJson);
            } catch (Exception jsonException) {
                LOGGER.error("Failed to serialize error message to JSON: {}", jsonException.getMessage());
            }
    
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            LOGGER.error("Unexpected error while registering user: ", e);
    
            try {
                String errorJson = new ObjectMapper().writeValueAsString(Map.of("error", e.getMessage()));
                setRequestAttributes(request, "AUTH", "Unexpected error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(), errorJson);
            } catch (Exception jsonException) {
                LOGGER.error("Failed to serialize error message to JSON: {}", jsonException.getMessage());
            }
    
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro inesperado ao registrar o usuário.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }    

    // Endpoint para confirmar e-mail
    @PostMapping("/confirm-email")
    public ResponseEntity<GlobalResponse<Object>> confirmEmail(
            @RequestBody TokenRequest tokenRequest, HttpServletRequest request) {
        try {
            String token = tokenRequest.getToken();
            boolean isConfirmed = userService.validateActivationToken(token, request);

            String logMessage = (String) request.getAttribute("logMessage");
            String apiMessage = (String) request.getAttribute("apiMessage");

            setRequestAttributes(request, "ACTIVATION", logMessage,
                    isConfirmed ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value(),
                    "{\"token\":\"" + token + "\"}");

            if (isConfirmed) {
                return ResponseEntity.ok(GlobalResponse.success(apiMessage, null));
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(GlobalResponse.error(apiMessage, HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            LOGGER.error("Error confirming email: ", e);
            setRequestAttributes(request, "ACTIVATION", "Error confirming email",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro ao confirmar o e-mail.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Endpoint para solicitar redefinição de senha
    @PostMapping("/request-password-reset")
    public ResponseEntity<GlobalResponse<Object>> requestPasswordReset(
            @RequestBody PasswordResetRequestDTO passwordResetRequest, HttpServletRequest request) {
        try {
            boolean isTokenSent = userService.resetPassword(passwordResetRequest.getEmail());

            setRequestAttributes(request, "AUTH", "Password reset email sent",
                    HttpStatus.OK.value(), "{\"email\":\"" + passwordResetRequest.getEmail() + "\"}");

            if (isTokenSent) {
                return ResponseEntity.ok(GlobalResponse.success(
                        "E-mail de redefinição enviado com sucesso.", null));
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(GlobalResponse.error("E-mail não encontrado.", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            LOGGER.error("Error requesting password reset: ", e);
            setRequestAttributes(request, "AUTH", "Error requesting password reset",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro ao solicitar redefinição de senha.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Endpoint para redefinição de senha
    @PostMapping("/reset-password")
    public ResponseEntity<GlobalResponse<Object>> resetPassword(
            @RequestBody PasswordResetConfirmDTO passwordResetConfirm, HttpServletRequest request) {
        try {
            // Verificar se o token ou a senha são nulos
            if (passwordResetConfirm.getTokenPassReset() == null) {
                setRequestAttributes(request, "AUTH", "Token is null", HttpStatus.BAD_REQUEST.value(), null);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(GlobalResponse.error("O token não pode ser nulo.", HttpStatus.BAD_REQUEST.value()));
            }
    
            if (passwordResetConfirm.getNewPassword() == null) {
                setRequestAttributes(request, "AUTH", "New password is null", HttpStatus.BAD_REQUEST.value(), null);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(GlobalResponse.error("A senha não pode ser nula.", HttpStatus.BAD_REQUEST.value()));
            }
    
            // Realizar a redefinição da senha
            boolean isPasswordReset = userService.confirmResetPassword(
                    passwordResetConfirm.getTokenPassReset(), passwordResetConfirm.getNewPassword());
    
            setRequestAttributes(request, "AUTH", "Password reset successfully",
                    HttpStatus.OK.value(), "{\"token\":\"" + passwordResetConfirm.getTokenPassReset() + "\"}");
    
            if (isPasswordReset) {
                return ResponseEntity.ok(GlobalResponse.success("Senha redefinida com sucesso.", null));
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(GlobalResponse.error("Token de redefinição inválido ou expirado.", HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            LOGGER.error("Error resetting password: ", e);
            setRequestAttributes(request, "AUTH", "Error resetting password",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");
    
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro ao redefinir senha.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }    

    /**
     * Verifica a validade do ACCESS_TOKEN.
     * Redireciona para a página de login se o token for inválido ou revogado.
     */
    @GetMapping("/validate-token")
    public ResponseEntity<GlobalResponse<Map<String, Object>>> validateToken(HttpServletRequest request) {
        try {
            // Obtém o token de acesso do cookie
            String accessToken = extractAccessTokenFromCookies(request);

            // Verifica se o token está ausente ou inválido
            if (accessToken == null || !jwtUtil.isTokenValid(accessToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(GlobalResponse.error("Token inválido ou ausente.", HttpStatus.UNAUTHORIZED.value()));
            }

            // Verifica se o token está na blacklist
            if (tokenBlacklistService.isBlacklisted(accessToken)) {
                LOGGER.warn("Tentativa de uso de token revogado: {}", accessToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(GlobalResponse.error("Token revogado.", HttpStatus.UNAUTHORIZED.value()));
            }

            // Extração do ID do usuário do token
            Long userId = jwtUtil.extractUserId(accessToken);

            // Busca os detalhes do usuário
            UserDetailsDTO userDetails = userService.getUserDetails(userId);

            // Adiciona informações ao retorno
            Map<String, Object> responseData = Map.of(
                    "userDetails", userDetails
            );

                        // Serializa o DTO em JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String userDetailsJson = "";
            try {
                userDetailsJson = objectMapper.writeValueAsString(userDetails);
            } catch (JsonProcessingException e) {
                LOGGER.error("Erro ao serializar UserDetailsDTO para JSON", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(GlobalResponse.error("Erro interno ao processar os dados do usuário.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
            }

            setRequestAttributes(request, "AUTH", "Validate access token",
            HttpStatus.OK.value(), "{\"DTO\":" + userDetailsJson + "}");
        
            return ResponseEntity.ok(GlobalResponse.success("Token válido.", responseData));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Erro na requisição: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error("Requisição inválida.", HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            LOGGER.error("Erro ao validar o token: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro ao validar o token.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
    
    // Método para extrair o token do cookie
    private String extractAccessTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
    
        for (Cookie cookie : cookies) {
            if ("ACCESS_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }     

    // Método auxiliar para configurar atributos de log
    private void setRequestAttributes(HttpServletRequest request, String logType, String event, int status, String payload) {
        request.setAttribute("logType", logType);
        request.setAttribute("event", event);
        request.setAttribute("responseStatus", status);
        request.setAttribute("payload", payload);
    }
}
