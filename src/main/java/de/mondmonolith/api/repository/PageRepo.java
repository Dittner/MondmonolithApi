package de.mondmonolith.api.repository;

import de.mondmonolith.api.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepo extends JpaRepository<Page, Long> {
    List<Page> findAllByDocId(Long docId);
    void deleteAllByDocId(Long docId);
    void deleteAllByUserId(Long userId);
}