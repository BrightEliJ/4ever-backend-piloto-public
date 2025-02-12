package tim.field.application.security;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import tim.field.application.User.model.User;
import tim.field.application.User.repository.UserRepository;
import tim.field.application.User.service.CustomUserDetailsService;
import tim.field.application.User.service.UserService;
import tim.field.application.logging.service.LogService;
import tim.field.application.util.GlobalResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final LogService logService;

    @Autowired
    private final UserService userService;

    @Autowired
    public JwtAuthorizationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService, LogService logService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.logService = logService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String token = extractJwtTokenFromCookies(request);
    
        if (token == null) {
            handleMissingToken(request, response, filterChain, startTime);
            return;
        }
    
        try {
            // Verifica se o endpoint é permitido sem autenticação completa
            if (isEndpointPermitted(request)) {
                logger.info("Endpoint permitido sem autenticação 2FA: {}", request.getRequestURI());
                
                // Verifica e configura o contexto, se possível
                String username = jwtUtil.extractUsername(token);
                if (username != null && !jwtUtil.isTokenExpired(token)) {
                    setSecurityContext(username, token, jwtUtil.extractAuthorities(token));
                }
                
                filterChain.doFilter(request, response);
                return;
            }
    
            // Processa token normalmente para outros endpoints
            processToken(token, request, response, filterChain, startTime);
        } catch (ExpiredJwtException e) {
            handleExpiredToken(e, response, startTime, request);
        } catch (Exception e) {
            handleProcessingError(e, response, startTime, request);
        }
    }     

    private void processToken(String token, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, long startTime)
    throws IOException, ServletException {
        if (jwtUtil.isTokenExpired(token)) {
            logger.warn("Token JWT expirado.");
            writeErrorResponse(response, "Token expirado. Faça login novamente.", HttpStatus.UNAUTHORIZED);
            return;
        }

        String username = jwtUtil.extractUsername(token);
        if (username == null) {
            logger.warn("Username não encontrado no token JWT.");
            writeErrorResponse(response, "Token inválido.", HttpStatus.UNAUTHORIZED);
            return;
        }

        boolean twoFactorAuthenticated = jwtUtil.isTwoFactorAuthenticated(token);
        if (!twoFactorAuthenticated) {
            logger.warn("Autenticação 2FA não concluída. URI: {}", request.getRequestURI());
            writeErrorResponse(response, "Two-factor authentication required.", HttpStatus.FORBIDDEN);
            return;
        }

        // Validação de expiração do 2FA somente para endpoints protegidos
        if (!isEndpointPermitted(request) && jwtUtil.isTwoFactorAuthExpired(token, 3600 * 1000)) {
            logger.warn("Autenticação 2FA expirada.");
            writeErrorResponse(response, "Two-factor authentication expired.", HttpStatus.FORBIDDEN);
            return;
        }

        setSecurityContext(username, token, jwtUtil.extractAuthorities(token));
        filterChain.doFilter(request, response);
    }

    private void handleMissingToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, long startTime)
            throws IOException, ServletException {
        if (isEndpointPermitted(request)) {
            logger.info("Endpoint '{}' permitido sem token.", request.getRequestURI());
            filterChain.doFilter(request, response);
        } else {
            logger.warn("Token ausente.");
            writeErrorResponse(response, "Token JWT ausente.", HttpStatus.UNAUTHORIZED);
        }
    }

    private void handleExpiredToken(ExpiredJwtException e, HttpServletResponse response, long startTime, HttpServletRequest request)
            throws IOException {
        logger.error("Token expirado: {}", e.getMessage());
        writeErrorResponse(response, "Token expirado. Faça login novamente.", HttpStatus.UNAUTHORIZED);
    }

    private void handleProcessingError(Exception e, HttpServletResponse response, long startTime, HttpServletRequest request)
            throws IOException {
        logger.error("Erro ao processar token: {}", e.getMessage());
        writeErrorResponse(response, "Erro ao processar token.", HttpStatus.BAD_REQUEST);
    }

    private String extractJwtTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void writeErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(status.value());
        response.getWriter().write(GlobalResponse.error(message, status.value()).toJson());
    }

    private void setSecurityContext(String username, String token, List<GrantedAuthority> authorities) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, token, authorities)
        );
    }

    private boolean isEndpointPermitted(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/2fa/verify") || 
               uri.startsWith("/api/auth/confirm-email") || 
               uri.startsWith("/api/auth/register" ) || 
               uri.startsWith("/api/auth/request-password-reset" ) || 
               uri.startsWith("/api/auth/reset-password" );
    }
}