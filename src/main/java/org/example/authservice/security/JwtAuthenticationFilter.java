package org.example.authservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.authservice.domain.dto.responses.ErrorResponse;
import org.example.authservice.exceptions.AccessDeniedException;
import org.example.authservice.repositories.TokenRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static org.example.authservice.config.WebSecurityConfig.WHITE_LIST_URL;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
                                    throws ServletException, IOException {
        if (isWhiteListed(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            final String authHeader = request.getHeader(HEADER_NAME); // Bearer *token*

            if (authHeader == null || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
                throw new AccessDeniedException("Auth header was not provided or has wrong prefix");
            }

            String jwtToken = authHeader.substring(BEARER_PREFIX.length()); // Only *token* part, without Bearer prefix
            String email = jwtService.extractEmail(jwtToken);
            String tokenType = jwtService.extractTokenType(jwtToken);
            if (StringUtils.isEmpty(tokenType) || tokenType.equals("REFRESH")) {
                throw new AccessDeniedException("Can not authorize user with refresh token");
            }

            tokenRepository.getByToken(jwtToken)
                    .orElseThrow(() -> new AccessDeniedException("Token has expired or invalid"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

            filterChain.doFilter(request, response);
        } catch (AccessDeniedException ex) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .id(UUID.randomUUID())
                    .message(ex.getMessage())
                    .build();
            String json = new ObjectMapper().writeValueAsString(errorResponse);
            response.getWriter().write(json);
        }
    }

    private boolean isWhiteListed(HttpServletRequest request) {
        String path = request.getRequestURI();
        return Arrays.stream(WHITE_LIST_URL)
                .anyMatch(url -> path.matches(url.replace("/**", ".*")));
    }
}