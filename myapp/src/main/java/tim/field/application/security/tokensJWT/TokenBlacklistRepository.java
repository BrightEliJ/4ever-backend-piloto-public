package tim.field.application.security.tokensJWT;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistModel, Long> {
    boolean existsByToken(String token);
}