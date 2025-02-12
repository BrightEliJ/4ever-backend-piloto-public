package tim.field.application.User.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserManagementDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastAccess;
    private int loginAttempts;
    private List<GroupDTO> groups;
    private List<PermissionDTO> permissions;

    //Getters e Setters

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
    public List<GroupDTO> getGroups() {
        return groups;
    }
    public void setGroups(List<GroupDTO> groups) {
        this.groups = groups;
    }
    public List<PermissionDTO> getPermissions() {
        return permissions;
    }
    public void setPermissions(List<PermissionDTO> permissions) {
        this.permissions = permissions;
    }
    public int getLoginAttempts() {
        return loginAttempts;
    }
    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }
}
