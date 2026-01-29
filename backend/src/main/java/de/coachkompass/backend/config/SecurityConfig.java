package de.coachkompass.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public browse
                        .requestMatchers(HttpMethod.GET,
                                "/api/health",
                                "/api/coaches/**",
                                "/api/sports/**",
                                "/api/specializations/**"
                        ).permitAll()

                        // everything else needs a valid Firebase JWT
                        .anyRequest().authenticated()
                )

                // JWT validation via issuer + jwks from Google
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))

                .build();
    }
}
