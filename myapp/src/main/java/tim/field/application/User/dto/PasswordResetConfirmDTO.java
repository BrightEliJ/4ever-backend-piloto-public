package tim.field.application.User.dto;

import jakarta.validation.constraints.NotNull;

public class PasswordResetConfirmDTO {

    @NotNull(message = "O token de redefinição não pode estar vazio.")
    private String tokenPassReset;

    @NotNull(message = "A nova senha não pode estar vazia.")
    private String newPassword;

    // Construtor vazio
    public PasswordResetConfirmDTO() {}

    // Construtor completo
    public PasswordResetConfirmDTO(String tokenPassReset, String newPassword) {
        this.tokenPassReset = tokenPassReset;
        this.newPassword = newPassword;
    }

    // Getters e Setters
    public String getTokenPassReset() {
        return tokenPassReset;
    }

    public void setTokenPassReset(String tokenPassReset) {
        this.tokenPassReset = tokenPassReset;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
