package tim.field.application.User.controller;

import tim.field.application.User.model.User;
import tim.field.application.User.repository.UserRepository;
import tim.field.application.User.service.TwoFactorService;
import tim.field.application.User.service.UserService;
import tim.field.application.security.JwtUtil;
import tim.field.application.util.GlobalResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {

    private static final Logger logger = LoggerFactory.getLogger(TwoFactorController.class);

    private final TwoFactorService twoFactorService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;

    public TwoFactorController(TwoFactorService twoFactorService, JwtUtil jwtUtil, UserService userService, UserRepository userRepository) {
        this.twoFactorService = twoFactorService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/verify")
    public ResponseEntity<GlobalResponse<Void>> verify2FA(
            @CookieValue(name = "ACCESS_TOKEN", required = false) String accessToken,
            @RequestParam String token, // Alterado para String para validação de não-numérico
            HttpServletRequest request,
            HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
    
        logger.debug("Início da verificação 2FA com token recebido: {}", token);
    
        // Verificar se o token contém apenas números
        if (!token.matches("\\d+")) {
            setRequestAttributes(request, "AUTH", "Invalid token format", HttpStatus.BAD_REQUEST.value(), null);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error("O token deve conter apenas números.", HttpStatus.BAD_REQUEST.value()));
        }
    
        // Converte o token para int após validação
        int numericToken;
        try {
            numericToken = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            setRequestAttributes(request, "AUTH", "Token parsing error", HttpStatus.BAD_REQUEST.value(), null);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error("Erro ao processar o token.", HttpStatus.BAD_REQUEST.value()));
        }
    
        if (accessToken == null || accessToken.isEmpty()) {
            setRequestAttributes(request, "AUTH", "Access token missing", HttpStatus.UNAUTHORIZED.value(), null);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalResponse.error("Token de acesso ausente.", HttpStatus.UNAUTHORIZED.value()));
        }
    
        String username = jwtUtil.extractUsername(accessToken);
        if (username == null || jwtUtil.isTokenExpired(accessToken)) {
            setRequestAttributes(request, "AUTH", "Invalid or expired token", HttpStatus.UNAUTHORIZED.value(), null);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalResponse.error("Token inválido ou expirado.", HttpStatus.UNAUTHORIZED.value()));
        }
    
        boolean isValid2FAToken = twoFactorService.validateToken(username, numericToken);
        if (isValid2FAToken) {
            Optional<User> userOptional = userRepository.findByUsernameWithPermissions(username);
            if (userOptional.isEmpty()) {
                setRequestAttributes(request, "AUTH", "User not found", HttpStatus.NOT_FOUND.value(), null);
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(GlobalResponse.error("Usuário não encontrado.", HttpStatus.NOT_FOUND.value()));
            }
    
            User user = userOptional.get();
    
            Long id = user.getId();
    
            // Atualiza o token usando o método updateTokenWithTwoFactor
            String updatedToken = jwtUtil.updateTokenWithTwoFactor(id, accessToken, true, new Date());
    
            // Adiciona o token ao cookie
            addAccessTokenToCookie(response, updatedToken);
    
            // Atualiza o contexto de segurança
            List<GrantedAuthority> authorities = jwtUtil.extractAuthorities(updatedToken);
            UsernamePasswordAuthenticationToken updatedAuthentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(updatedAuthentication);
    
            setRequestAttributes(request, "AUTH", "2FA verified", HttpStatus.OK.value(), "{\"message\":\"2FA successful\"}");
            logger.info("Contexto de segurança atualizado e cookie ACCESS_TOKEN configurado para '{}'.", username);
    
            return ResponseEntity
                    .ok()
                    .body(GlobalResponse.success("Autenticação 2FA bem-sucedida.", null));
        } else {
            setRequestAttributes(request, "AUTH", "Invalid 2FA token", HttpStatus.BAD_REQUEST.value(), "{\"token\":\"" + token + "\"}");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error("Token 2FA inválido.", HttpStatus.BAD_REQUEST.value()));
        }
    }    
    
    private void addAccessTokenToCookie(HttpServletResponse response, String updatedToken) {
        String cookieValue = "ACCESS_TOKEN=" + updatedToken
                + "; Path=/"
                + "; HttpOnly"
                + "; SameSite=None"
                + "; Secure"
                + "; Max-Age=86400";
    
        response.addHeader("Set-Cookie", cookieValue);
    }    

    private void setRequestAttributes(HttpServletRequest request, String logType, String event, int status, String payload) {
        request.setAttribute("logType", logType);
        request.setAttribute("event", event);
        request.setAttribute("responseStatus", status);
        request.setAttribute("payload", payload);
    }
}