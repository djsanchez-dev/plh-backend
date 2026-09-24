package pe.clubplayahonda.plh_backend.condominium.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import pe.clubplayahonda.plh_backend.condominium.model.PointStatus;
import pe.clubplayahonda.plh_backend.condominium.model.PointType;

public record CondominiumPointRequest(
        @NotNull(message = "El condominio es obligatorio")
        UUID condominiumId,
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 60, message = "El código no puede superar 60 caracteres")
        String code,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String name,
        @NotNull(message = "El tipo de punto es obligatorio")
        PointType pointType,
        @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
        String address,
        PointStatus status) {
}
