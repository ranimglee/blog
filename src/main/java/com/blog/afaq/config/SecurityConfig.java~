package com.blog.afaq.config;

import com.blog.afaq.security.JwtAuthenticationFilter;
import com.blog.afaq.security.JwtTokenProvider;
import com.blog.afaq.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    public SecurityConfig(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpStatus.UNAUTHORIZED.value());
                            res.setContentType("application/json");
                            res.getWriter().write("{\"message\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpStatus.FORBIDDEN.value());
                            res.setContentType("application/json");
                            res.getWriter().write("{\"message\":\"Forbidden\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth

                        // 🔓 AUTH & PUBLIC
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/api/public/**",
                                "/api/public/newsletter/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/ressources/download/**")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/ressources/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/articles/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/initiatives/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/comments/article/**")
                        .permitAll()

                        .requestMatchers(
                                "/api/comments/pending",
                                "/api/comments/approve/**",
                                "/api/initiatives/create-new-initiative",
                                "/api/initiatives/update-initiative/**",
                                "/api/initiatives/delete-initiative/**",
                                "/api/initiatives/upload-image-initiative",
                                "/api/ressources/upload",
                                "/api/ressources/**",
                                "/api/analytics/**"

                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/api/user/**",
                                "/api/articles/**",
                                "/api/comments"
                        ).authenticated()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
