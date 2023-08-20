package de.mondmonolith.api.repository;

import de.mondmonolith.api.model.User;
import de.mondmonolith.api.security.JwtTokenPublisher;
import de.mondmonolith.api.security.SecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DatabaseInitializer implements CommandLineRunner {
    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (userRepo.count() != 0) {
            return;
        }

        final List<User> defUsers = Arrays.asList(
                new User("demo", encoder.encode("pwd"), SecurityConfig.USER, JwtTokenPublisher.generateRandomTokenSecret()),
                new User("dev", encoder.encode("pwd"), SecurityConfig.USER, JwtTokenPublisher.generateRandomTokenSecret())
        );
        defUsers.forEach(userRepo::save);
        log.info("Database initialized");
    }
}