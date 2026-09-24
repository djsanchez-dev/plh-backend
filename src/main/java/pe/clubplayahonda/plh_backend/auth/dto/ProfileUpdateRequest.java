package pe.clubplayahonda.plh_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
		@NotBlank(message = "El nombre es obligatorio")
		@Size(max = 60, message = "El nombre no puede superar 60 caracteres")
		String firstName,
		@NotBlank(message = "El apellido es obligatorio")
		@Size(max = 60, message = "El apellido no puede superar 60 caracteres")
		String lastName,
		@Size(max = 20, message = "El tipo de documento no puede superar 20 caracteres")
		String documentType,
		@Size(max = 30, message = "El número de documento no puede superar 30 caracteres")
		String documentNumber,
		@Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
		String phone,
		@Size(max = 255, message = "La dirección no puede superar 255 caracteres")
		String address) {
}
