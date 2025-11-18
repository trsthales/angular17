package com.example.ecommerce.boot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
// TokenAuthFilter retained for historical reference but no longer registered as a bean.
// It has been replaced by the OAuth2 Resource Server JWT configuration (JwtDecoder + SecurityConfig).
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro simples que aceita um token estático `secret-token` como Bearer para testes locais.
 * Em produção substitua por validação JWT real.
 */
public class TokenAuthFilter extends OncePerRequestFilter {

    private static final String TEST_TOKEN = "secret-token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            if (TEST_TOKEN.equals(token)) {
                // cria authenticação simples com role USER
                Authentication authentication = new UsernamePasswordAuthenticationToken("test-user", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
