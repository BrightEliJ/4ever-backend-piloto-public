package tim.field.application.User.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tim.field.application.User.model.UserPermission;
import tim.field.application.User.model.UserPermissionId;
import tim.field.application.User.model.Permission;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, UserPermissionId> {

    boolean existsById(UserPermissionId id);

    @Query("SELECT p FROM Permission p " +
    "LEFT JOIN UserPermission up ON p.id = up.permission.id " +
    "LEFT JOIN GroupPermission gp ON p.id = gp.permission.id " +
    "LEFT JOIN UserGroup ug ON ug.group.id = gp.group.id " +
    "WHERE ug.user.id = :userId OR up.user.id = :userId")
List<Permission> findEffectivePermissionsByUserId(@Param("userId") Long userId);

}