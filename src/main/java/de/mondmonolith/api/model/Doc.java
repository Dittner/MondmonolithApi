package de.mondmonolith.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "docs")
public class Doc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @Column
    private Long dirId;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "public_key", length = 50)
    private String publicKey;

    public Doc(Long userId, Long dirId, String title, String publicKey) {
        this.userId = userId;
        this.dirId = dirId;
        this.title = title;
        this.publicKey = publicKey;
    }
}