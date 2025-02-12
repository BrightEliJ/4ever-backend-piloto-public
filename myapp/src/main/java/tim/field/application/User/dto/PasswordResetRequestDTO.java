package tim.field.application.User.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class PasswordResetRequestDTO {

    @NotNull(message = "O e-mail não pode estar vazio.")
    @Email(message = "Forneça um endereço de e-mail válido.")
    private String email;

    // Construtor vazio
    public PasswordResetRequestDTO() {}

    // Construtor completo
    public PasswordResetRequestDTO(String email) {
        this.email = email;
    }

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}