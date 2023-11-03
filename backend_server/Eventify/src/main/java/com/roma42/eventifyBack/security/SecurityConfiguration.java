package com.roma42.eventifyBack.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private UserEnableFilter userEnableFilter;
    @Autowired
    private RequestThrottleFilter requestThrottleFilter;
    @Value("${jwt.public.key}")
    private RSAPublicKey pub;
    @Value("${jwt.private.key}")
    private RSAPrivateKey prv;

    private final String[] publicApis = {
            "/api/v1/event/all",
            "/api/v1/credential/signUp",
            "/api/v1/credential/login",
            "/api/v1/credential/upload",
            "/api/v1/credential/verify",
            "/api/v1/credential/refresh",
            "/api/v1/event/image"
    };
    private final String[] privateApis = {
            "/api/v1/credential/logout",
            "/api/v1/category/all",
            "/api/v1/event",
            "/api/v1/event/registered",
            "/api/v1/event/owned",
            "/api/v1/event/imminent",
            "/api/v1/event/add",
            "/api/v1/event/upload",
            "/api/v1/event/edit",
            "/api/v1/event/categories/edit",
            "/api/v1/event/delete",
            "/api/v1/user",
            "/api/v1/user/image",
            "/api/v1/user/update",
            "/api/v1/user/upload",
            "/api/v1/user/register",
            "/api/v1/user/unregister",
            "/api/v1/user/notification",
            "/api/v1/map"
    };
    private final String[] adminApis = {
            "/api/v1/category/add",
            "/api/v1/category/update",
            "/api/v1/category/delete",
            "/api/v1/user/all",
            "/api/v1/user/delete",
            "/api/v1/user/block",
            "/api/v1/user/unblock"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(publicApis).permitAll()
                        .requestMatchers(privateApis).hasAnyAuthority("SCOPE_ADMIN", "SCOPE_USER")
                        .requestMatchers(adminApis).hasAuthority("SCOPE_ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                )
                .addFilterAfter(this.userEnableFilter, AuthorizationFilter.class)
                .addFilterBefore(this.requestThrottleFilter, DisableEncodeUrlFilter.class);
        return http.build();
    }
    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.pub).build();
    }
    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.pub).privateKey(this.prv).build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }
}
