package neptunilus.blueprint.sa.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import neptunilus.blueprint.sa.security.authentication.AuthenticatedUser;
import neptunilus.blueprint.sa.security.authentication.JwtAuthenticationToken;
import neptunilus.blueprint.sa.security.service.impl.AuthenticatedUserDetailsService;
import neptunilus.blueprint.sa.security.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static neptunilus.blueprint.sa.security.util.JwtUtils.getUsername;

/**
 * Filter for user authorization based on a provided JWT in authorization.
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";

    private final JwtUtils jwtUtils;
    private final AuthenticatedUserDetailsService userDetailsService;

    public JwtAuthorizationFilter(final JwtUtils jwtUtils, final AuthenticatedUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {

        try {
            final Authentication existingAuthentication = SecurityContextHolder.getContext().getAuthentication();
            if (existingAuthentication != null && existingAuthentication.isAuthenticated()) {
                return;
            }

            final String token = extractToken(request);
            final Jws<Claims> claims = this.jwtUtils.validateAndParseClaims(token);

            final String username = getUsername(claims);
            final AuthenticatedUser authenticatedUser = this.userDetailsService.loadUserByUsername(username);

            final JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authenticatedUser, token);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            LOGGER.info("successfully authorized user: {}", username);

        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.info("authorization failed, jwt issue: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (UsernameNotFoundException e) {
            LOGGER.info("authorization failed, username issue: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private static String extractToken(final HttpServletRequest request) {
        final String header = StringUtils.trimToNull(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BEARER)) {
            return null;
        }
        return StringUtils.trimToNull(StringUtils.substringAfter(header, AUTHENTICATION_SCHEME_BEARER));
    }

}
