package tim.field.application.User.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tim.field.application.User.model.Permission;
import tim.field.application.User.repository.PermissionRepository;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    // Lista estática com o balde de permissões
    private static final List<String> PERMISSION_BUCKET = Arrays.asList(
        "2FA_PRIVILEGE",
        "USER_PRIVILEGE",
        "ADMIN_PRIVILEGE",
        "VIEW_REPORTS",
        "MANAGE_USERS",
        "MANAGE_SETTINGS"
    );

    // Busca permissão por ID
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    public List<Permission> findByIds(List<Long> ids) {
        List<Permission> permissions = permissionRepository.findAllById(ids);
        if (permissions.size() != ids.size()) {
            throw new RuntimeException("Uma ou mais permissões não foram encontradas.");
        }
        return permissions;
    }

    // Busca permissão por nome
    public Optional<Permission> findByName(String name) {
        return permissionRepository.findByName(name);
    }

    // Lista todas as permissões
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    // Salva ou atualiza uma permissão
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    // Exclui uma permissão por ID
    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }

    // Método de inicialização para criar permissões faltantes
    @PostConstruct
    public void initializePermissions() {
        for (String permissionName : PERMISSION_BUCKET) {
            if (!permissionRepository.existsByName(permissionName)) {
                Permission permission = new Permission();
                permission.setName(permissionName);
                permissionRepository.save(permission);
            }
        }
    }
}
