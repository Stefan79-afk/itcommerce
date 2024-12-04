package com.example.itcommerce.util;

import com.example.itcommerce.exception.UserLoginException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final long expirationTime;

    public JwtUtil(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") long expirationTime) {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        this.expirationTime = expirationTime;
    }

    public String generateToken(String email) throws UserLoginException {
        try {
            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        } catch (InvalidKeyException e) {
            throw new UserLoginException("Failed to sign key", e);
        }
    }
}
