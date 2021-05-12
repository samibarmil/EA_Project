package com.eaProject.demo.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class JWTUtil {
    private final String SECRET_KEY = "iW5Y7JQX3sTLRlZe1ndfKvktiQhkeSenj/pHvYscMAQ=";

    // build jwt and returns it
     public String generateUserToke(UserDetails userDetails) {
         return Jwts.builder()
                     .setSubject(userDetails.getUsername())
                     .claim("authorities", userDetails.getAuthorities())
                     .setExpiration(Date.valueOf(LocalDate.now().plusWeeks(2)))
                     .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                     .compact();
     }

    public Boolean isValid(String token, UserDetails userDetails) {
         final String username = extractUsername(token);
         return (username.equals(userDetails.getUsername()) && !isExpired(token));
    }

    public java.util.Date extractExpirationDate(String token) {
         return Jwts.parser()
                 .setSigningKey(SECRET_KEY)
                 .parseClaimsJws(token)
                 .getBody()
                 .getExpiration();
    }

    public boolean isExpired(String token) {
        return  extractExpirationDate(token)
                .before(new java.util.Date());
     }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
     }
}
