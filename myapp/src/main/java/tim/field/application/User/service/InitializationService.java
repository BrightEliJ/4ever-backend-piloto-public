package tim.field.application.User.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tim.field.application.User.model.Group;
import tim.field.application.User.model.Permission;
import tim.field.application.User.model.User;
import tim.field.application.User.model.UserGroup;
import tim.field.application.User.model.UserGroupId;
import tim.field.application.User.model.UserPermission;
import tim.field.application.User.repository.GroupRepository;
import tim.field.application.User.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InitializationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
	private GroupService groupService;

    @Autowired
    private TwoFactorService twoFactorService;

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@domain.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    @PostConstruct
    public void initializeSystem() {
        LOGGER.debug("Iniciando sistema...");
        initializePermissions();
        initializeAdminUser();
    }

    // Inicializa permissões do balde
    private void initializePermissions() {
        LOGGER.debug("Inicializando permissões...");
        permissionService.initializePermissions();
    }

    // Cria usuário administrador padrão
    private void initializeAdminUser() {
        LOGGER.debug("Inicializando usuário administrador...");
        Optional<User> adminUserOptional = userRepository.findByUsernameWithPermissions(DEFAULT_ADMIN_USERNAME);
    
        User adminUser;
    
        if (adminUserOptional.isEmpty()) {
            LOGGER.debug("Usuário administrador não encontrado. Criando novo usuário administrador...");
            adminUser = new User();
            adminUser.setUsername(DEFAULT_ADMIN_USERNAME);
            adminUser.setEmail(DEFAULT_ADMIN_EMAIL);
            adminUser.setFullName("Administrador Padrão");
            adminUser.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
            adminUser.setStatus("ativo");
            adminUser.setCreatedAt(LocalDateTime.now());
    
            // Configura 2FA para o administrador
            LOGGER.debug("Configurando 2FA para o administrador...");
            twoFactorService.setupTwoFactorForUser(adminUser);
    
            // Salva o usuário com as configurações de 2FA
            adminUser = userRepository.save(adminUser);
            LOGGER.debug("Usuário administrador criado com sucesso: {}", adminUser.getUsername());
        } else {
            adminUser = adminUserOptional.get();
            LOGGER.debug("Usuário administrador já existente: {}", adminUser.getUsername());
        }
    
        // Atribui todas as permissões disponíveis
        assignAllPermissions(adminUser);
    
        // Adiciona o admin a todos os grupos
        addAdminToAllGroups(adminUser);
    }    

    // Atribui todas as permissões disponíveis ao administrador
    private void assignAllPermissions(User adminUser) {
        LOGGER.debug("Atribuindo todas as permissões ao administrador...");
        List<Permission> allPermissions = permissionService.findAll();
        LOGGER.debug("Permissões disponíveis para atribuição: {}", allPermissions.size());

        for (Permission permission : allPermissions) {
            boolean alreadyHasPermission = adminUser.getUserPermissions().stream()
                    .anyMatch(up -> up.getPermission().getName().equals(permission.getName()));

            if (!alreadyHasPermission) {
                UserPermission userPermission = new UserPermission(adminUser, permission);
                adminUser.getUserPermissions().add(userPermission);
                LOGGER.debug("Permissão atribuída ao usuário administrador: {}", permission.getName());
            } else {
                LOGGER.debug("Usuário administrador já possui a permissão: {}", permission.getName());
            }
        }

        // Salva o usuário com as permissões atribuídas
        userRepository.save(adminUser);
        LOGGER.debug("Todas as permissões foram atribuídas ao usuário administrador.");
    }

    // Adiciona o usuário administrador a todos os grupos
    private void addAdminToAllGroups(User adminUser) {
        LOGGER.debug("Adicionando usuário administrador '{}' a todos os grupos...", adminUser.getUsername());

        List<Group> allGroups = groupService.findAll();
        LOGGER.debug("Grupos encontrados: {}", allGroups.size());

        for (Group group : allGroups) {
            LOGGER.debug("Verificando associação do administrador ao grupo '{}'", group.getName());

            // Verifica se já existe uma associação
            boolean isAlreadyAssociated = adminUser.getUserGroups().stream()
                    .anyMatch(userGroup -> userGroup.getGroup().getId().equals(group.getId()));

            if (isAlreadyAssociated) {
                LOGGER.debug("Usuário administrador já está associado ao grupo '{}'", group.getName());
                continue;
            }

            LOGGER.debug("Criando nova associação entre administrador e grupo '{}'", group.getName());

            UserGroup userGroup = new UserGroup();
            userGroup.setUser(adminUser);
            userGroup.setGroup(group);

            // Inicializa o identificador composto (se necessário)
            if (userGroup.getId() == null) {
                UserGroupId userGroupId = new UserGroupId();
                userGroupId.setUserId(adminUser.getId());
                userGroupId.setGroupId(group.getId());
                userGroup.setId(userGroupId);
            }

            // Adiciona ao conjunto de associações
            adminUser.getUserGroups().add(userGroup);
        }

        // Salva o usuário com as novas associações
        userRepository.save(adminUser);
        LOGGER.debug("Usuário administrador '{}' foi associado a todos os grupos disponíveis.", adminUser.getUsername());
    }

}
