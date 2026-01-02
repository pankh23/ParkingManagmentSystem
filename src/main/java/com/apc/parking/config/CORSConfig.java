package com.apc.parking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                // Get allowed origins from environment variable or use defaults
                String allowedOrigins = System.getenv("ALLOWED_ORIGINS");
                String[] origins;
                
                if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
                    // Split by comma and trim whitespace
                    origins = allowedOrigins.split(",");
                    for (int i = 0; i < origins.length; i++) {
                        origins[i] = origins[i].trim();
                    }
                } else {
                    // Default to localhost for development
                    origins = new String[]{"http://localhost:3000", "http://localhost:3001"};
                }
                
                registry.addMapping("/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
