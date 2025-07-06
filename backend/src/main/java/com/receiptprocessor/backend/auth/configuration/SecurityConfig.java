package com.receiptprocessor.backend.auth.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;


@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests((authorize) -> authorize
				.anyRequest().authenticated()
			).oauth2ResourceServer((oauth2) -> oauth2.jwt(jwt -> jwt.decoder(JwtDecoders.fromIssuerLocation(issuerUri))));
		return http.build();
    }
}
