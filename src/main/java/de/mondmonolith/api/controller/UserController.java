package de.mondmonolith.api.controller;

import de.mondmonolith.api.controller.dto.Response;
import de.mondmonolith.api.controller.dto.UserDto;
import de.mondmonolith.api.model.User;
import de.mondmonolith.api.repository.DirRepo;
import de.mondmonolith.api.repository.DocRepo;
import de.mondmonolith.api.repository.PageRepo;
import de.mondmonolith.api.repository.UserRepo;
import de.mondmonolith.api.security.SecurityConfig;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/")
public class UserController {
    @Autowired
    UserRepo userRepo;

    @Autowired
    DirRepo dirRepo;

    @Autowired
    DocRepo docRepo;

    @Autowired
    PageRepo pageRepo;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("signup")
    public Response signUp(@Valid @RequestBody SignUpRequest request) {
        if (request.username.equals("")) {
            return new Response("Email is required", HttpStatus.BAD_REQUEST);
        }

        EmailValidator validator = new EmailValidator();
        if (!validator.validate(request.username)) {
            return new Response("The email «" + request.username + "» is not valid", HttpStatus.BAD_REQUEST);
        }

        if (request.password.equals("")) {
            return new Response("User password is required", HttpStatus.BAD_REQUEST);
        }

        if (userRepo.findByUsername(request.username).isPresent()) {
            return new Response("User «" + request.username + "» is already signed up", HttpStatus.CONFLICT);
        }

        User user = new User(request.username, encoder.encode(request.password), SecurityConfig.USER);
        userRepo.save(user);

        return new Response(new UserDto(user.getId(), user.getUsername(), user.getRole()), HttpStatus.CREATED);
    }

    @GetMapping("auth")
    public Response auth(@AuthenticationPrincipal User user) {
        try {
            return new Response(new UserDto(user.getId(), user.getUsername(), user.getRole()), HttpStatus.OK);
        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("users")
    public Response getAllUsers() {
        try {

            List<UserDto> res = new ArrayList<>(userRepo.findAll()
                    .stream()
                    .map(u -> new UserDto(u.getId(), u.getUsername(), u.getRole()))
                    .toList());

            if (res.isEmpty()) {
                return new Response(HttpStatus.NO_CONTENT);
            }

            return new Response(res, HttpStatus.OK);
        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @DeleteMapping("users/{userId}")
    public Response deleteUser(@PathVariable("userId") Long userId, @AuthenticationPrincipal User user) {
        try {
            if (Objects.equals(userId, user.getId())) {
                pageRepo.deleteAllByUserId(userId);
                docRepo.deleteAllByUserId(userId);
                dirRepo.deleteAllByUserId(userId);
                userRepo.deleteById(userId);
            } else {
                return new Response("User not found", HttpStatus.NOT_FOUND);
            }

            return new Response(HttpStatus.OK);

        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

class SignUpRequest {
    public String username;
    public String password;

    public SignUpRequest() {}
}

class EmailValidator {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public boolean validate(String email) {
        return email.matches(EMAIL_REGEX);
    }
}