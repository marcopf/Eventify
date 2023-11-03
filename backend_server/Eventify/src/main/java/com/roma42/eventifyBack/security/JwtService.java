package com.roma42.eventifyBack.security;

import com.roma42.eventifyBack.models.Token;
import com.roma42.eventifyBack.models.UserCredential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    @Value("${jwt.expiry.user}")
    private Long userExpiry;
    @Value("${jwt.expiry.admin}")
    private Long adminExpiry;
    @Value("${jwt.expiry.refresh}")
    private Long refreshExpiry;
    @Value("${client.ip}")
    private String address;
    @Autowired
    private JwtEncoder encoder;
    @Autowired
    private JwtDecoder decoder;

    public String generateJwt(Token token) {
        return this.encoder.encode(JwtEncoderParameters.from(token.toClaims())).getTokenValue();
    }
    public ResponseCookie generateAccessToken(UserCredential userCredential) {
        return this.generateAccessToken(userCredential.getUsername(),
                userCredential.getUser().getRolesList().getRole().getRoleName());
    }
    public ResponseCookie generateRefreshToken(UserCredential userCredential) {
        Token token = new Token(userCredential, this.refreshExpiry);
        token.setScope("REFRESH");
        String jwt = this.generateJwt(token);
        return ResponseCookie.from("refresh", jwt)
                .secure(true)
                .path("/")
                .maxAge(this.refreshExpiry)
                .domain(this.address)
                .sameSite("Lax")
                .httpOnly(true)
                .build();
    }
    public ResponseCookie generateAccessToken(String username, String role) {
        Long expiry = role.equals("ADMIN") ? this.adminExpiry : this.userExpiry;
        String jwt = this.generateJwt(new Token(username, role, expiry));
        return ResponseCookie.from("authorization", "Bearer%20" + jwt)
                .secure(true)
                .path("/")
                .maxAge(expiry)
//                .domain(this.address)
                .sameSite("Lax")
                .build();
    }

    public String extractClaim(String tokenValue, String claim) {
        if (tokenValue.startsWith("Bearer ")) {
            tokenValue = tokenValue.substring(7);
        }
        Jwt jwt = this.decoder.decode(tokenValue);
        return jwt.getClaim(claim);
    }

    private ResponseCookie generateLogoutToken(String name) {
        return ResponseCookie.from(name, "deleted")
                .maxAge(0)
                .path("/")
                .build();
    }

    public HttpHeaders generateTokens(UserCredential userCredential) {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie authToken = this.generateAccessToken(userCredential);
        headers.add(HttpHeaders.SET_COOKIE, authToken.toString());
        ResponseCookie refreshToken;
        if (userCredential.getUser().getRolesList().getRole().getRoleName().equals("USER"))
            refreshToken = this.generateRefreshToken(userCredential);
        else
            refreshToken = this.generateLogoutToken("refresh");
        headers.add(HttpHeaders.SET_COOKIE, refreshToken.toString());
        return headers;
    }
    public HttpHeaders generateLogoutTokens() {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie cookie = this.generateLogoutToken("authorization");
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        cookie = this.generateLogoutToken("refresh");
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }

    public String getTokenFromCookie(String cookie, String name) {
        int start, end;
        start = cookie.indexOf((name + "=")) + (name.length() + 1);
        end = cookie.indexOf("; ", start);
        if (end < 0 || end >= cookie.length())
            return cookie.substring(start);
        return cookie.substring(start, end);
    }
}
