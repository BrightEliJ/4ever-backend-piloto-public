package tim.field.application.User.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tim.field.application.User.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Busca por nome de usuário com permissões carregadas
    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.userPermissions up
        LEFT JOIN FETCH up.permission
        LEFT JOIN FETCH u.userGroups ug
        LEFT JOIN FETCH ug.group g
        LEFT JOIN FETCH g.groupPermissions gp
        LEFT JOIN FETCH gp.permission
        WHERE u.username = :username
    """)
    Optional<User> findByUsernameWithPermissions(String username);

    // Busca por ID com permissões carregadas
    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.userPermissions up
        LEFT JOIN FETCH up.permission
        LEFT JOIN FETCH u.userGroups ug
        LEFT JOIN FETCH ug.group g
        LEFT JOIN FETCH g.groupPermissions gp
        LEFT JOIN FETCH gp.permission
        WHERE u.id = :id
    """)
    Optional<User> findByIdWithPermissions(Long id);

    // Busca por e-mail
    Optional<User> findByEmail(String email);

    // Busca por nome de usuário
    User findByUsername(String username);

    // Busca por token de ativação
    Optional<User> findByActivationToken(String token);

    // Verificação de existência por nome de usuário
    boolean existsByUsername(String username);

    // Verificação de existência por e-mail
    boolean existsByEmail(String email);
}
