package tim.field.application.security.tokensJWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    @Autowired
    private TokenBlacklistRepository repository;

    public void addToBlacklist(String token) {
        repository.save(new TokenBlacklistModel(token));
    }

    public boolean isBlacklisted(String token) {
        return repository.existsByToken(token);
    }
}