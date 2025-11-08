package com.disasterrelief.relief_system.config;

import com.disasterrelief.relief_system.model.User;
import com.disasterrelief.relief_system.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class DummyAuthFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7).trim();
                // expected token format: dummy-jwt-token-<userId>
                if (token.startsWith("dummy-jwt-token-")) {
                    String idStr = token.replace("dummy-jwt-token-", "");
                    Long userId = Long.parseLong(idStr);
                    User user = userService.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                    
                    // Add ROLE_ prefix if not present
                    String roleWithPrefix = user.getRole().name().startsWith("ROLE_") ? 
                        user.getRole().name() : "ROLE_" + user.getRole().name();
                        
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleWithPrefix);
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(authority));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Authentication error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Authentication failed: " + e.getMessage() + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
