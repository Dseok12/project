// backend/src/main/java/com/example/backend/security/JwtFilter.java
package com.example.backend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JwtFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<Claims> claims = resolveToken(req).flatMap(jwtUtil::parseClaims);

            if (claims.isPresent()) {
                Claims body = claims.get();
                String email = body.getSubject();
                if (StringUtils.hasText(email)) {
                    String role = body.get("role", String.class);

                    List<GrantedAuthority> auths = new ArrayList<>();
                    auths.add(new SimpleGrantedAuthority("ROLE_USER"));
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        auths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }

                    Authentication auth = new UsernamePasswordAuthenticationToken(email, null, auths);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } else {
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(req, res);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        return StringUtils.hasText(token) ? Optional.of(token) : Optional.empty();
    }
}
