package de.mondmonolith.api.repository;

import de.mondmonolith.api.model.Dir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirRepo extends JpaRepository<Dir, Long> {
    List<Dir> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}