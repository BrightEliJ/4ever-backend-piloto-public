package tim.field.application.User.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "user_permissions")
public class UserPermission {

    @EmbeddedId
    private UserPermissionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JsonBackReference // Marca este lado como o "dependente" da relação
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("permissionId")
    private Permission permission;

    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt = LocalDateTime.now();

    @Column(name = "expiration_at")
    private LocalDateTime expirationAt;

    public UserPermission() {}

    public UserPermission(User user, Permission permission) {
        this.user = user;
        this.permission = permission;
        this.id = new UserPermissionId(user.getId(), permission.getId());
    }

    // Getters and Setters
    public UserPermissionId getId() {
        return id;
    }

    public void setId(UserPermissionId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(LocalDateTime grantedAt) {
        this.grantedAt = grantedAt;
    }

    public LocalDateTime getExpirationAt() {
        return expirationAt;
    }

    public void setExpirationAt(LocalDateTime expirationAt) {
        this.expirationAt = expirationAt;
    }
}
