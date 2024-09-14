package com.newbusiness.one4all.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Allows CORS for all endpoints
                        .allowedOrigins("http://localhost:3000")  // URL of your React application
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // or use "*" for all methods
                        .allowedHeaders("*")  // or specify headers if needed
                        .allowCredentials(true);
            }
        };
    }
}
