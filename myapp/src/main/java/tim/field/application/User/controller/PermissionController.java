package tim.field.application.User.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tim.field.application.User.model.Permission;
import tim.field.application.User.service.PermissionService;
import tim.field.application.exception.UnauthorizedException;
import tim.field.application.security.JwtUtil;
import tim.field.application.util.GlobalResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/permissions")
public class PermissionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionController.class);
    private final PermissionService permissionService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public PermissionController(PermissionService permissionService, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.permissionService = permissionService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    // 🔹 Listar todas as permissões
    @GetMapping
    public ResponseEntity<GlobalResponse<List<Permission>>> getAllPermissions(HttpServletRequest request) {
        try {
            List<Permission> permissions = permissionService.findAll();
            setRequestAttributes(request, "READ", "List all permissions", HttpStatus.OK.value(), null);
            return ResponseEntity.ok(GlobalResponse.success("Permissões carregadas com sucesso.", permissions));
        } catch (Exception e) {
            LOGGER.error("Erro ao buscar permissões", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro ao buscar permissões.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // 🔹 Buscar permissão por ID
    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse<Permission>> getPermissionById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Optional<Permission> permission = permissionService.findById(id);
            if (permission.isPresent()) {
                setRequestAttributes(request, "READ", "Fetch permission by ID", HttpStatus.OK.value(), null);
                return ResponseEntity.ok(GlobalResponse.success("Permissão encontrada.", permission.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(GlobalResponse.error("Permissão não encontrada.", HttpStatus.NOT_FOUND.value()));
            }
        } catch (Exception e) {
            LOGGER.error("Erro ao buscar permissão", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro interno ao buscar permissão.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // 🔹 Criar nova permissão
    @PostMapping
    public ResponseEntity<GlobalResponse<Permission>> createPermission(
            @Valid @RequestBody Permission permission,
            HttpServletRequest request) {
        try {
            Long userId = jwtUtil.extractAndValidateUserId(request);
            LOGGER.info("Usuário {} está criando uma permissão.", userId);

            Permission savedPermission = permissionService.save(permission);
            String permissionJson = objectMapper.writeValueAsString(savedPermission);
            setRequestAttributes(request, "CREATE", "Create permission", HttpStatus.CREATED.value(), permissionJson);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(GlobalResponse.success("Permissão criada com sucesso.", savedPermission));
        } catch (UnauthorizedException e) {
            LOGGER.warn("Acesso não autorizado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalResponse.error("Token inválido ou ausente.", HttpStatus.UNAUTHORIZED.value()));
        } catch (JsonProcessingException e) {
            LOGGER.error("Erro ao serializar permissão para JSON", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro interno ao processar a permissão.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (Exception e) {
            LOGGER.error("Erro ao criar permissão", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro ao criar permissão.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // 🔹 Atualizar permissão
    @PutMapping("/{id}")
    public ResponseEntity<GlobalResponse<Permission>> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody Permission updatedPermission,
            HttpServletRequest request) {
        try {
            Long userId = jwtUtil.extractAndValidateUserId(request);
            LOGGER.info("Usuário {} está atualizando a permissão {}.", userId, id);

            Optional<Permission> existingPermission = permissionService.findById(id);
            if (existingPermission.isPresent()) {
                Permission permission = existingPermission.get();
                permission.setName(updatedPermission.getName());

                Permission savedPermission = permissionService.save(permission);
                String permissionJson = objectMapper.writeValueAsString(savedPermission);
                setRequestAttributes(request, "UPDATE", "Update permission", HttpStatus.OK.value(), permissionJson);

                return ResponseEntity.ok(GlobalResponse.success("Permissão atualizada com sucesso.", savedPermission));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(GlobalResponse.error("Permissão não encontrada.", HttpStatus.NOT_FOUND.value()));
            }
        } catch (UnauthorizedException e) {
            LOGGER.warn("Acesso não autorizado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalResponse.error("Token inválido ou ausente.", HttpStatus.UNAUTHORIZED.value()));
        } catch (JsonProcessingException e) {
            LOGGER.error("Erro ao serializar permissão para JSON", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro interno ao processar a permissão.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (Exception e) {
            LOGGER.error("Erro ao atualizar permissão", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro ao atualizar permissão.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // 🔹 Deletar permissão
    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse<Void>> deletePermission(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = jwtUtil.extractAndValidateUserId(request);
            LOGGER.info("Usuário {} está deletando a permissão {}.", userId, id);

            Optional<Permission> existingPermission = permissionService.findById(id);
            if (existingPermission.isPresent()) {
                permissionService.deleteById(id);
                setRequestAttributes(request, "DELETE", "Delete permission", HttpStatus.NO_CONTENT.value(), null);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(GlobalResponse.error("Permissão não encontrada.", HttpStatus.NOT_FOUND.value()));
            }
        } catch (UnauthorizedException e) {
            LOGGER.warn("Acesso não autorizado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalResponse.error("Token inválido ou ausente.", HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            LOGGER.error("Erro ao deletar permissão", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro ao deletar permissão.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // 🔹 Método auxiliar para configuração de logs/auditoria
    private void setRequestAttributes(HttpServletRequest request, String logType, String event, int status, String payload) {
        request.setAttribute("logType", logType);
        request.setAttribute("event", event);
        request.setAttribute("responseStatus", status);
        request.setAttribute("payload", payload);
    }
}