package tim.field.application.User.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import tim.field.application.User.model.User;
import tim.field.application.User.model.UserGroup;
import tim.field.application.User.model.Permission;
import tim.field.application.User.model.UserPermission;
import tim.field.application.User.model.GroupPermission;
import tim.field.application.User.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithPermissions(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Bloquear somente usuários com status "bloqueado"
        if ("bloqueado".equalsIgnoreCase(user.getStatus())) {
            LOGGER.error("Conta do usuário '{}' está bloqueada.", username);
            throw new LockedException("Conta do usuário está bloqueada.");
        }

        // Converte permissões para GrantedAuthority
        Set<GrantedAuthority> authorities = extractAuthorities(user);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,  // habilitado, mesmo para usuários inativos (tratamento posterior)
                true,  // conta não expirada
                true,  // credenciais não expiradas
                !"bloqueado".equalsIgnoreCase(user.getStatus()),  // conta bloqueada apenas para "bloqueado"
                authorities
        );
    }   

    // Método auxiliar para extrair authorities
    private Set<GrantedAuthority> extractAuthorities(User user) {
        // Permissões individuais do usuário
        Set<GrantedAuthority> userPermissions = user.getUserPermissions().stream()
                .map(UserPermission::getPermission)
                .map(Permission::getName) // Nome exato da permissão
                .map(SimpleGrantedAuthority::new) // Converte diretamente para GrantedAuthority
                .collect(Collectors.toSet());

        LOGGER.debug("Permissões individuais para '{}': {}", user.getUsername(), userPermissions);

        // Permissões associadas aos grupos
        Set<GrantedAuthority> groupPermissions = user.getUserGroups().stream()
                .flatMap(userGroup -> userGroup.getGroup().getGroupPermissions().stream())
                .map(GroupPermission::getPermission)
                .map(Permission::getName) // Nome exato da permissão
                .map(SimpleGrantedAuthority::new) // Converte diretamente para GrantedAuthority
                .collect(Collectors.toSet());

        LOGGER.debug("Permissões de grupo para '{}': {}", user.getUsername(), groupPermissions);

        // Junta todas as permissões
        userPermissions.addAll(groupPermissions);
        return userPermissions;
    }
}
