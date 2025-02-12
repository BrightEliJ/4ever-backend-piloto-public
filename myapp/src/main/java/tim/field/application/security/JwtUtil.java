package tim.field.application.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import tim.field.application.User.model.GroupPermission;
import tim.field.application.User.model.Permission;
import tim.field.application.User.model.User;
import tim.field.application.User.model.UserPermission;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tim.field.application.exception.UnauthorizedException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey secretKey;
    private final long ACCESS_TOKEN_EXPIRATION = 86400000; // 24 horas

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Gera um token de acesso com atributos adicionais para controle de autenticação.
     *
     * @param username               Nome do usuário.
     * @param roles                  Permissões do usuário.
     * @param twoFactorAuthenticated Estado de autenticação 2FA.
     * @param last2FAAuth            Data da última autenticação 2FA.
     * @return Token JWT gerado.
     */
    public String generateAccessToken(String username, Long userId, Set<String> roles, boolean twoFactorAuthenticated, Date last2FAAuth) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId) // Adiciona o userId ao token
                .claim("roles", roles)
                .claim("twoFactorAuthenticated", twoFactorAuthenticated)
                .claim("last2FAAuth", last2FAAuth)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        try {
            logger.info("Extraindo todas as claims do token.");
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("Token expirado: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            logger.error("Erro ao analisar token JWT: {}", e.getMessage());
            throw new IllegalArgumentException("Token inválido ou malformado.", e);
        }
    }

    /**
     * Extrai o ID do usuário das claims contidas no token JWT.
     *
     * @param token Token JWT a ser analisado.
     * @return ID do usuário como Long.
     * @throws IllegalArgumentException se o token for inválido ou o ID do usuário não estiver presente.
     */
    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            logger.error("Erro ao extrair userId do token JWT: {}", e.getMessage());
            throw new IllegalArgumentException("Não foi possível extrair o userId do token.", e);
        }
    }

    /**
     * Verifica se o token JWT está no contexto de segurança do Spring Security e é válido.
     *
     * @param token O token JWT a ser validado.
     * @return True se o token estiver no contexto de segurança e for válido, caso contrário False.
     */
    public boolean isTokenValid(String token) {
        try {
            // Obtém a autenticação do contexto de segurança
            var authentication = SecurityContextHolder.getContext().getAuthentication();
    
            if (authentication == null) {
                logger.warn("Nenhuma autenticação encontrada no contexto de segurança.");
                return false;
            }
    
            // Verifica se as credenciais no contexto são válidas e se correspondem ao token fornecido
            if (authentication.getCredentials() instanceof String) {
                String tokenInContext = (String) authentication.getCredentials();
    
                if (token.equals(tokenInContext)) {
                    logger.info("Token JWT está no contexto de segurança e é válido.");
                    return true;
                } else {
                    logger.warn("Token JWT fornecido não corresponde ao token no contexto.");
                    return false;
                }
            } else {
                logger.warn("As credenciais no contexto de segurança não são do tipo esperado.");
                return false;
            }
        } catch (Exception e) {
            logger.error("Erro ao validar o token JWT no contexto de segurança: {}", e.getMessage());
            return false;
        }
    }    

    /**
     * Atualiza o token com uma nova data de autenticação 2FA.
     *
     * @param token                  Token JWT atual.
     * @param twoFactorAuthenticated Novo estado de autenticação 2FA.
     * @param last2FAAuth            Nova data de autenticação 2FA.
     * @return Token JWT atualizado.
     */
    public String updateTokenWithTwoFactor(Long id, String token, boolean twoFactorAuthenticated, Date last2FAAuth) {
        Claims claims = extractAllClaims(token);

        String username = claims.getSubject();
        Set<String> roles = ((List<?>) claims.get("roles")).stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

        return generateAccessToken(username, id, roles, twoFactorAuthenticated, last2FAAuth);
    }

    public boolean isTwoFactorAuthenticated(String token) {
        Claims claims = extractAllClaims(token);
        return Boolean.TRUE.equals(claims.get("twoFactorAuthenticated"));
    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().before(new Date());
    }

    /**
     * Verifica se o tempo da última autenticação 2FA excede o limite permitido.
     *
     * @param token            Token JWT.
     * @param twoFactorTimeout Tempo máximo permitido para a autenticação 2FA em milissegundos.
     * @return True se a autenticação 2FA precisar ser renovada.
     */
    public boolean isTwoFactorAuthExpired(String token, long twoFactorTimeout) {
        Claims claims = extractAllClaims(token);
        Date last2FAAuth = claims.get("last2FAAuth", Date.class);
        return last2FAAuth == null || (System.currentTimeMillis() - last2FAAuth.getTime()) > twoFactorTimeout;
    }

    public Set<String> extractRoles(User user) {
        Set<String> userRoles = user.getUserPermissions().stream()
                .map(UserPermission::getPermission)
                .map(Permission::getName)
                .collect(Collectors.toSet());

        Set<String> groupRoles = user.getUserGroups().stream()
                .flatMap(userGroup -> userGroup.getGroup().getGroupPermissions().stream())
                .map(GroupPermission::getPermission)
                .map(Permission::getName)
                .collect(Collectors.toSet());

        userRoles.addAll(groupRoles);
        return userRoles;
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        List<String> roles = claims.get("roles", List.class);
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public String extractTokenFromContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String) {
            return (String) authentication.getCredentials();
        }
        throw new IllegalStateException("Token JWT não encontrado no contexto de segurança.");
    }

    public Long extractAndValidateUserId(HttpServletRequest request) {
    String accessToken = extractAccessTokenFromCookies(request);
    if (accessToken == null || !isTokenValid(accessToken)) {
        throw new UnauthorizedException("Token inválido ou ausente.");
    }
        return extractUserId(accessToken);
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
}
