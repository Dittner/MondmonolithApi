package de.mondmonolith.api.security;

import de.mondmonolith.api.model.User;
import de.mondmonolith.api.repository.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    UserRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            logger.info("JwtRequestFilter::doFilterInternal...");

            final String requestTokenHeader = request.getHeader("Authorization");

            if (requestTokenHeader == null) {
                logger.warn("Authorization header not found");
                chain.doFilter(request, response);
                return;
            }
            if (!requestTokenHeader.startsWith("Bearer ")) {
                logger.warn("JWT-token does not begin with Bearer String");
                chain.doFilter(request, response);
                return;
            }

            final String token = requestTokenHeader.substring(7);
            final String username = JwtTokenPublisher.readUsername(token);


            //Once we get the token validate it.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = this.userRepo.findByUsername(username).orElse(null);

                if (JwtTokenPublisher.validateToken(token, user)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // After setting the Authentication in the context, we specify
                    // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    logger.error("Jwt-token validation is complete with negative result");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
        } catch (Exception e) {
            logger.error("=== JwtRequestFilter Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().println(e.getMessage());
            return;
        }

        chain.doFilter(request, response);
    }
}
