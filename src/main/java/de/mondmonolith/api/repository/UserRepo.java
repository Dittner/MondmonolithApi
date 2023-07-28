package de.mondmonolith.api.repository;

import de.mondmonolith.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/*
* We can use JpaRepository’s methods: save(), findOne(), findById(), findAll(), count(), delete(), deleteById()...
* without implementing these methods.
* */

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}