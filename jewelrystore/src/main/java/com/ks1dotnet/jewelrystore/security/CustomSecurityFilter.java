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
                authenticationManagerBuilder.userDetailsService(customUserDetailService)
                                .passwordEncoder(passwordEncoder());

                return authenticationManagerBuilder.build();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.cors(withDefaults())
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers("/authentication/**", "/role/list",
                                                                "/employee/files/**", "/earnpoints/**")
                                                .permitAll()
                                                .requestMatchers("/policy/listpolicy").hasAnyAuthority("STAFF", "ADMIN")

                                                // Employee
                                                .requestMatchers("/employee/listpage", "/employee/search",
                                                                "/employee/listemployee/{id}")
                                                .hasAnyAuthority("ADMIN", "MANAGER")
                                                .requestMatchers("/employee/insert", "/employee/update",
                                                                "/employee/delete/**")
                                                .hasAuthority("ADMIN")
                                                .requestMatchers("/employee/**")
                                                .hasAnyAuthority("ADMIN", "MANAGER")

                                                // Counter
                                                .requestMatchers("/counter/allactivecounter",
                                                                "/counter/listproductsbycounter",
                                                                "/counter/addproductsforcounter",
                                                                "/counter/products/counter1",
                                                                "/counter/products/all", "/counter/product/details",
                                                                "/counter/moveProductsToCounter")
                                                .hasAnyAuthority("MANAGER", "STAFF", "ADMIN")
                                                .requestMatchers("/counter/update", "/counter/inactive",
                                                                "/counter/inactive","/counter/delete/{id}", "/counter/insert")
                                                .hasAuthority("ADMIN")

                                                // Customer Type
                                                .requestMatchers("/customertype/findall")
                                                .hasAnyAuthority("MANAGER", "STAFF", "ADMIN")
                                                .requestMatchers("/customertype/**")
                                                .hasAuthority("ADMIN")

                                                // User Information
                                                .requestMatchers("/userinfo/listcustomer", "/userinfo/listpage",
                                                                "/userinfo/findcustomer/{id}",
                                                                "/userinfo/searchcustomer")
                                                .hasAnyAuthority("STAFF", "MANAGER", "ADMIN")

                                                .requestMatchers("/userinfo/update" ,"/userinfo/insert")
                                                .hasAnyAuthority("STAFF", "MANAGER", "ADMIN")

                                                .requestMatchers("/userinfo/listsupplier",
                                                                "/userinfo/searchsupplier")
                                                .hasAnyAuthority("MANAGER", "ADMIN")

                                                .requestMatchers("/userinfo/**", "/role/insert")
                                                .hasAuthority("ADMIN")

                                                // Policy
                                                .requestMatchers("/policy/**", "/product/**").hasAuthority("ADMIN")
                                                .anyRequest().authenticated())
                                .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}