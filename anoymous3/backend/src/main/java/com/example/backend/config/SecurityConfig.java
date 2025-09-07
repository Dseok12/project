// backend/src/main/java/com/example/backend/config/SecurityConfig.java
package com.example.backend.config;

import com.example.backend.security.JwtFilter;
import com.example.backend.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
        http
                // SPA + JWT (stateless)
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(f -> f.disable())
                .httpBasic(b -> b.disable())

                // 401/403 일관 처리
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_FORBIDDEN))
                )

                // JWT 필터
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                // 권한 규칙
                .authorizeHttpRequests(reg -> reg
                        // 정적/루트/에러
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/error").permitAll()
                        .requestMatchers("/static/**", "/assets/**", "/css/**", "/js/**", "/images/**").permitAll()

                        // 프리플라이트
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 공개 엔드포인트
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // 읽기 전용 공개: 게시글/공지/댓글/리액션 GET
                        .requestMatchers(HttpMethod.GET,
                                "/api/posts/**",
                                "/api/posts/*/comments",
                                "/api/posts/*/reactions"
                        ).permitAll()

                        // 관리자 전용
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 게시글 쓰기/수정/삭제 (인증 필요)
                        .requestMatchers(HttpMethod.POST,   "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH,  "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()

                        // 댓글/리액션 쓰기/삭제 (인증 필요)
                        .requestMatchers(HttpMethod.POST,   "/api/posts/*/comments").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/posts/*/reactions").authenticated()

                        // 마이페이지
                        .requestMatchers("/api/users/me/**").authenticated()

                        // 나머지는 인증
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        // Vite dev 서버
        c.setAllowedOrigins(List.of("http://localhost:3000"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        c.setExposedHeaders(List.of("Authorization"));
        c.setAllowCredentials(false); // JWT는 헤더 사용
        c.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }
}
