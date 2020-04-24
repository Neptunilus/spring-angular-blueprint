package neptunilus.blueprint.sa.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import neptunilus.blueprint.sa.security.authentication.AuthenticatedUser;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Utilities for JWT handling.
 */
public class JwtUtils {

    private static final String ROLE_CLAIM = "role";

    private final String jwtSecret;
    private final String jwtIssuer;
    private final long jwtExpiration;

    public JwtUtils(final String jwtSecret, final String jwtIssuer, final long jwtExpiration) {
        this.jwtSecret = jwtSecret;
        this.jwtIssuer = jwtIssuer;
        this.jwtExpiration = jwtExpiration;
    }

    public String generate(final AuthenticatedUser authenticatedUser) {
        return Jwts.builder()
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .setHeaderParam("typ", "JWT")
                .setId(UUID.randomUUID().toString())
                .setIssuer(this.jwtIssuer)
                .setIssuedAt(convertToDate(LocalDateTime.now()))
                .setSubject(authenticatedUser.getUsername())
                .setExpiration(convertToDate(LocalDateTime.now().plusSeconds(this.jwtExpiration)))
                .addClaims(Map.of(ROLE_CLAIM, authenticatedUser.getUser().getRole().getId()))
                .compact();
    }

    public Jws<Claims> validateAndParseClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
    }

    public static String getUsername(final Jws<Claims> claims) {
        return claims.getBody().getSubject();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(this.jwtSecret.getBytes());
    }

    private static Date convertToDate(final LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
