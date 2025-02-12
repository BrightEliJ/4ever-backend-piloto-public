package tim.field.application.User.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tim.field.application.User.model.Group;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByName(String name);

    boolean existsByName(String name);

    @Override
    List<Group> findAll();

    // Busca grupo com permissões carregadas
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.groupPermissions gp WHERE g.id = :id")
    Optional<Group> findByIdWithPermissions(Long id);
}