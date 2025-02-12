package tim.field.application.logging.filter;

import java.io.IOException;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tim.field.application.User.model.User;
import tim.field.application.User.service.UserService;
import tim.field.application.logging.service.LogService;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    private final LogService logService;
    private final UserService userService;

    public LoggingFilter(LogService logService, UserService userService) {
        this.logService = logService;
        this.userService = userService;
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long startTime = System.currentTimeMillis();

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Excluir rotas específicas do logging
        if (shouldExcludeLogging(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        // Evitar logging duplicado
        if (httpRequest.getAttribute("LOGGED") != null) {
            chain.doFilter(request, response);
            return;
        }

        // Marcar requisição como já logada e capturar tempo de início
        httpRequest.setAttribute("LOGGED", true);
        httpRequest.setAttribute("startTime", startTime);

        try {
            chain.doFilter(request, response);
        } finally {
            // Realizar o log do evento
            logEvent(httpRequest, httpResponse);
        }
    }

    /**
     * Determina se uma rota deve ser excluída do logging.
     */
    private boolean shouldExcludeLogging(String uri) {
        return uri.startsWith("/api/auth/login") || uri.startsWith("/api/auth/logout");
    }

    /**
     * Loga o evento de uma requisição HTTP.
     */
    private void logEvent(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            // Verificar se o logging está habilitado para esta requisição
            Boolean shouldLog = (Boolean) httpRequest.getAttribute("SHOULD_LOG");
            if (shouldLog != null && !shouldLog) {
                logger.debug("Skipping log for request to {}", httpRequest.getRequestURI());
                return;
            }
    
            long startTime = (long) httpRequest.getAttribute("startTime");
            long executionTime = System.currentTimeMillis() - startTime;
    
            String logType = (String) httpRequest.getAttribute("logType");
            String event = (String) httpRequest.getAttribute("event");
            Integer responseStatus = (Integer) httpRequest.getAttribute("responseStatus");
            String payload = (String) httpRequest.getAttribute("payload");
    
            // Recupera o ID do usuário da requisição (se presente)
            Long userId = (Long) httpRequest.getAttribute("userId");
    
            if (logType == null) logType = "REQUEST";
            if (event == null) event = "API Request";
    
            User user = userId != null ? userService.findById(userId) : getCurrentUser();
    
            logService.logEvent(
                    user,
                    logType,
                    event,
                    httpRequest.getRequestURI(),
                    httpRequest.getMethod(),
                    responseStatus != null ? responseStatus.shortValue() : (short) httpResponse.getStatus(),
                    getClientIp(httpRequest),
                    httpRequest.getHeader("User-Agent"),
                    BigDecimal.valueOf(executionTime),
                    payload
            );
    
            logger.info("Logged event [{}] for URI [{}] with status [{}] in [{} ms] for user [{}] from IP [{}]",
                    event, httpRequest.getRequestURI(), httpResponse.getStatus(), executionTime,
                    user != null ? user.getUsername() : "anonymous", getClientIp(httpRequest));
        } catch (Exception e) {
            logger.error("Error logging event: {}", e.getMessage());
        }
    }    

    /**
     * Obtém o usuário autenticado atual.
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                return userService.findByUsername(username);
            } else if (principal instanceof String) {
                String username = (String) principal;
                return userService.findByUsername(username);
            }
        }
        return null;
    }

    /**
     * Obtém o IP do cliente, considerando cabeçalhos de proxy.
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}