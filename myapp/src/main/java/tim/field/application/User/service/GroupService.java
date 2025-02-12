package tim.field.application.User.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import tim.field.application.User.model.Group;
import tim.field.application.User.model.GroupInvitationCode;
import tim.field.application.User.model.GroupPermission;
import tim.field.application.User.model.Permission;
import tim.field.application.User.model.User;
import tim.field.application.User.repository.GroupRepository;
import tim.field.application.User.repository.UserRepository;
import tim.field.application.User.repository.GroupInvitationCodeRepository;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
	private UserRepository userRepository;

    @Autowired
    private GroupInvitationCodeRepository invitationCodeRepository;

    @Autowired
    private PermissionService permissionService;

    // Lista estática com os grupos padrão
    private static final List<Group> GROUP_BUCKET = Arrays.asList(
        new Group("TIM", "Grupo da empresa TIM", "COMPANY"),
        new Group("Field", "Grupo de trabalho Field", "TEAM")
    );

    // Busca grupo por ID
    public Optional<Group> findById(Long id) {
        return groupRepository.findById(id);
    }

    public List<Group> findAllByIds(List<Long> ids) {
        List<Group> groups = groupRepository.findAllById(ids);
        if (groups.size() != ids.size()) {
            throw new RuntimeException("Um ou mais grupos não foram encontrados.");
        }
        return groups;
    }

    // Lista todos os grupos
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    // Salva ou atualiza um grupo
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    // Exclui um grupo por ID
    public void deleteById(Long id) {
        groupRepository.deleteById(id);
    }

    // Adiciona uma permissão a um grupo
    public Group addPermissionToGroup(Long groupId, Long permissionId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado."));

        Permission permission = permissionService.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permissão não encontrada."));

        GroupPermission groupPermission = new GroupPermission();
        groupPermission.setGroup(group);
        groupPermission.setPermission(permission);
        group.getGroupPermissions().add(groupPermission);

        return groupRepository.save(group);
    }

    // Remove uma permissão de um grupo
    public Group removePermissionFromGroup(Long groupId, Long permissionId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado."));

        group.getGroupPermissions().removeIf(gp -> gp.getPermission().getId().equals(permissionId));
        return groupRepository.save(group);
    }

    // Atualiza ou cria um novo código de convite para um grupo
    public GroupInvitationCode updateGroupInvitationCode(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado."));

        // Geração de código de convite (exemplo simples)
        String newCode = generateUniqueCode();

        // Configurar a data de expiração para 1 hora a partir de agora
        LocalDateTime expiration = LocalDateTime.now().plusHours(1);

        // Recuperar ou criar um novo código de convite
        GroupInvitationCode invitationCode = invitationCodeRepository.findByGroup(group)
                .orElse(new GroupInvitationCode());

        if (invitationCode.getId() == null) { // Código não existe
            invitationCode.setGroup(group);
        }

        invitationCode.setCode(newCode);
        invitationCode.setExpiresAt(expiration);
        invitationCode.setUpdatedAt(LocalDateTime.now());

        // Obter o ID do usuário autenticado do contexto de segurança
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String loggedInUsername = authentication.getName(); // Obter o nome do usuário
            User user = userRepository.findByUsernameWithPermissions(loggedInUsername) // Recuperar o objeto User
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
            invitationCode.setUpdatedBy(user); // Atribuir o ID do usuário ao campo updatedBy
        } else {
            throw new RuntimeException("Usuário não autenticado.");
        }

        return invitationCodeRepository.save(invitationCode);
    }

    // Verifica se um código de convite é válido
    public boolean isInvitationCodeValid(String code) {
        Optional<GroupInvitationCode> invitationCodeOpt = invitationCodeRepository.findByCode(code);

        if (invitationCodeOpt.isPresent()) {
            GroupInvitationCode invitationCode = invitationCodeOpt.get();
            return invitationCode.getExpiresAt().isAfter(LocalDateTime.now());
        }
        return false;
    }

    // Método auxiliar para gerar um código único de convite
    private String generateUniqueCode() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    // Método para listar os grupos com seus IDs e código de convite (caso tenha)
    public List<Map<String, Object>> getAllGroupsWithIds() {
        return groupRepository.findAll().stream()
                .map(group -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("groupid", group.getId());
                    map.put("groupName", group.getName());
                    map.put("description", group.getDescription());
                    map.put("groupType", group.getGroupType());

                    // Garante que está pegando apenas convites do próprio grupo
                    List<GroupInvitationCode> groupInvitations = group.getInvitationCodes().stream()
                            .filter(invite -> invite.getGroup().getId().equals(group.getId())) // Confirma que o convite pertence ao grupo
                            .sorted(Comparator.comparing(GroupInvitationCode::getCreatedAt).reversed()) // Ordena do mais recente para o mais antigo
                            .toList(); 

                    // Pega o mais recente, se houver
                    String latestCode = groupInvitations.isEmpty() ? null : groupInvitations.get(0).getCode();

                    map.put("groupInvitationCode", latestCode);
                    // Obtém o convite mais recente, se houver
                    GroupInvitationCode latestInvitation = groupInvitations.isEmpty() ? null : groupInvitations.get(0);

                    // Adiciona a data de atualização ao mapa, se existir
                    map.put("createdAt", latestInvitation != null ? latestInvitation.getUpdatedAt() : null);

                    return map;
                })
                .collect(Collectors.toList());
    }

    // Método de inicialização para criar grupos padrão
    @PostConstruct
    public void initializeGroups() {
        for (Group groupTemplate : GROUP_BUCKET) {
            if (!groupRepository.existsByName(groupTemplate.getName())) {
                Group newGroup = new Group();
                newGroup.setName(groupTemplate.getName());
                newGroup.setDescription(groupTemplate.getDescription());
                newGroup.setGroupType(groupTemplate.getGroupType());
                groupRepository.save(newGroup);
            }
        }
    }
}
