package tim.field.application.User.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "group_type", nullable = false)
    private String groupType; // TEAM, COMPANY, OTHER

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Ignora a serialização de groupPermissions para evitar ciclos
    private Set<GroupPermission> groupPermissions = new HashSet<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Ignora a serialização de UserGroups para evitar ciclos
    private Set<UserGroup> userGroups = new HashSet<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<GroupInvitationCode> invitationCodes = new HashSet<>();

    // Construtor padrão
    public Group() {
    }

    // Construtor adicional
    public Group(String name, String description, String groupType) {
        this.name = name;
        this.description = description;
        this.groupType = groupType;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
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

    public Set<GroupPermission> getGroupPermissions() {
        return groupPermissions;
    }

    public void setGroupPermissions(Set<GroupPermission> groupPermissions) {
        this.groupPermissions = groupPermissions;
    }

    public Set<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public Set<GroupInvitationCode> getInvitationCodes() {
        return invitationCodes;
    }

    public void setInvitationCodes(Set<GroupInvitationCode> invitationCodes) {
        this.invitationCodes = invitationCodes;
    }

}
