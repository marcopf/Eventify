package com.roma42.eventifyBack.models;

import lombok.*;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class Token {
    private static final String ISSUER = "self";
    private String sub;
    private String scope;
    private Long expiry;

    public Token(UserCredential userCredential, Long expiry) {
        this.sub = userCredential.getUsername();
        this.scope = userCredential.getUser().getRolesList().getRole().getRoleName();
        this.expiry = expiry;
    }
    public JwtClaimsSet toClaims() {
        Instant now = Instant.now();
        return JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(this.expiry))
                .subject(this.sub)
                .claim("scope", this.scope)
                .build();
    }
    @Override
    public String toString() {
        return "Token{" +
                "subject='" + sub + '\'' +
                ", scope='" + scope + '\'' +
                ", expiry=" + expiry +
                '}';
    }
}
