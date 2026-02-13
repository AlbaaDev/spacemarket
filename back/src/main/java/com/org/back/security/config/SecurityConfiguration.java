package com.org.back.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.org.back.security.jwt.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final AuthenticationProvider authenticationProvider;

        public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
                        AuthenticationProvider authenticationProvider) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.authenticationProvider = authenticationProvider;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
                requestHandler.setCsrfRequestAttributeName(null);

                http
                                .sessionManagement(sm -> sm
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/auth/login", "/auth/register",
                                                                "/h2-console/**")
                                                .csrfTokenRepository(
                                                                CookieCsrfTokenRepository.withHttpOnlyFalse())
                                                .csrfTokenRequestHandler(requestHandler))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register",
                                                                "/h2-console/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/auth/csrf", "/h2-console/**",
                                                                "/hello")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.PUT, "/h2-console/**").permitAll()
                                                .requestMatchers(HttpMethod.DELETE, "/h2-console/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/auth/logout/**").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/contacts/**").authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/contacts/**").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/contacts/**").authenticated()
                                                .requestMatchers(HttpMethod.POST, "/contacts/**").authenticated()
                                                .anyRequest().authenticated())
                                .logout(logout -> logout
                                                .logoutUrl("/auth/logout")
                                                .deleteCookies("jwt")
                                                .logoutSuccessHandler((_, res, _) -> res
                                                                .setStatus(HttpServletResponse.SC_OK)))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                // Allow H2 console to be accessed without authentication + Clickjacking
                                // protection
                                .headers(h -> h
                                                .frameOptions(fo -> fo.sameOrigin()));

                return http.build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("https://black-moss-027062103.1.azurestaticapps.net", "http://localhost:4200"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(
                                List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "X-XSRF-TOKEN"));
                configuration.setAllowCredentials(true);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
