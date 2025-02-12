package tim.field.application.User.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tim.field.application.User.model.Group;
import tim.field.application.User.model.GroupInvitationCode;

import java.util.Optional;

@Repository
public interface GroupInvitationCodeRepository extends JpaRepository<GroupInvitationCode, Long> {

    // Busca por código de convite
    Optional<GroupInvitationCode> findByCode(String code);

    // Busca por código de convite
    Optional<GroupInvitationCode> findByGroup(Group group);

    // Verifica se um código de convite já existe
    boolean existsByCode(String code);
}