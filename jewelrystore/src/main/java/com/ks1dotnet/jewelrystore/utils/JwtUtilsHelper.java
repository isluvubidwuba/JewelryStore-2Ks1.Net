package com.ks1dotnet.jewelrystore.utils;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
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

    public String generateToken(String idEmployeeAndOTpCode) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
        long currentTimeMillis = System.currentTimeMillis();
        Date issueDate = new Date(currentTimeMillis);
        Date expirationDate = new Date(currentTimeMillis + 5 * 60 * 1000); // 5 phút

        return Jwts.builder().setSubject(idEmployeeAndOTpCode).setIssuedAt(issueDate)
                .setExpiration(expirationDate).signWith(key).compact();
    }

    public boolean verifyToken(String token) {

        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
            Jwts.parserBuilder().setSigningKey(key).build().parse(token);
            return true;
        } catch (Exception e) {
            System.out.println("Not have permission " + e.getMessage());
            return false;
        }
    }

    public boolean verifyToken(String token, String idEmployeeAndOtpCode) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
            Claims claims =
                    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

            String subject = claims.getSubject();
            Date expiration = claims.getExpiration();

            if (expiration.before(new Date())) {
                return false;
            }

            if (subject.equals(idEmployeeAndOtpCode)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Not have permission " + e.getMessage());
            return false;
        }
    }

    public String getRoleFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
                .get("role", String.class);
    }

    // Lấy id của employee từ token
    public String getEmployeeIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
                .getSubject();
    }
}
