package de.mondmonolith.api.controller.dto;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class Response extends ResponseEntity<Object> {
    public Response(Object body, HttpStatusCode status) {
        super(body, status);
    }

    public Response(String error, HttpStatusCode status) {
        super(error, status);
    }

    public Response(HttpStatusCode status) {
        super(status);
    }
}