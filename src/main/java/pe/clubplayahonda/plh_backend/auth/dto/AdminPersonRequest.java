package pe.clubplayahonda.plh_backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.clubplayahonda.plh_backend.auth.model.BoardPosition;
import pe.clubplayahonda.plh_backend.auth.model.EmployeeType;
import pe.clubplayahonda.plh_backend.auth.model.UserRole;

public record AdminPersonRequest(
		@NotBlank(message = "El nombre es obligatorio")
		@Size(max = 60, message = "El nombre no puede superar 60 caracteres")
		String firstName,
		@NotBlank(message = "El apellido es obligatorio")
		@Size(max = 60, message = "El apellido no puede superar 60 caracteres")
		String lastName,
		@NotBlank(message = "El correo es obligatorio")
		@Email(message = "El correo no es válido")
		@Size(max = 254, message = "El correo no puede superar 254 caracteres")
		String email,
		@NotBlank(message = "La contraseña es obligatoria")
		@Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
		String password,
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
