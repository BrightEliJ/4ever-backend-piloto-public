package tim.field.application.security.tokensJWT;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
public class TokenBlacklistModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String token;

    @Column(name = "revoked_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime revokedAt = LocalDateTime.now();

    // Construtor padrão
    public TokenBlacklistModel() {
    }

    // Construtor com token
    public TokenBlacklistModel(String token) {
        this.token = token;
        this.revokedAt = LocalDateTime.now(); // Define a data automaticamente
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }

    // Optional: Override toString, equals, and hashCode methods
    @Override
    public String toString() {
        return "TokenBlacklist{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", revokedAt=" + revokedAt +
                '}';
    }
}