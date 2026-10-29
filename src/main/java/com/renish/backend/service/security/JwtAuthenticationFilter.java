package com.renish.backend.service.security;

import com.renish.backend.exception.InvalidTokenException;
import com.renish.backend.exception.TokenExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ") || authHeader.substring(7).isBlank()) {
            fail(request, new InvalidTokenException(
                    "Invalid authorization header. Expected format: Bearer <token>"));
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7).trim();

        try {
            final String username = jwtService.extractUsername(jwtToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    fail(request, new InvalidTokenException(
                            "Invalid token. Please login again to get a valid token."));
                }
            }
        } catch (ExpiredJwtException e) {
            fail(request, new TokenExpiredException(
                    "Token has expired. Please login again to get a new token.", e));
        } catch (JwtException | IllegalArgumentException e) {
            fail(request, new InvalidTokenException(
                    "Invalid token or expired token. Please login again.", e));
        } catch (UsernameNotFoundException e) {
            fail(request, new InvalidTokenException(
                    "Invalid token. User linked to this token no longer exists.", e));
        } catch (Exception e) {
            fail(request, new InvalidTokenException(
                    "Invalid token. Please login again to get a valid token.", e));
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Stores the reason the token was rejected so {@link JwtAuthenticationEntryPoint}
     * can return it in the 401 response when the endpoint requires authentication.
     */
    private void fail(HttpServletRequest request, RuntimeException reason) {
        SecurityContextHolder.clearContext();
        request.setAttribute(JwtAuthenticationEntryPoint.AUTH_ERROR_ATTRIBUTE, reason.getMessage());
    }
}
