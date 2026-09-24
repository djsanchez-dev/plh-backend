package pe.clubplayahonda.plh_backend.condominium.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pe.clubplayahonda.plh_backend.condominium.model.CondominiumStatus;

public record CondominiumRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 40, message = "El código no puede superar 40 caracteres")
        String code,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 160, message = "El nombre no puede superar 160 caracteres")
        String name,
        @NotBlank(message = "El país es obligatorio")
        @Size(max = 80, message = "El país no puede superar 80 caracteres")
        String country,
        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 80, message = "La ciudad no puede superar 80 caracteres")
        String city,
        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
        String address,
        Double latitude,
        Double longitude,
        CondominiumStatus status) {
}
