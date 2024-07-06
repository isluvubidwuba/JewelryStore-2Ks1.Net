package com.ks1dotnet.jewelrystore.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomJwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtilsHelper jwtUtilsHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String tokenAT = getTokenFromHeader(request);
            String tokenRT = getRefreshTokenFromCookies(request);
            if (tokenAT != null && tokenRT != null) {
                Claims claimsAT = jwtUtilsHelper.verifyToken(tokenAT);
                Claims claimsRT = jwtUtilsHelper.verifyToken(tokenRT);
                String role = claimsAT.get("role", String.class);
                String id = claimsAT.getSubject();
                Map<String, Claims> mapClaims = new HashMap<>();
                mapClaims.put("at", claimsAT);
                mapClaims.put("rt", claimsRT);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(id, mapClaims,
                                List.of(new SimpleGrantedAuthority(role)));
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (ApplicationException e) {
            handleException(response, e);
        } catch (Exception e) {
            handleException(response, new ApplicationException("Internal Server Error",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            token = header.split(" ")[1];
        }
        return token;
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void handleException(HttpServletResponse response, ApplicationException e)
            throws IOException {
        response.setStatus(e.getStatus().value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper()
                .writeValueAsString(new ResponseData(e.getStatus(), e.getErrorString(), null)));
    }
}
