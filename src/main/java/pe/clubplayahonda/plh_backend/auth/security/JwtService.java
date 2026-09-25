package pe.clubplayahonda.plh_backend.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private final SecretKey signingKey;
	private final long expirationMs;

	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-ms}") long expirationMs) {
		byte[] keyBytes = toSigningKey(secret);
		this.signingKey = Keys.hmacShaKeyFor(keyBytes);
		this.expirationMs = expirationMs;
	}

	private byte[] toSigningKey(String secret) {
		if (secret == null || secret.isBlank()) {
			throw new IllegalArgumentException("JWT secret cannot be blank");
		}

		try {
			return Decoders.BASE64.decode(secret);
		} catch (RuntimeException ignored) {
			try {
				return Decoders.BASE64URL.decode(secret);
			} catch (RuntimeException ignoredAgain) {
				return secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
			}
		}
	}

	public String generateToken(UserDetails userDetails) {
		Date issuedAt = new Date();
		Date expiration = new Date(issuedAt.getTime() + expirationMs);
		return Jwts.builder()
				.subject(userDetails.getUsername())
				.issuedAt(issuedAt)
				.expiration(expiration)
				.signWith(signingKey)
				.compact();
	}

	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		Claims claims = parseClaims(token);
		return claims.getSubject().equalsIgnoreCase(userDetails.getUsername())
				&& claims.getExpiration().after(new Date());
	}

	public long getExpirationMs() {
		return expirationMs;
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
