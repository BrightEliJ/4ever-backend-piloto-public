package tim.field.application.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import java.util.function.Supplier;

import tim.field.application.User.model.User;

public class PermissionAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final String requiredPermission;

    public PermissionAuthorizationManager(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
    
        Object principal = auth.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            boolean hasPermission = user.getUserPermissions().stream()
                    .anyMatch(up -> up.getPermission().getName().equals(requiredPermission));
            return new AuthorizationDecision(hasPermission);
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User userDetails = 
                (org.springframework.security.core.userdetails.User) principal;
    
            boolean hasPermission = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(requiredPermission));
            return new AuthorizationDecision(hasPermission);
        }
    
        return new AuthorizationDecision(false);
    }
}
