package tim.field.application.User.dto;

import jakarta.validation.constraints.NotNull;

public class ActivationRequestDTO {

    @NotNull(message = "O token de ativação não pode estar vazio.")
    private String token;

    // Construtor vazio
    public ActivationRequestDTO() {}

    // Construtor completo
    public ActivationRequestDTO(String token) {
        this.token = token;
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
