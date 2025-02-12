package tim.field.application.User.dto;

import java.time.LocalDateTime;
import java.util.Set;

import tim.field.application.User.model.User;

public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String status;
    private LocalDateTime createdAt;
    private Set<String> roles; // Novo campo para permissões
    private String qrCodeUrl;

    // Construtor vazio
    public UserResponseDTO() {}

    // Construtor completo
    public UserResponseDTO(Long id, String username, String email, String fullName, String status, LocalDateTime createdAt, String qrCodeUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.status = status;
        this.createdAt = createdAt;
        this.roles = roles;
        this.qrCodeUrl = qrCodeUrl;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getter e Setter para roles
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

     // Método estático para converter User para UserResponseDTO
     public static UserResponseDTO fromUser(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getQrCodeUrl()
        );
    }
}
