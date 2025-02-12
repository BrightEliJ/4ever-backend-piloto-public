package tim.field.application.User.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tim.field.application.User.model.GroupPermission;
import tim.field.application.User.model.GroupPermissionId;

@Repository
public interface GroupPermissionRepository extends JpaRepository<GroupPermission, GroupPermissionId> {

    boolean existsById(GroupPermissionId id);
}
