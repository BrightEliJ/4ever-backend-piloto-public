package tim.field.application.security;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MenuItem>> getMenuItems(Authentication authentication) {
        // Obtém as permissões do usuário autenticado
        Set<String> userPermissions = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        // Dashboard SEM restrição de permissão
        MenuItem dashboard = new MenuItem("Dashboard", "dashboard", "/", List.of(), null);

        // Outros itens do menu com permissões específicas
        List<MenuItem> menuItems = List.of(
            new MenuItem("Calendário", "calendar", "/calendar", List.of("USER_PRIVILEGE"), null),
            new MenuItem("Base de Tarefas", "taskBase", "#", List.of("USER_PRIVILEGE"), List.of(
                new MenuItem("Base WFM", "WFMBase", "/TarefasWFM", List.of("USER_PRIVILEGE"), null)

            )),
            new MenuItem("Gestão", "management", "#", List.of("MANAGE_USERS"), List.of(
                new MenuItem("Usuários", "users", "/users-management", List.of("MANAGE_USERS"), null),
                new MenuItem("Permissões", "permissions", "/permissions-management", List.of("MANAGE_USERS"), null),                
                new MenuItem("Grupos", "groups", "/groups-management", List.of("MANAGE_USERS"), null)

            )),
            new MenuItem("Relatórios", "reports", "#", List.of("VIEW_REPORTS"), List.of(
                new MenuItem("Relatório 1", "report1", "/reports/1", List.of("VIEW_REPORTS"), null),
                new MenuItem("Relatório 2", "report2", "/reports/2", List.of("VIEW_REPORTS"), null)
            ))
        );

        // Filtra os itens do menu com base nas permissões do usuário
        List<MenuItem> filteredMenuItems = menuItems.stream()
            .filter(item -> item.hasPermission(userPermissions))
            .collect(Collectors.toList());

        // Adiciona o Dashboard sempre
        filteredMenuItems.add(0, dashboard);

        return ResponseEntity.ok(filteredMenuItems);
    }
}