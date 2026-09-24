package pe.clubplayahonda.plh_backend.auth.dto;

import java.util.UUID;

public record AuthResponse(
		String accessToken,
		String tokenType,
		long expiresIn,
		UUID userId,
		String email,
		String firstName,
		String lastName,
		String fullName,
		String role) {
}
