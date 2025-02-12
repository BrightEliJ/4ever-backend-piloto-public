package tim.field.application.User.dto;

public class TwoFactorValidationRequest {

    private String username;
    private int totpCode;
    private String accessToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotpCode() {
        return totpCode;
    }

    public void setTotpCode(int totpCode) {
        this.totpCode = totpCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
