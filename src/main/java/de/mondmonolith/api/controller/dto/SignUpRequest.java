package de.mondmonolith.api.controller.dto;

public class SignUpRequest {
    public String username;
    public String password;
    public String verificationCode;

    public SignUpRequest(String username, String password, String verificationCode) {
        this.username = username;
        this.password = password;
        this.verificationCode = verificationCode;
    }
}
