package pe.clubplayahonda.plh_backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
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
		String password) {
}
