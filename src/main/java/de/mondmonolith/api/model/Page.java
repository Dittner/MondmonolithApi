package de.mondmonolith.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "pages")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @Column
    private Long docId;

    @Column(name = "title", length = 100)
    private String title;

    @Column
    private String[] blocks;

    public Page(Long userId, Long docId, String title, String[] blocks) {
        this.userId = userId;
        this.docId = docId;
        this.title = title;
        this.blocks = blocks;
    }
}