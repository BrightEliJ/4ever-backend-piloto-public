package tim.field.application.security;

import java.util.List;
import java.util.Set;

public class MenuItem {
    private String title;
    private String icon;
    private String path;
    private List<String> permissions;
    private List<MenuItem> children; // Submenus

    public MenuItem(String title, String icon, String path, List<String> permissions, List<MenuItem> children) {
        this.title = title;
        this.icon = icon;
        this.path = path;
        this.permissions = permissions;
        this.children = children;
    }

    public boolean hasPermission(Set<String> userPermissions) {
        if (permissions.stream().anyMatch(userPermissions::contains)) {
            return true;
        }
        if (children != null) {
            return children.stream().anyMatch(child -> child.hasPermission(userPermissions));
        }
        return false;
    }

    // Getters e Setters
    public String getTitle() { return title; }
    public String getIcon() { return icon; }
    public String getPath() { return path; }
    public List<String> getPermissions() { return permissions; }
    public List<MenuItem> getChildren() { return children; }
}