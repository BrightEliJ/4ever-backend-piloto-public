package tim.field.application.User.dto;

import tim.field.application.User.model.User;

import java.time.LocalDateTime;

public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String password; // Novo campo
    private String fullName;
    private String phoneNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastAccess;

    // Construtor vazio
    public UserDTO() {}

    // Construtor com argumentos
    public UserDTO(Long id, String username, String email, String password, String fullName, String phoneNumber, String status,
                   LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastAccess) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password; // Novo campo
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastAccess = lastAccess;
    }

    // Construtor para conversão de entidade User para DTO
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.phoneNumber = user.getPhoneNumber();
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.lastAccess = user.getLastAccess();
    }

    // Getter e Setter para todos os campos
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    // Converta o DTO para a entidade User
    public User toModel() {
        User user = new User();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password); // Novo campo
        user.setFullName(this.fullName);
        user.setPhoneNumber(this.phoneNumber);
        user.setStatus(this.status != null ? this.status : "inativo"); // Valor padrão se null
        user.setCreatedAt(this.createdAt != null ? this.createdAt : LocalDateTime.now());
        user.setUpdatedAt(this.updatedAt);
        user.setLastAccess(this.lastAccess);
        return user;
    }
}
