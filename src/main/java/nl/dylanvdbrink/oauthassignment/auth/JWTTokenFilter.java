package nl.dylanvdbrink.oauthassignment.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.dylanvdbrink.oauthassignment.service.AuthorizationService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JWTTokenFilter extends OncePerRequestFilter {
    private final AuthorizationService authorizationService;

    public JWTTokenFilter(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String headerValue = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(headerValue) || !headerValue.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = headerValue.split(" ")[1].trim();
        DecodedJWT decoded;
        try {
            decoded = authorizationService.verifyToken(token);
        } catch (AuthenticationException e) {
            response.sendError(401, e.getMessage());
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(decoded, null, List.of());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

}
