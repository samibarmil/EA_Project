package com.eaProject.demo.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;

@Service
public class JWTUtil {

    @Value("${jwt-secret-key}")
    private String SECRET_KEY;
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateUserToke(UserDetails userDetails) {
        return Jwts.builder()
                     .setSubject(userDetails.getUsername())
                     .claim("authorities", userDetails.getAuthorities())
                     .setExpiration(Date.valueOf(LocalDate.now().plusWeeks(2)))
                     .signWith(key, SignatureAlgorithm.HS256)
                     .compact();
     }

    public Boolean isValid(String token, UserDetails userDetails) {
         final String username = extractUsername(token);
         return (username.equals(userDetails.getUsername()) && !isExpired(token));
    }

    public java.util.Date extractExpirationDate(String token) {
         return Jwts.parserBuilder()
                 .setSigningKey(key)
                 .build()
                 .parseClaimsJws(token)
                 .getBody()
                 .getExpiration();
    }

    public boolean isExpired(String token) {
        return  extractExpirationDate(token)
                .before(new java.util.Date());
     }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
     }
}
