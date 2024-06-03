package com.ks1dotnet.jewelrystore.utils;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtilsHelper {

    @Value("${jwt.privateKey}")
    private String privateKey;

    public String generateToken(String data, String role) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
        String jwt = Jwts.builder().setSubject(data).claim("role", role).signWith(key).compact();
        return jwt;
    }

    public boolean verifyToken(String token) {

        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parse(token);
            return true;
        } catch (Exception e) {
            System.out.println("Not have permission " + e.getMessage());
            return false;
        }
    }

    public String getRoleFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

}
