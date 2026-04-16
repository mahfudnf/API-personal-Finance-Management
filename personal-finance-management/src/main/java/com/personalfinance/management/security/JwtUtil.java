package com.personalfinance.management.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SCRET = "mySecretKeyMySecretKeyMySecretKey123456"; // minimal 32 char
    private final long EXPIRATION =  (1000*60*60); // 1 Jam

    private Key getSignKey(){
        return Keys.hmacShaKeyFor(SCRET.getBytes());
    }

    public String generateToken(String name){
        return Jwts.builder()
                .setSubject(name)
                .setIssuedAt(new Date())
                .setExpiration(new Date((System.currentTimeMillis()+EXPIRATION)))
                .signWith(getSignKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String name, long expirationMillis){
        return Jwts.builder()
                .setSubject(name)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
           return false;
        }
    }

    public Long getExpirationTime(){
        return System.currentTimeMillis() + EXPIRATION;
    }

}
