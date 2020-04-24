package neptunilus.blueprint.sa.security.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import neptunilus.blueprint.sa.security.filter.JwtAuthenticationFilter;
import neptunilus.blueprint.sa.security.filter.JwtAuthorizationFilter;
import neptunilus.blueprint.sa.security.service.impl.AuthenticatedUserDetailsService;
import neptunilus.blueprint.sa.security.util.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Configuration for web security.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.expirationInSeconds}")
    private long jwtExpiration;

    private final AuthenticatedUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public WebSecurityConfiguration(
            final AuthenticatedUserDetailsService userDetailsService,
            final ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated().and()
                .addFilterAt(new JwtAuthenticationFilter(this.objectMapper, jwtUtils(), authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new JwtAuthorizationFilter(jwtUtils(), this.userDetailsService), BasicAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(this.jwtSecret, this.jwtIssuer, this.jwtExpiration);
    }

}
