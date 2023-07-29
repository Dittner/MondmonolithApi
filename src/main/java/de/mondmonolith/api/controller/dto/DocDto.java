package de.mondmonolith.api.controller.dto;

public class DocDto {
    public Long id;
    public Long dirId;
    public String title;

    public DocDto(Long id, Long dirId, String title) {
        this.id = id;
        this.dirId = dirId;
        this.title = title;
    }
}
