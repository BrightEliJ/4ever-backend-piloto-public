package tim.field.application.User.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import tim.field.application.User.dto.UserManagementDTO;
import tim.field.application.User.repository.UserRepository;
import tim.field.application.User.service.UserService;
import tim.field.application.util.GlobalResponse;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint para adicionar uma permissão a um usuário.
     *
     * @param userId       ID do usuário.
     * @param permissionId ID da permissão.
     * @param request      HttpServletRequest para logs.
     * @return Resposta indicando sucesso ou erro.
     */
    @PostMapping("/{userId}/add-permissions")
    public ResponseEntity<GlobalResponse<String>> addPermissionsToUser(
            @PathVariable Long userId,
            @RequestParam List<Long> permissionIds,
            HttpServletRequest request) {
        try {
            userService.addPermissionsToUser(userId, permissionIds);
    
            LOGGER.info("Permissões com IDs '{}' adicionadas ao usuário com ID '{}'.", permissionIds, userId);
    
            setRequestAttributes(request, "USER_PERMISSION", "Permissions added to user",
                    HttpStatus.OK.value(), "{\"userId\":" + userId + ",\"permissionIds\":" + permissionIds + "}");
    
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(GlobalResponse.success("Permissões adicionadas ao usuário com sucesso.", null));
        } catch (RuntimeException e) {
            LOGGER.error("Erro ao adicionar permissões ao usuário com ID '{}': ", userId, e);
    
            setRequestAttributes(request, "USER_PERMISSION", "Error adding permissions to user",
                    HttpStatus.BAD_REQUEST.value(), "{\"error\":\"" + e.getMessage() + "\"}");
    
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao adicionar permissões ao usuário com ID '{}': ", userId, e);
    
            setRequestAttributes(request, "USER_PERMISSION", "Unexpected error",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");
    
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro inesperado ao adicionar permissões ao usuário.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
    * Endpoint para remover múltiplas permissões de um usuário.
    *
    * @param userId        ID do usuário.
    * @param permissionIds Lista de IDs das permissões.
    * @param request       HttpServletRequest para logs.
    * @return Resposta indicando sucesso ou erro.
    */
    @DeleteMapping("/{userId}/remove-permissions")
    public ResponseEntity<GlobalResponse<String>> removePermissionsFromUser(
                @PathVariable Long userId,
                @RequestParam List<Long> permissionIds,
                HttpServletRequest request) {
        try {
                userService.removePermissionsFromUser(userId, permissionIds);

                setRequestAttributes(request, "USER_PERMISSION", "Permissions removed from user",
                        HttpStatus.OK.value(), "{\"userId\":" + userId + ",\"permissionIds\":" + permissionIds + "}");

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(GlobalResponse.success("Permissões removidas do usuário com sucesso.", null));
        } catch (RuntimeException e) {
                LOGGER.error("Erro ao remover permissões do usuário com ID '{}': ", userId, e);

                setRequestAttributes(request, "USER_PERMISSION", "Error removing permissions from user",
                        HttpStatus.BAD_REQUEST.value(), "{\"error\":\"" + e.getMessage() + "\"}");

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(GlobalResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
                LOGGER.error("Erro inesperado ao remover permissões do usuário com ID '{}': ", userId, e);

                setRequestAttributes(request, "USER_PERMISSION", "Unexpected error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(GlobalResponse.error("Erro inesperado ao remover permissões do usuário.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Endpoint para remover todas as permissões de um usuário.
     *
     * @param userId  ID do usuário.
     * @param request HttpServletRequest para logs.
     * @return Resposta indicando sucesso ou erro.
     */
    @DeleteMapping("/{userId}/remove-all-permissions")
    public ResponseEntity<GlobalResponse<String>> removeAllPermissionsFromUser(
            @PathVariable Long userId,
            HttpServletRequest request) {
        try {
            userService.removeAllPermissionsFromUser(userId);

            setRequestAttributes(request, "USER_PERMISSION", "All permissions removed from user",
                    HttpStatus.OK.value(), "{\"userId\":" + userId + "}");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(GlobalResponse.success("Todas as permissões foram removidas do usuário com sucesso.", null));
        } catch (RuntimeException e) {
            LOGGER.error("Erro ao remover permissões do usuário com ID '{}': ", userId, e);

            setRequestAttributes(request, "USER_PERMISSION", "Error removing all permissions from user",
                    HttpStatus.BAD_REQUEST.value(), "{\"error\":\"" + e.getMessage() + "\"}");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao remover permissões do usuário com ID '{}': ", userId, e);

            setRequestAttributes(request, "USER_PERMISSION", "Unexpected error",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro inesperado ao remover permissões do usuário.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/{userId}/add-groups")
    public ResponseEntity<GlobalResponse<String>> addUserToGroups(
            @PathVariable Long userId,
            @RequestParam List<Long> groupIds,
            HttpServletRequest request) {
        try {
            userService.addUserToGroups(userId, groupIds);
    
            LOGGER.info("Usuário com ID '{}' adicionado aos grupos com IDs '{}'.", userId, groupIds);
    
            setRequestAttributes(request, "USER_GROUP", "User added to groups",
                    HttpStatus.OK.value(), "{\"userId\":" + userId + ",\"groupIds\":" + groupIds + "}");
    
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(GlobalResponse.success("Usuário adicionado aos grupos com sucesso.", null));
        } catch (RuntimeException e) {
            LOGGER.error("Erro ao adicionar o usuário com ID '{}' aos grupos '{}': ", userId, groupIds, e);
    
            setRequestAttributes(request, "USER_GROUP", "Error adding user to groups",
                    HttpStatus.BAD_REQUEST.value(), "{\"error\":\"" + e.getMessage() + "\"}");
    
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao adicionar o usuário com ID '{}' aos grupos '{}': ", userId, groupIds, e);
    
            setRequestAttributes(request, "USER_GROUP", "Unexpected error",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");
    
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro inesperado ao adicionar o usuário aos grupos.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
    * Endpoint para remover um usuário de múltiplos grupos.
    *
    * @param userId   ID do usuário.
    * @param groupIds Lista de IDs dos grupos.
    * @param request  HttpServletRequest para logs.
    * @return Resposta indicando sucesso ou erro.
    */
    @DeleteMapping("/{userId}/remove-groups")
    public ResponseEntity<GlobalResponse<String>> removeUserFromGroups(
                @PathVariable Long userId,
                @RequestParam List<Long> groupIds,
                HttpServletRequest request) {
        try {
                userService.removeUserFromGroups(userId, groupIds);

                setRequestAttributes(request, "USER_GROUP", "User removed from groups",
                        HttpStatus.OK.value(), "{\"userId\":" + userId + ",\"groupIds\":" + groupIds + "}");

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(GlobalResponse.success("Usuário removido dos grupos com sucesso.", null));
        } catch (RuntimeException e) {
                LOGGER.error("Erro ao remover usuário dos grupos '{}': ", groupIds, e);

                setRequestAttributes(request, "USER_GROUP", "Error removing user from groups",
                        HttpStatus.BAD_REQUEST.value(), "{\"error\":\"" + e.getMessage() + "\"}");

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(GlobalResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
                LOGGER.error("Erro inesperado ao remover usuário dos grupos '{}': ", groupIds, e);

                setRequestAttributes(request, "USER_GROUP", "Unexpected error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(GlobalResponse.error("Erro inesperado ao remover usuário dos grupos.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Endpoint para remover um usuário de todos os grupos.
     *
     * @param userId  ID do usuário.
     * @param request HttpServletRequest para logs.
     * @return Resposta indicando sucesso ou erro.
     */
    @DeleteMapping("/{userId}/remove-all-groups")
    public ResponseEntity<GlobalResponse<String>> removeUserFromAllGroups(
            @PathVariable Long userId,
            HttpServletRequest request) {
        try {
            userService.removeUserFromAllGroups(userId);

            setRequestAttributes(request, "USER_GROUP", "User removed from all groups",
                    HttpStatus.OK.value(), "{\"userId\":" + userId + "}");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(GlobalResponse.success("Usuário removido de todos os grupos com sucesso.", null));
        } catch (RuntimeException e) {
            LOGGER.error("Erro ao remover usuário de todos os grupos: ", e);

            setRequestAttributes(request, "USER_GROUP", "Error removing user from all groups",
                    HttpStatus.BAD_REQUEST.value(), "{\"error\":\"" + e.getMessage() + "\"}");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao remover usuário de todos os grupos: ", e);

            setRequestAttributes(request, "USER_GROUP", "Unexpected error",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro inesperado ao remover usuário de todos os grupos.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


     /**
     * Retorna todos os usuários como DTO com GlobalResponse.
     */
     @GetMapping
     public ResponseEntity<GlobalResponse<List<UserManagementDTO>>> getAllUsers(HttpServletRequest request) {
        try {
                List<UserManagementDTO> users = userService.getAllUsers();

                setRequestAttributes(request, "USER_MANAGEMENT", "Fetch all users",
                        HttpStatus.OK.value(), "{}");

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(GlobalResponse.success("Usuários carregados com sucesso.", users));
        } catch (Exception e) {
                LOGGER.error("Erro ao buscar usuários: ", e);

                setRequestAttributes(request, "USER_MANAGEMENT", "Error fetching users",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(), "{\"error\":\"" + e.getMessage() + "\"}");

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(GlobalResponse.error("Erro inesperado ao buscar usuários.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Retorna um usuário específico pelo ID.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId, HttpServletRequest request) {
        try {
            Optional<UserManagementDTO> user = userService.getUserById(userId);
            if (user.isEmpty()) {
                setRequestAttributes(request, "USER_MANAGEMENT", "User not found",
                        HttpStatus.NOT_FOUND.value(), "{\"userId\":" + userId + "}");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(GlobalResponse.error("Usuário não encontrado", 404));
            }
            setRequestAttributes(request, "USER_MANAGEMENT", "User fetched",
                    HttpStatus.OK.value(), "{\"userId\":" + userId + "}");
            return ResponseEntity.ok(user.get());
        } catch (Exception e) {
            LOGGER.error("Erro ao buscar usuário com ID '{}': ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro inesperado ao buscar usuário.", 500));
        }
    }

    /**
     * Atualiza o status do usuário (ativo, inativo, bloqueado).
     */
    @PatchMapping("/{userId}/status")
    public ResponseEntity<GlobalResponse<String>> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam String status,
            HttpServletRequest request) {
        try {
            GlobalResponse<String> response = userService.updateUserStatus(userId, status);
    
            setRequestAttributes(request, "USER_MANAGEMENT", "User status updated",
                    HttpStatus.OK.value(), "{\"userId\":" + userId + ",\"status\":\"" + status + "\"}");
    
            // 🔹 Corrigindo: Agora usa response.getCode() em vez de response.getStatus()
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (RuntimeException e) {
            LOGGER.error("Erro ao atualizar status do usuário '{}': ", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao atualizar status do usuário '{}': ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro inesperado ao atualizar status do usuário.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }    

    /**
     * Reseta as tentativas de login do usuário.
     */
    @PatchMapping("/{userId}/reset-login-attempts")
    public ResponseEntity<GlobalResponse<String>> resetLoginAttempts(
            @PathVariable Long userId,
            HttpServletRequest request) {
        try {
            GlobalResponse<String> response = userService.resetLoginAttempts(userId);
            setRequestAttributes(request, "USER_MANAGEMENT", "User login attempts reset",
                    HttpStatus.OK.value(), "{\"userId\":" + userId + "}");
    
            // 🔹 Usando response.getCode() corretamente
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (RuntimeException e) {
            LOGGER.error("Erro ao resetar tentativas de login do usuário '{}': ", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao resetar tentativas de login do usuário '{}': ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro inesperado ao resetar tentativas de login.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }     

    /**
     * Atualiza a data do último acesso do usuário.
     */
    @PatchMapping("/{userId}/update-last-access")
    public ResponseEntity<GlobalResponse<String>> updateLastAccess(
            @PathVariable Long userId,
            HttpServletRequest request) {
        try {
            GlobalResponse<String> response = userService.updateLastAccess(userId);
            setRequestAttributes(request, "USER_MANAGEMENT", "User last access updated",
                    HttpStatus.OK.value(), "{\"userId\":" + userId + "}");
    
            // 🔹 Usando response.getCode() corretamente
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (RuntimeException e) {
            LOGGER.error("Erro ao atualizar último acesso do usuário '{}': ", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GlobalResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao atualizar último acesso do usuário '{}': ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro inesperado ao atualizar último acesso.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }    

    /**
     * Configura atributos de requisição para logs detalhados.
     */

    private void setRequestAttributes(HttpServletRequest request, String logType, String event, int status, String payload) {
        request.setAttribute("logType", logType);
        request.setAttribute("event", event);
        request.setAttribute("responseStatus", status);
        request.setAttribute("payload", payload);
    }
}