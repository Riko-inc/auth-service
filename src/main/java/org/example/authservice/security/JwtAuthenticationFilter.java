package org.example.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.authservice.exceptions.AccessDeniedException;
import org.example.authservice.repositories.TokenRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

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
        final String authHeader = request.getHeader(HEADER_NAME); // Bearer *token*
        if (authHeader == null || !StringUtils.startsWith(authHeader, BEARER_PREFIX) || isWhiteListed(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(BEARER_PREFIX.length()); // Only *token* part, withoud Bearer prefix
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
    }

    private boolean isWhiteListed(HttpServletRequest request) {
        String path = request.getRequestURI();
        return Arrays.stream(WHITE_LIST_URL)
                .anyMatch(url -> path.matches(url.replace("/**", ".*")));
    }
}