package com.ks1dotnet.jewelrystore.utils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.ks1dotnet.jewelrystore.Enum.TokenType;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.repository.IInvalidatedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
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
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));
            Claims tokenClaim =
                    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            if (!iInvalidatedTokenRepository.existsById(tokenClaim.getId())) {
                return tokenClaim;
            } else
                log.info("User already logout!");
        } catch (JwtException | IllegalArgumentException e) {
            handleJwtException(e);
        } catch (Exception e) {
            log.info("Exception at verifyToken JwtUtilsHelper: " + e.getMessage());
        }
        return null;
    }



    private void handleJwtException(Exception e) {
        if (e instanceof ExpiredJwtException) {
            log.info("User has expired");
        } else if (e instanceof UnsupportedJwtException) {
            log.info("User is unsupported");
        } else if (e instanceof MalformedJwtException) {
            log.info("User is malformed");
        } else if (e instanceof SignatureException) {
            log.info("User signature does not match");
        } else if (e instanceof IllegalArgumentException) {
            log.info("User token is null or empty");
        } else {
            log.info("User verification failed");
        }
    }

    public static Claims getAuthorizationByTokenType(String tokenType) {
        Authentication context = SecurityContextHolder.getContext().getAuthentication();
        if (context == null || !context.isAuthenticated()
                || "anonymousUser".equals(context.getPrincipal())) {
            throw new ApplicationException("User not authenticated!", HttpStatus.UNAUTHORIZED);
        }
        Map<String, Claims> claimsMap = (Map<String, Claims>) context.getCredentials();
        return claimsMap.get(tokenType);
    }

}
