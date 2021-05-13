package com.eaProject.demo.util;

import com.eaProject.demo.exceptions.UnauthorizedAccessException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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


    private  SecretKey getKey (String secretKey) {
        return  Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateUserToke(UserDetails userDetails) {
        System.out.println(SECRET_KEY);
        return Jwts.builder()
                     .setSubject(userDetails.getUsername())
                     .claim("authorities", userDetails.getAuthorities())
                     .setExpiration(Date.valueOf(LocalDate.now().plusWeeks(2)))
                     .signWith(getKey(SECRET_KEY), SignatureAlgorithm.HS256)
                     .compact();
     }

    public java.util.Date extractExpirationDate(String token) {
         return Jwts.parserBuilder()
                 .setSigningKey(getKey(SECRET_KEY))
                 .build()
                 .parseClaimsJws(token)
                 .getBody()
                 .getExpiration();
    }

    public boolean isExpired(String token) {
        return  extractExpirationDate(token)
                .before(new java.util.Date());
     }

    public String extractUsername(String token) throws UnauthorizedAccessException {
       try {
           return Jwts.parserBuilder()
                   .setSigningKey(getKey(SECRET_KEY))
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
       } catch (Exception exception) {
           throw new UnauthorizedAccessException(exception.getMessage());
       }
     }
}
