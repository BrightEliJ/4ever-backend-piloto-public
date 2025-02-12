package tim.field.application.User.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import tim.field.application.User.model.Group;
import tim.field.application.User.model.GroupInvitationCode;
import tim.field.application.User.model.User;
import tim.field.application.User.repository.UserRepository;
import tim.field.application.User.service.GroupService;
import tim.field.application.util.GlobalResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/groups")
public class GroupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupController.class);
    private final GroupService groupService;
    private final UserRepository userRepository;

    public GroupController(GroupService groupService, UserRepository userRepository) {
        this.groupService = groupService;
        this.userRepository = userRepository;
    }

    /**
     * 🔹 Endpoint para listar todos os grupos
     */
    @GetMapping
    public ResponseEntity<GlobalResponse<List<Map<String, Object>>>> getAllGroups(HttpServletRequest request) {
        try {
            List<Map<String, Object>> groups = groupService.getAllGroupsWithIds();
            setRequestAttributes(request, "READ", "List all groups", HttpStatus.OK.value(), null);
            return ResponseEntity.ok(GlobalResponse.success("Lista de grupos recuperada com sucesso.", groups));
        } catch (Exception e) {
            LOGGER.error("Erro ao recuperar lista de grupos: ", e);
            setRequestAttributes(request, "READ", "Error fetching groups", HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro ao recuperar lista de grupos.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * 🔹 Criar ou atualizar o código de convite de um grupo
     */
    @PostMapping("/{groupId}/invitation-code")
    public ResponseEntity<GlobalResponse<Map<String, Object>>> createOrUpdateInvitationCode(
            @PathVariable Long groupId, HttpServletRequest request) {
        try {
            // 🔹 Extrair usuário autenticado
            String username = getAuthenticatedUsername();
            User user = userRepository.findByUsernameWithPermissions(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            // 🔹 Verificar associação ao grupo
            boolean isGroupAssociated = user.getUserGroups().stream()
                    .anyMatch(userGroup -> userGroup.getGroup().getId().equals(groupId));

            if (!isGroupAssociated) {
                throw new RuntimeException("Você não está associado a este grupo.");
            }

            // 🔹 Atualizar código de convite
            GroupInvitationCode updatedCode = groupService.updateGroupInvitationCode(groupId);

            // 🔹 Recuperar informações do grupo
            Group group = groupService.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Grupo não encontrado."));

            // 🔹 Registrar log da requisição
            setRequestAttributes(request, "GROUP", "Invitation code updated",
                    HttpStatus.OK.value(), "{\"groupId\":" + groupId + "}");

            Map<String, Object> data = Map.of(
                    "group", group.getName(),
                    "code", updatedCode.getCode(),
                    "expiresAt", updatedCode.getExpiresAt()
            );

            return ResponseEntity.ok(GlobalResponse.success("Código de convite atualizado com sucesso.", data));

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            if ("Você não está associado a este grupo.".equals(errorMessage)) {
                LOGGER.warn("Acesso negado: {}", errorMessage);
            } else {
                LOGGER.error("Erro ao atualizar código de convite para grupo {}: {}", groupId, errorMessage);
            }

            setRequestAttributes(request, "GROUP", "Error updating invitation code",
                    HttpStatus.BAD_REQUEST.value(), "{\"error\":\"" + errorMessage + "\"}");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error(errorMessage, HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao atualizar código de convite para grupo {}: ", groupId, e);

            setRequestAttributes(request, "GROUP", "Unexpected error",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro inesperado ao atualizar o código de convite.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * 🔹 Método auxiliar para obter o usuário autenticado
     */
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado.");
        }
        return authentication.getName();
    }

    /**
     * 🔹 Método auxiliar para configurar logs e auditoria
     */
    private void setRequestAttributes(HttpServletRequest request, String logType, String event, int status, String payload) {
        request.setAttribute("logType", logType);
        request.setAttribute("event", event);
        request.setAttribute("responseStatus", status);
        request.setAttribute("payload", payload);
    }
}