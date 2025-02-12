package tim.field.application.security;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tim.field.application.User.dto.LoginRequestDTO;
import tim.field.application.User.model.User;
import tim.field.application.User.service.CustomUserDetailsService;
import tim.field.application.User.service.UserService;
import tim.field.application.config.CachedBodyHttpServletRequest;
import tim.field.application.logging.service.LogService;
import tim.field.application.util.ResponseUtil;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    public JwtAuthenticationFilter(CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        long startTime = System.currentTimeMillis();
        String username = null;

        try {
            logger.info("Iniciando processo de autenticação.");
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
            String requestBody = cachedRequest.getBody();

            if (requestBody == null || requestBody.trim().isEmpty()) {
                logEvent(null, "ACCESS", "Corpo da requisição vazio", request, startTime, HttpServletResponse.SC_BAD_REQUEST, null);
                writeErrorResponse(response, "O corpo da requisição está vazio", HttpStatus.BAD_REQUEST);
                return null;
            }

            LoginRequestDTO loginRequest = objectMapper.readValue(requestBody, LoginRequestDTO.class);
            username = loginRequest.getUsername();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            Authentication authResult = this.getAuthenticationManager().authenticate(authToken);
            User user = userService.findByUsername(username);

            if (user == null) {
                logEvent(null, "ACCESS", "Usuário não encontrado após autenticação", request, startTime, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
                writeErrorResponse(response, "Usuário não encontrado após a autenticação.", HttpStatus.INTERNAL_SERVER_ERROR);
                return null;
            }

            // 🚨 Verifica se a conta está bloqueada antes de autenticar
            if ("bloqueado".equalsIgnoreCase(user.getStatus())) {
                logger.warn("Tentativa de login para usuário bloqueado: {}", username);
                logEvent(user, "ACCESS", "Usuário bloqueado tentou login", request, startTime, 423, null);
                response.setStatus(423);
                writeErrorResponse(response, "Usuário bloqueado. Entre em contato com o suporte.", HttpStatus.LOCKED);
                return null;
            }            

            if ("inativo".equalsIgnoreCase(user.getStatus())) {
                logger.warn("Usuário '{}' está inativo, mas permitido para login.", username);
                Set<String> roles = authResult.getAuthorities()
                        .stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.toSet());

                        Long id = user.getId();

                addAccessTokenToCookie(response, username, id, roles, false);
                logEvent(user, "ACCESS", "Login de usuário inativo permitido", request, startTime, HttpServletResponse.SC_OK, "{\"message\":\"Usuário inativo permitido\"}");
                writeSuccessResponse(response, "Login realizado com sucesso. Usuário inativo.", Map.of("isActiveUser", false));
                return null;
            }

            SecurityContextHolder.getContext().setAuthentication(authResult);
            return authResult;

        } catch (AuthenticationException e) {
            handleAuthenticationException(request, response, startTime, username, e);
            return null;
        } catch (IOException e) {
            logger.error("Erro ao ler o corpo da requisição: {}", e.getMessage());
            logEvent(null, "ACCESS", "Erro ao ler corpo da requisição", request, startTime, HttpServletResponse.SC_BAD_REQUEST, null);
            try {
                writeErrorResponse(response, "Formato de requisição inválido.", HttpStatus.BAD_REQUEST);
            } catch (IOException ioException) {
                logger.error("Erro ao escrever resposta: {}", ioException.getMessage());
            }
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        String username = userDetails.getUsername();

        userService.resetLoginAttempts(username);
        User user = userService.findByUsername(username);

        if (user == null) {
            logEvent(null, "ACCESS", "Usuário não encontrado após autenticação", request, startTime, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
            writeErrorResponse(response, "Usuário não encontrado.", HttpStatus.INTERNAL_SERVER_ERROR);
            return;
        }

        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toSet());

        Long id = user.getId();

        addAccessTokenToCookie(response, username, id, roles, false);
        logEvent(user, "ACCESS", "Login successful", request, startTime, HttpServletResponse.SC_OK, "{\"roles\":\"" + roles + "\",\"message\":\"Login bem-sucedido\"}");
        writeSuccessResponse(response, "Login realizado com sucesso. Continue com a autenticação 2FA.", null);
    }

    private void addAccessTokenToCookie(HttpServletResponse response, String username, Long id, Set<String> roles, boolean twoFactorAuthenticated) {
        String accessToken = jwtUtil.generateAccessToken(username, id, roles, twoFactorAuthenticated, null);
    
        String cookieValue = "ACCESS_TOKEN=" + accessToken
                + "; Path=/"
                + "; HttpOnly"
                + "; SameSite=None"
                + "; Secure"
                + "; Max-Age=86400";
    
        response.addHeader("Set-Cookie", cookieValue);
    }

    private void handleAuthenticationException(HttpServletRequest request, HttpServletResponse response, long startTime, String username, AuthenticationException e) {
        try {
            User user = username != null ? userService.findByUsername(username) : null;

            Map<String, Object> blockObject = blockUser(username);

            Boolean activeValueBlock = (Boolean) blockObject.get("Active");
            String typeValueBlock = (String) blockObject.get("Type");

            if (activeValueBlock != null && !activeValueBlock && "adminBlock".equals(typeValueBlock)) {
                writeErrorResponse(response, "Usuário bloqueado pelo admin.", HttpStatus.LOCKED);
                logEvent(user, "ACCESS", "Blocked user by admin", request, startTime, 423, null);
                return;
            } else if (activeValueBlock != null && !activeValueBlock && "attempBlock".equals(typeValueBlock)) {
                    writeErrorResponse(response, "Usuário bloqueado após múltiplas tentativas de login.", HttpStatus.LOCKED);
                    logEvent(user, "ACCESS", "Blocked by multiple attemps", request, startTime, 423, null);
                return;
            } else if (activeValueBlock != null && activeValueBlock) {
                userService.incrementLoginAttempts(username);
            }

            logEvent(user, "ACCESS", "Falha na autenticação", request, startTime, HttpServletResponse.SC_UNAUTHORIZED, null);
            writeErrorResponse(response, "Falha na autenticação: Nome de usuário ou senha inválidos.", HttpStatus.UNAUTHORIZED);
        } catch (IOException ioException) {
            logger.error("Erro ao escrever resposta: {}", ioException.getMessage());
        }
    }

    private void writeErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(status.value());
        response.getWriter().write(ResponseUtil.toJson(ResponseUtil.error(message, status.value())));
    }

    private void writeSuccessResponse(HttpServletResponse response, String message, Object data) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(ResponseUtil.toJson(ResponseUtil.success(message, data)));
    }

    private void logEvent(User user, String logType, String event, HttpServletRequest request, long startTime, int status, String payload) {
        logService.logEvent(
                user,
                logType,
                event,
                request.getRequestURI(),
                request.getMethod(),
                (short) status,
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                BigDecimal.valueOf(System.currentTimeMillis() - startTime),
                payload
        );
    }

    private Map<String, Object> blockUser(String username) {
        User user = userService.findByUsername(username);
        Map<String, Object> map = new HashMap<>();
    
        if (user.getLoginAttempts() >= 3) {

            userService.blockUser(username);

            map.put("Active", false);
            map.put("Type", "attempBlock");
    
            return map;

        } else if ((user.getLoginAttempts() < 3) && "bloqueado".equals(user.getStatus())) {

            map.put("Active", false);
            map.put("Type", "adminBlock");
    
            return map;  

        }

        map.put("Active", true);

        return map;
    }

}
