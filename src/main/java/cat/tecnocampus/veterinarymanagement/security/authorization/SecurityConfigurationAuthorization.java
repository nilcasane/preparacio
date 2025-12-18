package cat.tecnocampus.veterinarymanagement.security.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasAnyScope;
import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurationAuthorization {
    private final JwtDecoder jwtDecoder;
    private static final String[] WHITE_LIST_URL = {
            "/loginJWT",
            "/h2-console/**",
            "/webjars/**",
            "/v3/api-docs/**", //this is for swagger
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/openapi.yaml"};

    public SecurityConfigurationAuthorization(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable()) //This is to disable the csrf protection. It is not needed for this project since the application is stateless (and we are using JWT)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions
                        .sameOrigin()))   // This is to allow the h2-console to be used in the browser. It allows the browser to render the response in a frame.
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(WHITE_LIST_URL).permitAll();

                    // Veterinarian availability management - RECEPTIONIST only
                    auth.requestMatchers("/veterinarians/*/availabilities/**").access(hasAnyScope("RECEPTIONIST", "CLINIC_MANAGER"));

                    // Veterinarian schedule - VETERINARIAN or RECEPTIONIST
                    auth.requestMatchers("/veterinarians/*/schedule").access(hasAnyScope("VETERINARIAN", "RECEPTIONIST", "CLINIC_MANAGER"));

                    // Availability management - RECEPTIONIST only
                    auth.requestMatchers("/availabilities/**").access(hasAnyScope("RECEPTIONIST", "CLINIC_MANAGER"));

                    // Exception management - RECEPTIONIST only
                    auth.requestMatchers("/exceptions/**").access(hasAnyScope("RECEPTIONIST", "CLINIC_MANAGER"));

                    // Visit creation - RECEPTIONIST or PET_OWNER
                    auth.requestMatchers(HttpMethod.POST, "/visits").access(hasAnyScope("RECEPTIONIST", "PET_OWNER", "CLINIC_MANAGER"));

                    // Visit management (GET, PUT, DELETE) - RECEPTIONIST or VETERINARIAN
                    auth.requestMatchers(HttpMethod.GET, "/visits/*").access(hasAnyScope("RECEPTIONIST", "VETERINARIAN", "CLINIC_MANAGER"));
                    auth.requestMatchers(HttpMethod.PUT, "/visits/*").access(hasAnyScope("RECEPTIONIST", "VETERINARIAN", "CLINIC_MANAGER"));
                    auth.requestMatchers(HttpMethod.DELETE, "/visits/*").access(hasAnyScope("RECEPTIONIST", "VETERINARIAN", "CLINIC_MANAGER"));

                    // Visit operations - VETERINARIAN only
                    auth.requestMatchers(   "/visits/*/start",
                                            "/visits/*/complete",
                                            "/visits/*/prescriptions/**",
                                            "/visits/*/treatments/**",
                                            "/visits/*/owner-not-showed-up"
                    ).access(hasAnyScope("VETERINARIAN", "CLINIC_MANAGER"));

                    // Visit rescheduling and cancellation - RECEPTIONIST, VETERINARIAN or PET_OWNER
                    auth.requestMatchers("/visits/*/reschedule", "/visits/*/cancel").access(hasAnyScope("RECEPTIONIST", "VETERINARIAN", "PET_OWNER", "CLINIC_MANAGER"));

                    // Walk-in visits - RECEPTIONIST only
                    auth.requestMatchers("/visits/walk-in").access(hasAnyScope("RECEPTIONIST", "CLINIC_MANAGER"));

                    // Medication management & Low stock alerts - CLINIC_MANAGER only
                    auth.requestMatchers("/medications/**",
                            "/medicationBatches/**",
                            "/low-stock-alerts/**"
                    ).access(hasScope("CLINIC_MANAGER"));

                    // Promotions and discounts - CLINIC_MANAGER only
                    auth.requestMatchers("/promotions/**",
                            "/discounts/**",
                            "/loyaltyTiers/**"
                    ).access(hasScope("CLINIC_MANAGER"));

                    // Invoice management - RECEPTIONIST or CLINIC_MANAGER
                    auth.requestMatchers("/invoices/**").access(hasAnyScope("RECEPTIONIST", "CLINIC_MANAGER"));

                    auth.anyRequest().authenticated();
                })
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((oauth2) -> oauth2.jwt((jwt) -> jwt.decoder(jwtDecoder)))
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
