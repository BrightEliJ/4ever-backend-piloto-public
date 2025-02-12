package tim.field.application.User.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserPermissionId implements Serializable {

    private Long userId;
    private Long permissionId;

    public UserPermissionId() {}

    public UserPermissionId(Long userId, Long permissionId) {
        this.userId = userId;
        this.permissionId = permissionId;
    }

    // Getters, Setters, hashCode e equals
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPermissionId that = (UserPermissionId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, permissionId);
    }
}
