package pe.clubplayahonda.plh_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.clubplayahonda.plh_backend.auth.model.BoardPosition;
import pe.clubplayahonda.plh_backend.auth.model.EmployeeType;
import pe.clubplayahonda.plh_backend.auth.model.UserRole;

public record AdminPersonUpdateRequest(
		@NotBlank(message = "El nombre es obligatorio")
		@Size(max = 60, message = "El nombre no puede superar 60 caracteres")
		String firstName,
		@NotBlank(message = "El apellido es obligatorio")
		@Size(max = 60, message = "El apellido no puede superar 60 caracteres")
		String lastName,
		@NotNull(message = "El rol es obligatorio")
		UserRole role,
		EmployeeType employeeType,
		@Size(max = 20, message = "El tipo de documento no puede superar 20 caracteres")
		String documentType,
		@Size(max = 30, message = "El número de documento no puede superar 30 caracteres")
		String documentNumber,
		@Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
		String phone,
		@Size(max = 255, message = "La dirección no puede superar 255 caracteres")
		String address,
		/** true = propietario que además forma parte de la directiva. */
		Boolean boardMember,
		/** Cargo en la directiva (obligatorio si boardMember o si el rol es BOARD). */
		BoardPosition boardPosition,
		@Size(max = 100, message = "La propiedad no puede superar 100 caracteres")
		String property) {
}