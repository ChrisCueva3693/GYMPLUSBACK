package com.gymplus.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

        @Bean
        public CorsFilter corsFilter() {
                CorsConfiguration config = new CorsConfiguration();

                // Permitir credenciales
                config.setAllowCredentials(true);

                // Orígenes permitidos
                config.setAllowedOrigins(Arrays.asList(
                                "https://gymplus.srv1070869.hstgr.cloud", // Production Frontend
                                "http://localhost:3000", // Local React
                                "http://localhost:5173", // Local Vite
                                "http://localhost:8080" // Local Backend
                ));

                // Métodos HTTP permitidos
                config.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

                // Headers permitidos
                config.setAllowedHeaders(Arrays.asList(
                                "Origin", "Content-Type", "Accept", "Authorization",
                                "X-Requested-With", "Access-Control-Request-Method",
                                "Access-Control-Request-Headers"));

                // Headers expuestos
                config.setExposedHeaders(Arrays.asList(
                                "Access-Control-Allow-Origin",
                                "Access-Control-Allow-Credentials",
                                "Authorization"));

                // Cache
                config.setMaxAge(3600L);

                // Aplicar a todas las rutas
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);

                return new CorsFilter(source);
        }
}
