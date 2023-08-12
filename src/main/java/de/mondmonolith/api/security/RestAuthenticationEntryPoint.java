package de.mondmonolith.api.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("restAuthenticationEntryPoint")
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {
        //response.setHeader("WWW-Authenticate", "Basic realm=\"Realm\"");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type, Accept");
        response.setContentType("application/json");
        if(authenticationException instanceof BadCredentialsException)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        else if(authenticationException instanceof InsufficientAuthenticationException)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        else
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //InternalAuthenticationServiceException

        System.out.println(authenticationException.toString());
        System.out.println(authenticationException.getMessage());
        System.out.println("Response status: " + response.getStatus());
        response.getOutputStream().println("{ \"error\": \"" + authenticationException.getMessage() + "\" }");
    }
}