package tim.field.application.User.mapper;

import tim.field.application.User.dto.UserManagementDTO;
import tim.field.application.User.dto.GroupDTO;
import tim.field.application.User.dto.PermissionDTO;
import tim.field.application.User.model.User;

import java.util.stream.Collectors;

public class UserMapper {
    public static UserManagementDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserManagementDTO dto = new UserManagementDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastAccess(user.getLastAccess());
        dto.setLoginAttempts(user.getLoginAttempts());

        // Utilizando os construtores já definidos em GroupDTO e PermissionDTO
        dto.setGroups(user.getUserGroups().stream()
                .map(group -> new GroupDTO(group.getGroup()))
                .collect(Collectors.toList()));

        dto.setPermissions(user.getUserPermissions().stream()
                .map(permission -> new PermissionDTO(permission.getPermission()))
                .collect(Collectors.toList()));

        return dto;
    }
}