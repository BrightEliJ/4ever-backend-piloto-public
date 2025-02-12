package tim.field.application.User.dto;

import jakarta.validation.constraints.NotNull;

public class LoginRequestDTO {

    @NotNull(message = "O username não pode estar vazio.")
    private String username;

    @NotNull(message = "A senha não pode estar vazia.")
    private String password;

    // Construtor vazio
    public LoginRequestDTO() {}

    // Construtor completo
    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters e Setters
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
}
