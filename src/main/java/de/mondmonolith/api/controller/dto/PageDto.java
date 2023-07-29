package de.mondmonolith.api.controller.dto;

public class PageDto {
    public Long id;
    public Long docId;
    public String title;
    public String[] blocks;

    public PageDto(Long id, Long docId, String title, String[] blocks) {
        this.id = id;
        this.docId = docId;
        this.title = title;
        this.blocks = blocks;
    }
}
