package de.mondmonolith.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "dirs")
public class Dir {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @Column(name = "title", length = 100)
    private String title;

    public Dir(Long userId, String title) {
        this.userId = userId;
        this.title = title;
    }
}