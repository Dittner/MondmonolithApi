package de.mondmonolith.api.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.mondmonolith.api.model.User;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;


public class JwtTokenPublisher {
    public static final long TOKEN_EXPIRES_AFTER_SEC = 15 * 24 * 60 * 60;

    public static String generateToken(User user) throws JwtException {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .claim("userId", user.getId())
                    .expirationTime(Date.from(Instant.now().plusSeconds(TOKEN_EXPIRES_AFTER_SEC)))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            JWSSigner signer = new MACSigner(user.getTokenSecret());
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            System.err.println("Token generating is failed: " + e.getMessage());
            throw new JwtException("Token generating is failed");
        }
    }

    public static String readUsername(String token) throws JwtException {
        try {
            System.out.println("JWT-token parsing...");
            final SignedJWT parsedToken = SignedJWT.parse(token);

            JWTClaimsSet claims = parsedToken.getJWTClaimsSet();

            return claims.getSubject();
        } catch (Exception e) {
            System.err.println("Error by reading username from token: " + e.getMessage());
            throw new JwtException("Reading username from token is failed");
        }
    }

    public static Boolean validateToken(String token, User user) throws JwtException {
        try {
            System.out.println("JWT-token parsing...");
            final SignedJWT parsedToken = SignedJWT.parse(token);

            JWTClaimsSet claims = parsedToken.getJWTClaimsSet();

            final String subject = claims.getSubject();
            System.out.println("JWT-token subject: " + subject);
            if (!Objects.equals(user.getUsername(), subject)) {
                throw new ParseException("Username mismatched", -1);
            }

            final Date expirationTime = claims.getExpirationTime();
            System.out.println("JWT-token expirationTime: " + expirationTime);
            if (isTokenExpired(expirationTime)) {
                throw new ParseException("Token is expired" , -1);
            }

            System.out.println("Parsed JWT-token Verifying...");

            JWSVerifier verifier = new MACVerifier(user.getTokenSecret());
            return parsedToken.verify(verifier);
        } catch (ParseException e) {
            System.err.println("Error by parsing token: " + e.getMessage());
            throw new JwtException(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error by validating token: " + e.getMessage());
            throw new JwtException("Token validation is failed");
        }

    }

    private static Boolean isTokenExpired(Date tokenExpirationTime) {
        return tokenExpirationTime.before(new Date());
    }

    public static byte[] generateRandomTokenSecret() {
        Random random = new Random();
        byte[] tokenSecret = new byte[256];
        random.nextBytes(tokenSecret);
        return tokenSecret;
    }
}
