package pe.clubplayahonda.plh_backend.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.crypto.SecretKey;
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

		byte[] candidate;
		try {
			candidate = Decoders.BASE64.decode(secret);
		} catch (RuntimeException ignored) {
			try {
				candidate = Decoders.BASE64URL.decode(secret);
			} catch (RuntimeException ignoredAgain) {
				candidate = secret.getBytes(StandardCharsets.UTF_8);
			}
		}

		if (candidate.length >= 32) {
			return candidate;
		}

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 not available in the runtime", e);
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
