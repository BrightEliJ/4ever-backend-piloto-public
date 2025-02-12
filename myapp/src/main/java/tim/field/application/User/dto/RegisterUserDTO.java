package tim.field.application.User.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterUserDTO {

    @NotBlank(message = "O campo username é obrigatório.")
    @Size(min = 3, max = 50, message = "O username deve ter entre 3 e 50 caracteres.")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "O username deve conter apenas caracteres alfabéticos.")
    private String username;

    @NotBlank(message = "O campo email é obrigatório.")
    @Email(message = "O e-mail fornecido não é válido.")
    private String email;

    @NotBlank(message = "O campo senha é obrigatório.")
    @Size(min = 8, max = 255, message = "A senha deve ter entre 8 e 255 caracteres.")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*]).*$",
        message = "A senha deve conter pelo menos uma letra minúscula, uma letra maiúscula, um número e um caractere especial."
    )
    private String password;

    @NotBlank(message = "O campo nome completo é obrigatório.")
    private String fullName;

    @Pattern(
        regexp = "^\\+55 \\(\\d{2}\\) \\d{5}-\\d{4}$",
        message = "O número de telefone deve estar no formato +55 (XX) XXXXX-XXXX."
    )
    private String phoneNumber;

    private String inviteCode; // Novo campo para código de convite

    private String matricula;

    // Getters e setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
