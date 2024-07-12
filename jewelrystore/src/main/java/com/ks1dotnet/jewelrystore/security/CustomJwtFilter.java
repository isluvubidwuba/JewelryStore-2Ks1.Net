package com.ks1dotnet.jewelrystore.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomJwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtilsHelper jwtUtilsHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {

            String tokenAT = getTokenFromHeader(request);
            String tokenRT = getRefreshTokenFromCookies(request);
            log.info("At doFilterInternal CustomJwtFilter");
            Claims claimsAT = getClaimsFromToken(tokenAT, "Access token");
            Claims claimsRT = getClaimsFromToken(tokenRT, "Refresh token");


            Map<String, Claims> mapClaims = new HashMap<>();
            List<SimpleGrantedAuthority> listAuthority = new ArrayList<>();

            if (claimsAT != null) {
                mapClaims.put("at", claimsAT);
                listAuthority.add(new SimpleGrantedAuthority(claimsAT.get("role", String.class)));
                listAuthority
                        .add(new SimpleGrantedAuthority(claimsAT.get("token_type", String.class)));
            }

            if (claimsRT != null) {
                mapClaims.put("rt", claimsRT);
                listAuthority
                        .add(new SimpleGrantedAuthority(claimsRT.get("token_type", String.class)));
            }

            registerAuthorization(
                    claimsAT != null ? claimsAT.getSubject()
                            : (claimsRT != null ? claimsRT.getSubject() : null),
                    mapClaims, listAuthority, request, response, filterChain);
        } catch (Exception e) {
            log.error("Exception at doFilterInternal CustomeJwtFilter: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.getWriter()
                    .write(new ObjectMapper()
                            .writeValueAsString(new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR,
                                    "Something wrong at while authorize!", null)));
        }
    }

    private Claims getClaimsFromToken(String token, String tokenType) {
        Object tokenObject = jwtUtilsHelper.verifyToken(token);
        if (tokenObject != null && tokenObject instanceof String) {
            log.info(tokenType + ": " + (String) tokenObject);
        }
        return (tokenObject instanceof Claims) ? (Claims) tokenObject : null;
    }


    private String getTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (StringUtils.hasText(header) && header.startsWith("Bearer ")) ? header.substring(7)
                : null;
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("rt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    private void registerAuthorization(String subject, Map<String, Claims> mapClaims,
            List<SimpleGrantedAuthority> listAuthority, HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(subject, mapClaims, listAuthority);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
