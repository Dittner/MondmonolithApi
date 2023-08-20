package de.mondmonolith.api.controller.dto;

public class UserDto {
    public Long id;
    public String name;
    public String role;
    public String token;

    public UserDto(Long id, String name, String role, String token) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.token = token;
    }
}
