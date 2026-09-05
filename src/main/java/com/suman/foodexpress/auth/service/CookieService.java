package com.suman.foodexpress.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class CookieService {

    private final String cookieName;
    private final long refreshTokenExpiration;
    private final String cookiePath;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    public CookieService(
            @Value("${app.jwt.cookie-name}") String cookieName,
            @Value("${app.jwt.refresh-token-expiration}") long refreshTokenExpiration,
            @Value("${app.jwt.cookie-path:/}") String cookiePath,
            @Value("${app.jwt.cookie-secure:false}") boolean cookieSecure,
            @Value("${app.jwt.cookie-same-site:Lax}") String cookieSameSite) {
        this.cookieName = cookieName;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.cookiePath = cookiePath;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
    }

    public String getCookieName() {
        return cookieName;
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(cookieName, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(refreshTokenExpiration / 1000)
                .sameSite(cookieSameSite)
                .build();
    }

    public ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();
    }

    public String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}