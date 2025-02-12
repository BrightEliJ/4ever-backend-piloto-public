package tim.field.application.User.dto;

import tim.field.application.User.model.Group;

public class GroupDTO {
    private Long id;
    private String name;
    private String description;

    public GroupDTO(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.description = group.getDescription();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}