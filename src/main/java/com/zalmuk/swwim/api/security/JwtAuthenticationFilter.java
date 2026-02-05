package com.zalmuk.swwim.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT 인증 필터
 * 모든 요청에서 JWT 토큰을 검증하고 SecurityContext에 인증 정보를 설정
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.getUserIdFromToken(token);

                // Claims에서 관리자 여부 확인
                var claims = jwtTokenProvider.parseClaims(token);
                Boolean isAdmin = claims.get("isAdmin", Boolean.class);

                List<SimpleGrantedAuthority> authorities;
                if (Boolean.TRUE.equals(isAdmin)) {
                    authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_USER"),
                            new SimpleGrantedAuthority("ROLE_ADMIN")
                    );
                } else {
                    authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                }

                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // X-User-Id 헤더에 사용자 ID 설정 (Controller에서 사용)
                // 이미 헤더가 있으면 덮어쓰지 않음
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // 인증이 필요 없는 경로는 필터 스킵
        return path.equals("/") ||
               path.equals("/health") ||
               path.startsWith("/api/v1/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/v3/api-docs");
    }
}
