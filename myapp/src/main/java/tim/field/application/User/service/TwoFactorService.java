package tim.field.application.User.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import tim.field.application.User.model.User;
import tim.field.application.User.repository.UserRepository;
import tim.field.application.security.JwtUtil;

@Service
public class TwoFactorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwoFactorService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    /**
     * Configura o 2FA para um usuário e salva o secret e QR Code.
     *
     * @param user Usuário para o qual o 2FA será configurado.
     */
    public void setupTwoFactorForUser(User user) {
        LOGGER.debug("Iniciando configuração do 2FA para usuário: {}", user.getUsername());
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secret = key.getKey();

        String issuer = "Field";
        String qrCodeData = String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                issuer,
                user.getUsername(),
                secret,
                issuer
        );

        try {
            String qrCodeUrl = String.format(
                    "https://api.qrserver.com/v1/create-qr-code/?data=%s",
                    URLEncoder.encode(qrCodeData, "UTF-8")
            );

            user.setTwoFactorSecret(secret);
            user.setQrCodeUrl(qrCodeUrl);

            userRepository.save(user);
            LOGGER.info("2FA configurado com sucesso para o usuário '{}'. Secret e QR Code salvos.", user.getUsername());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Erro ao gerar URL do QR Code para 2FA do usuário '{}': {}", user.getUsername(), e.getMessage());
            throw new RuntimeException("Erro ao configurar o 2FA para o usuário.");
        }
    }

    /**
     * Valida o token TOTP gerado pelo app 2FA.
     *
     * @param username Nome do usuário.
     * @param token    Token gerado pelo aplicativo 2FA.
     * @return True se o token for válido, false caso contrário.
     */
    public boolean validateToken(String username, int token) {
        LOGGER.debug("Validando token TOTP para usuário: {}", username);
        Optional<User> userOptional = userRepository.findByUsernameWithPermissions(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String secret = user.getTwoFactorSecret();

            if (secret == null || secret.isEmpty()) {
                LOGGER.warn("Usuário '{}' não possui um secret configurado para 2FA.", username);
                return false;
            }

            boolean isAuthorized = gAuth.authorize(secret, token);
            LOGGER.info("Resultado da validação TOTP para '{}': {}", username, isAuthorized ? "SUCESSO" : "FALHA");
            return isAuthorized;
        } else {
            LOGGER.error("Usuário '{}' não encontrado para validação do token.", username);
            return false;
        }
    }
    
}