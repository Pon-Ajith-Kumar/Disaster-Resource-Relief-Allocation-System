package com.disasterrelief.relief_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final DummyAuthFilter dummyAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            // allow authentication endpoints and static resources (GET)
            .requestMatchers(HttpMethod.GET,
                "/",
                "/index.html",
                "/login",
                "/register",
                "/css/**",
                "/js/**",
                "/images/**",
                "/static/**").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
                        // Dashboard should be accessible to any authenticated user (admin, NGO, citizen)
                        .requestMatchers("/api/admin/dashboard").authenticated()
                        // Admin-only endpoints
                        .requestMatchers("/api/admin/allocations").hasRole("ADMIN")
                        .requestMatchers("/api/admin/allocations/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/allocate").hasRole("ADMIN")
                        .requestMatchers("/api/admin/users").hasRole("ADMIN")
                        .requestMatchers("/api/resources/add").hasAnyRole("ADMIN", "NGO")
                        .requestMatchers("/api/requests/add").hasAnyRole("CITIZEN", "NGO")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

    // register dummy auth filter so our demo token becomes an authenticated principal
    http.addFilterBefore(dummyAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}