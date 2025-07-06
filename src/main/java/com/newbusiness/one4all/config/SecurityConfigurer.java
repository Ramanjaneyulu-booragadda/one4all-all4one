package com.newbusiness.one4all.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SecurityConfigurer {

    @Autowired
    @Lazy
    private final UserDetailsService userDetailsService;
    @Autowired
    private TokenValidationFilter tokenValidationFilter; // 🔥 Autowire instead of defining a Bean manually

    @Autowired
    private Environment env;
    @Value("${cors.allowed.origins:https://localhost:3000}")
    private String allowedOrigins;
    @Value("${cors.allowed.methods}")
    private String allowedMethods;

    @PostConstruct
public void init() {
    
    log.info(">>> CORS ORIGINS from YAML: {}", allowedOrigins);
    log.info(">>> CORS METHODS from YAML: {}", allowedMethods);

}

    public SecurityConfigurer(@Lazy UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Security filter chain configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder, ObjectMapper objectMapper)
            throws Exception {
        String clientID = env.getProperty("microservice.clientid");
        System.out.println("🔴 Loaded microservice.clientid: " + clientID);

        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/api/register", "/api/login", "/api/bulk-register",
                        "/api/admin/register", "/api/admin/login", "/api/reset-password-request",
                        "/api/reset-password/confirm", // <-- Added confirm endpoint
                        "/oauth2/token" // <-- Added to disable CSRF for token endpoint
                ))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/register", "/api/login", "/api/bulk-register",
                                "/api/admin/register", "/api/admin/login", "/api/reset-password-request",
                                "/api/reset-password/confirm", // <-- Added confirm endpoint
                                "/oauth2/token")
                        .permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // 👇 Add CORS headers to error response
                            response.setHeader("Access-Control-Allow-Origin", "*");
                            response.setHeader("Access-Control-Allow-Headers", request.getHeader("Origin"));
                            response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt())
                .addFilterBefore(tokenValidationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Custom CORS configuration source.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Support comma-separated origins from env/properties
        for (String origin : allowedOrigins.split(",")) {
            configuration.addAllowedOrigin(origin.trim());
        }
        for (String methods : allowedMethods.split(",")) {
            configuration.addAllowedMethod(methods.trim());
        }
        configuration.addAllowedHeader("*"); // Allow all headers
        configuration.setAllowCredentials(true); // Allow cookies or authorization headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Password encoder for secure password storage.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * DAO Authentication provider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

}
