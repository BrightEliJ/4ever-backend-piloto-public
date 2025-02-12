package tim.field.application.User.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import tim.field.application.User.model.Permission;

import java.util.Optional;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);

    // Busca permissões associadas a um grupo
    @Query("SELECT p FROM Permission p " +
           "JOIN GroupPermission gp ON gp.permission.id = p.id " +
           "WHERE gp.group.id = :groupId")
    List<Permission> findAllByGroupId(Long groupId);
}