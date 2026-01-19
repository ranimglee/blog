package com.blog.afaq.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        // Allow cookies / Authorization headers
        config.setAllowCredentials(true);

        // Allowed origins (use patterns because credentials = true)
        config.setAllowedOriginPatterns(Arrays.asList(
                "https://efaq.netlify.app",
                "https://efaqadmin.netlify.app",
                "https://blog-m2jm.onrender.com",
                "http://localhost:8081",
                "http://localhost:8082",
                "http://51.75.200.76",
		"http://51.75.200.76:81",
                "http://localhost:8080"  ,
                "https://dashboard.afaqgulfcoop.com",
                "https://afaqgulfcoop.com"
        ));

        // Allowed HTTP methods
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        // Allowed request headers
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.ORIGIN,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpHeaders.AUTHORIZATION,
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Headers exposed to the client
        config.setExposedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                "Content-Disposition"
        ));

        // Apply CORS config to all endpoints
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}

