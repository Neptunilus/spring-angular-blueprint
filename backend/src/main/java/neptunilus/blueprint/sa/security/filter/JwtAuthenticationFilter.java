package neptunilus.blueprint.sa.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import neptunilus.blueprint.sa.security.authentication.AuthenticatedUser;
import neptunilus.blueprint.sa.security.exception.JwtAuthenticationException;
import neptunilus.blueprint.sa.security.filter.in.LoginRequest;
import neptunilus.blueprint.sa.security.filter.out.LoginResponse;
import neptunilus.blueprint.sa.security.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * Filter for user authentication that returns a JWT in success case.
 */
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String DEFAULT_FILTER_URL = "/login";

    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(final ObjectMapper objectMapper, final JwtUtils jwtUtils, final AuthenticationManager authenticationManager) {
        super(DEFAULT_FILTER_URL);
        this.objectMapper = objectMapper;
        this.jwtUtils = jwtUtils;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
        try {
            final LoginRequest loginRequest = this.objectMapper.readValue(request.getReader(), LoginRequest.class);
            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword());

            return getAuthenticationManager().authenticate(authentication);

        } catch (final AuthenticationException e) {
            throw e;
        } catch (final Exception e) {
            LOGGER.error("authentication failed, request not processable: {}", e.getMessage(), e);
            throw new JwtAuthenticationException("authentication request not processable", e);
        }
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                            final FilterChain chain, final Authentication authResult) {
        try {
            final AuthenticatedUser authenticatedUser = (AuthenticatedUser) authResult.getPrincipal();
            final String token = this.jwtUtils.generate(authenticatedUser);

            final LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(token);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
            this.objectMapper.writeValue(response.getWriter(), loginResponse);

            LOGGER.info("successfully authenticated user: {}", authenticatedUser.getUsername());

        } catch (final Exception e) {
            LOGGER.error("authentication failed, could not write response: {}", e.getMessage(), e);
            throw new JwtAuthenticationException("could not write response", e);
        }
    }

}
