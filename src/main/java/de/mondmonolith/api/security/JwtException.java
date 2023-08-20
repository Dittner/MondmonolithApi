package de.mondmonolith.api.security;

import java.io.IOException;

public class JwtException extends IOException {
    public JwtException(String errorMessage) {
        super(errorMessage);
    }
}