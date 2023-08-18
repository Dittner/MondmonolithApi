package de.mondmonolith.api.controller;

import de.mondmonolith.api.controller.dto.Response;
import de.mondmonolith.api.controller.dto.UserDto;
import de.mondmonolith.api.model.User;
import de.mondmonolith.api.repository.DirRepo;
import de.mondmonolith.api.repository.DocRepo;
import de.mondmonolith.api.repository.PageRepo;
import de.mondmonolith.api.repository.UserRepo;
import de.mondmonolith.api.security.SecurityConfig;
import de.mondmonolith.api.service.EmailService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
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

    @Autowired
    EmailService emailService;

    Map<String, String> verificationCodeCache = new HashMap<>();

    @PostMapping("signup")
    public Response signUp(@Valid @RequestBody SignUpRequest request) {
        if (request.username.equals("")) {
            return new Response("Email is required", HttpStatus.BAD_REQUEST);
        }

        if (request.verificationCode.equals("")) {
            return new Response("Verification code is required", HttpStatus.BAD_REQUEST);
        }

        EmailValidator validator = new EmailValidator();
        if (!validator.validate(request.username)) {
            return new Response("Email «" + request.username + "» is incorrect", HttpStatus.BAD_REQUEST);
        }

        if (request.password.equals("")) {
            return new Response("User password is required", HttpStatus.BAD_REQUEST);
        }

        if (userRepo.findByUsername(request.username).isPresent()) {
            return new Response("User «" + request.username + "» is already signed up", HttpStatus.CONFLICT);
        }

        if (!verificationCodeCache.containsKey(request.username)) {
            return new Response("Verification code is not generated", HttpStatus.BAD_REQUEST);
        }

        if (!Objects.equals(verificationCodeCache.get(request.username), request.verificationCode)) {
            return new Response("Verification code is incorrect", HttpStatus.BAD_REQUEST);
        }

        User user = new User(request.username, encoder.encode(request.password), SecurityConfig.USER);
        userRepo.save(user);

        verificationCodeCache.remove(request.username);

        return new Response(new UserDto(user.getId(), user.getUsername(), user.getRole()), HttpStatus.CREATED);
    }


    @PostMapping("signup/code")
    public Response generateCode(@Valid @RequestBody SignUpRequest request) {
        if (request.username.equals("")) {
            return new Response("Email is required", HttpStatus.BAD_REQUEST);
        }

        EmailValidator validator = new EmailValidator();
        if (!validator.validate(request.username)) {
            return new Response("Email «" + request.username + "» is incorrect", HttpStatus.BAD_REQUEST);
        }

        if (userRepo.findByUsername(request.username).isPresent()) {
            return new Response("User «" + request.username + "» is already signed up", HttpStatus.CONFLICT);
        }

        log.info("signup/code: Sending email to: " + request.username);
        log.info("signup/code: verificationCodeMap size: " + verificationCodeCache.size());
        try {
            final String code = generateCode();
            verificationCodeCache.put(request.username, code);
            log.info("signup/code: sending code (" + code + ")...");
            emailService.sendVerificationCode(request.username, code);
            return new Response(HttpStatus.CREATED);
        } catch (Exception e) {
            return new Response(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private static final char[] digits = "0123456789".toCharArray();
    private static final Random random = new Random();

    private String generateCode() {
        char[] buf = new char[6];
        for (int i = 0; i < buf.length; ++i)
            buf[i] = digits[random.nextInt(digits.length)];
        return new String(buf);
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
    public String verificationCode;

    public SignUpRequest() {
    }
}

class EmailValidator {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public boolean validate(String email) {
        return email.matches(EMAIL_REGEX);
    }
}