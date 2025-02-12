package tim.field.application.User.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tim.field.application.User.model.Group;
import tim.field.application.User.model.UserGroup;
import tim.field.application.User.model.UserGroupId;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {

    void deleteByUserIdAndGroupId(Long userId, Long groupId);

    @Query("SELECT g FROM Group g " +
           "LEFT JOIN FETCH g.groupPermissions gp " +
           "LEFT JOIN UserGroup ug ON ug.group.id = g.id " +
           "WHERE ug.user.id = :userId")
    List<Group> findGroupsWithPermissionsByUserId(@Param("userId") Long userId);
}