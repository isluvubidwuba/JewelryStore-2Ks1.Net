package com.ks1dotnet.jewelrystore.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;

import org.springframework.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomJwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtilsHelper jwtUtilsHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getTokenFromHeader(request);
        if (token != null) {
            if (jwtUtilsHelper.verifyToken(token)) {
                String role = jwtUtilsHelper.getRoleFromToken(token);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        "", "", List.of(new SimpleGrantedAuthority(role)));
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }
        return token;
    }

}
