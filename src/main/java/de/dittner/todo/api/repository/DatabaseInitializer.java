package de.dittner.todo.api.repository;

import de.dittner.todo.api.model.User;
import de.dittner.todo.api.security.SecurityConfig;
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
                //admin@mycompany.com:pwd => Basic YWRtaW5AbXljb21wYW55LmNvbTpwd2Q=
                new User("admin@mycompany.com", encoder.encode("pwd"), SecurityConfig.ADMIN),
                //user@mycompany.com:pwd => Basic dXNlckBteWNvbXBhbnkuY29tOnB3ZA==
                new User("user@mycompany.com", encoder.encode("pwd"), SecurityConfig.USER)
        );
        defUsers.forEach(userRepo::save);
        log.info("Database initialized");
    }
}