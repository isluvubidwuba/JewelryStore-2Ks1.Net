package com.ks1dotnet.jewelrystore.security;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.annotation.PostConstruct;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class CustomSecurityFilter {
        private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**", "/v2/api-docs",
                        "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources",
                        "/swagger-resources/**", "/configuration/ui", "/configuration/security",
                        "/swagger-ui/**", "/webjars/**", "/swagger-ui.html", "/api/auth/**",
                        "/api/test/**", "/authenticate"};
        @Value("${apiURL}")
        private String apiURL;
        private String[] PUBLIC_API;
        @Autowired
        CustomJwtFilter customJwtFilter;

        @Autowired
        CustomAccessDeniedHandler customAccessDeniedHandler;

        @PostConstruct
        public void init() {
                PUBLIC_API = new String[] {apiURL + "/authentication/**", apiURL + "/mail/**",
                                apiURL + "/material/goldPriceFromSJC"};
        }

        @Bean
        UserDetailsService emptyDetailsService() {
                return username -> {
                        throw new UsernameNotFoundException(
                                        "no local users, only JWT tokens allowed");
                };
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.cors(withDefaults()).csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers(WHITE_LIST_URL).permitAll()
                                                .requestMatchers(PUBLIC_API).permitAll()
                                                .requestMatchers("/api/**")
                                                .hasAuthority("ACCESS_TOKEN").anyRequest()
                                                .permitAll())
                                .exceptionHandling(exception -> exception
                                                .accessDeniedHandler(customAccessDeniedHandler))
                                .addFilterBefore(customJwtFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
