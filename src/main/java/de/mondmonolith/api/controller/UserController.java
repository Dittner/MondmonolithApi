package de.mondmonolith.api.controller;

import de.mondmonolith.api.controller.dto.UserDto;
import de.mondmonolith.api.model.User;
import de.mondmonolith.api.repository.UserRepo;
import de.mondmonolith.api.security.SecurityConfig;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class UserController {

    @Autowired
    UserRepo repo;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest request) {
        if(request.username.equals("")) {
            return new ResponseEntity<>("An email is required", HttpStatus.BAD_REQUEST);
        }

        EmailValidator validator = new EmailValidator();
        if(!validator.validate(request.username)) {
            return new ResponseEntity<>("The email «" + request.username + "» is not valid", HttpStatus.BAD_REQUEST);
        }

        if(request.password.equals("")) {
            return new ResponseEntity<>("User password is required", HttpStatus.BAD_REQUEST);
        }

        if (repo.findByUsername(request.username).isPresent()) {
            return new ResponseEntity<>("The email «" + request.username + "» is already been used", HttpStatus.CONFLICT);
        }

        User user = new User(request.username, encoder.encode(request.password), SecurityConfig.USER);
        repo.save(user);

        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @GetMapping("auth")
    public ResponseEntity<UserDto> auth(@AuthenticationPrincipal User user) {
        try {
            return new ResponseEntity<>(new UserDto(user.getId(), user.getUsername(), user.getRole()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> res = new ArrayList<UserDto>();

            res.addAll(repo.findAll()
                    .stream()
                    .map(u -> new UserDto(u.getId(), u.getUsername(), u.getRole()))
                    .toList());

            if (res.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

class SignUpRequest {
    public String username;
    public String password;

    public SignUpRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

class EmailValidator {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public boolean validate(String email) {
        return  email.matches(EMAIL_REGEX);
    }
}