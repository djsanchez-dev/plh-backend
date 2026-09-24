package pe.clubplayahonda.plh_backend.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryMovementType;

public record InventoryMovementRequest(
        @NotNull(message = "El tipo de movimiento es obligatorio")
        InventoryMovementType type,

        @Min(value = 1, message = "La cantidad debe ser mayor que cero")
        int quantity,

        @NotBlank(message = "El responsable es obligatorio")
        @Size(max = 120, message = "El responsable no puede superar 120 caracteres")
        String responsiblePerson,

        @Size(max = 120, message = "El área no puede superar 120 caracteres")
        String destinationArea,

        @Size(max = 300, message = "Las notas no pueden superar 300 caracteres")
        String notes,

        @Size(max = 120, message = "La persona que recibió no puede superar 120 caracteres")
        String receivedBy,

        @NotNull(message = "La fecha y hora es obligatoria")
        LocalDateTime movementAt
) {}
