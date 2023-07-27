package de.dittner.todo.api.controller.dto;

public class UserDto {
    public Long id;
    public String name;
    public String role;

    public UserDto(Long id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}
