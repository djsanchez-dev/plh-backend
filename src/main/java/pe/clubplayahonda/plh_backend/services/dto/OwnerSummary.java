package pe.clubplayahonda.plh_backend.services.dto;

import java.util.UUID;
import pe.clubplayahonda.plh_backend.auth.model.User;

public record OwnerSummary(UUID id, String email, String firstName, String lastName, String property) {

	public static OwnerSummary from(User user) {
		return new OwnerSummary(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getProperty());
	}
}
