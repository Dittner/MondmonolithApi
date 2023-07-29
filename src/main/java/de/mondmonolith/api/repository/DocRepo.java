package de.mondmonolith.api.repository;

import de.mondmonolith.api.model.Doc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocRepo extends JpaRepository<Doc, Long> {
    List<Doc> findAllByDirId(Long dirId);
    void deleteAllByUserId(Long userId);
}