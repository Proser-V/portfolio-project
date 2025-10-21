package com.atelierlocal.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.atelierlocal.security.CustomUserDetailsService;
import com.atelierlocal.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public Argon2PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authConfig) throws Exception {
            return authConfig.getAuthenticationManager();
        }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, 
                                     JwtAuthenticationFilter jwtFilter,
                                     CustomUserDetailsService userDetailsService,
                                     Argon2PasswordEncoder passwordEncoder) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .cors(c -> c.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/home", "/", "/api/users/logout", "/api/users/login",
            "/api/clients/register", "/api/clients/{id}", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
            "/api/artisans/register", "/api/artisans/", "/api/artisans/{id}", "/swagger-resources/**", "/webjars/**", "/api/artisans/random-top",
            "/api/artisan-category/**", "/api/geocode/**", "/api/avatar/upload", "/api/event-categories/",
            "/api/event-categories/{id}", "/api/event-categories/{id}/artisan-categories", "/api/askings/creation",
            "/api/artisans/{id}/portfolio/upload", "/api/artisans/{id}/portfolio/delete", "/api/artisan-category/{id}/askings",
            "/api/artisan-category/{id}").permitAll()
            .anyRequest().authenticated()
        )
        .userDetailsService(userDetailsService)
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(logout -> logout
            .logoutUrl("/api/users/logout")
            .clearAuthentication(true)
            .deleteCookies("jwt")
            .logoutSuccessHandler((request, response, authentication) -> {
                response.setStatus(HttpServletResponse.SC_OK);
            })
        )
        .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("http://localhost:3000"));
        config.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
