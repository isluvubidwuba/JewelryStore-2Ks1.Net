package com.ks1dotnet.jewelrystore.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class CustomSecurityFilter {
    @Autowired
    CustomUserDetailService customUserDetailService;

    @Autowired
    CustomJwtFilter customJwtFilter;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/authentication/**", "/proxy").permitAll()
                        .requestMatchers("/promotion/files/**").permitAll()
                        .requestMatchers("/policy/listpolicy", "/voucher/list")
                        .hasAnyAuthority("ADMIN", "MANAGER", "STAFF")
                        .requestMatchers("/policy/**").hasAuthority("ADMIN")
                        .requestMatchers("/promotion/getHomePagePromotion**", "/promotion/getById",
                                "/promotion-for-product/promotion/**", "/promotion-for-product/not-in-promotion/**",
                                "/voucher/list", "/voucher/*/categories")
                        .hasAnyAuthority("STAFF", "ADMIN", "MANAGER")
                        .requestMatchers("/promotion/**", "/promotion-for-product/**", "/voucher/**")
                        .hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}