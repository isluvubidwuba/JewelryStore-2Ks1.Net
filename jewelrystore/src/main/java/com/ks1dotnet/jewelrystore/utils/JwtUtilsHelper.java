package com.ks1dotnet.jewelrystore.utils;

import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.ks1dotnet.jewelrystore.Enum.TokenType;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.repository.IInvalidatedTokenRepository;
import ch.qos.logback.core.subst.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtilsHelper {

    @Value("${jwt.privateKey}")
    private String privateKey;
    @Autowired
    private IInvalidatedTokenRepository iInvalidatedTokenRepository;

    private Date getIssueDate() {
        long currentTimeMillis = System.currentTimeMillis();
        return new Date(currentTimeMillis);
    }

    private Date getExpirationDate(int minute) {
        long currentTimeMillis = System.currentTimeMillis();
        return new Date(currentTimeMillis + minute * 60 * 1000);
    }

    public String generateToken(String idEmployee, String role, int minute, TokenType type) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
        return Jwts.builder().setSubject(idEmployee).setId(UUID.randomUUID().toString())
                .claim("role", role).setIssuedAt(getIssueDate())
                .setExpiration(getExpirationDate(minute)).claim("token_type", type).signWith(key)
                .compact();
    }


    public Claims verifyToken(String token) {
        try {
            if (token.startsWith("Bearer "))
                token = token.split(" ")[1];
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
            Claims tokenClaim =
                    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            if (iInvalidatedTokenRepository.existsById(tokenClaim.getId()))
                throw new ApplicationException("JWT invalid!", HttpStatus.UNAUTHORIZED);
            return tokenClaim;
        } catch (ApplicationException e) {
            throw new ApplicationException("Error at verifyToken JwtUtilsHelper: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (JwtException | IllegalArgumentException e) {
            handleJwtException(e);
        } catch (Exception e) {
            throw new ApplicationException("Error at verifyToken JwtUtilsHelper: " + e.getMessage(),
                    "JWT verification failed", HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    private void handleJwtException(Exception e) {
        if (e instanceof ExpiredJwtException) {
            throw new ApplicationException("JWT has expired", HttpStatus.UNAUTHORIZED);
        } else if (e instanceof UnsupportedJwtException) {
            throw new ApplicationException("JWT is unsupported", HttpStatus.UNAUTHORIZED);
        } else if (e instanceof MalformedJwtException) {
            throw new ApplicationException("JWT is malformed", HttpStatus.UNAUTHORIZED);
        } else if (e instanceof SignatureException) {
            throw new ApplicationException("JWT signature does not match", HttpStatus.UNAUTHORIZED);
        } else if (e instanceof IllegalArgumentException) {
            throw new ApplicationException("JWT token is null or empty", HttpStatus.UNAUTHORIZED);
        } else {
            throw new ApplicationException("JWT verification failed", HttpStatus.UNAUTHORIZED);
        }
    }


}
