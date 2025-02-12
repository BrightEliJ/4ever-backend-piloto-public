package tim.field.application.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tim.field.application.User.model.User;
import tim.field.application.User.service.UserService;
import tim.field.application.logging.service.LogService;
import tim.field.application.security.tokensJWT.TokenBlacklistService;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLogoutHandler.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final LogService logService;
    private final TokenBlacklistService tokenBlacklistService;

    public CustomLogoutHandler(JwtUtil jwtUtil, UserService userService, LogService logService, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.logService = logService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = getTokenFromCookies(request);
    
        if (token != null) {
            tokenBlacklistService.addToBlacklist(token);

            try {
                String username = jwtUtil.extractUsername(token);
                User user = userService.findByUsername(username);
    
                String clientIp = getClientIp(request);
                String userAgent = request.getHeader("User-Agent");
    
                // Adicione logs para depuração
                LOGGER.debug("Attempting to log out user: {}", username);
    
                logService.logEvent(
                    user,
                    "ACCESS",
                    "Logout successful",
                    request.getRequestURI(),
                    request.getMethod(),
                    (short) HttpServletResponse.SC_OK,
                    clientIp,
                    userAgent,
                    null,
                    null
                );
    
                LOGGER.info("Logout log registered successfully for user [{}]", username);
            } catch (Exception e) {
                LOGGER.error("Failed to process logout due to token issue: {}", e.getMessage(), e);
            }
        } else {
            LOGGER.warn("Logout attempted with no token provided.");
        }
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
