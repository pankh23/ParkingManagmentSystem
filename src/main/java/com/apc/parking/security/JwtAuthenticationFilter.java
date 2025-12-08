package com.apc.parking.security;

import com.apc.parking.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Skip JWT validation for OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String jwt = getJwtFromRequest(request);

            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                try {
                    // Extract user information from token
                    String username = jwtTokenProvider.getUsernameFromToken(jwt);
                    Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                    String role = jwtTokenProvider.getRoleFromToken(jwt);

                    // Create authentication object
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(authority)
                        );
                    
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Add user ID to request attributes for use in controllers
                    request.setAttribute("userId", userId);
                    request.setAttribute("username", username);
                    request.setAttribute("role", role);
                } catch (Exception e) {
                    logger.warn("Error extracting user information from token: " + e.getMessage());
                    // Token is invalid, clear any existing authentication
                    SecurityContextHolder.clearContext();
                }
            } else {
                // Log if token is missing or invalid (for debugging)
                if (jwt == null) {
                    logger.debug("No JWT token found in request for: " + request.getRequestURI());
                } else {
                    logger.debug("Invalid or expired JWT token for: " + request.getRequestURI());
                }
                // Clear any existing authentication
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            logger.error("Error in JWT authentication filter: " + e.getMessage(), e);
            // Clear security context on error
            SecurityContextHolder.clearContext();
            // Don't block the request - let Spring Security handle it
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

