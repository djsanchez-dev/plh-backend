package pe.clubplayahonda.plh_backend.inventory.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryMovement;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryMovementType;

public record InventoryMovementResponse(
        UUID id,
        UUID itemId,
        String itemName,
        InventoryMovementType type,
        int quantity,
        String responsiblePerson,
        String destinationArea,
        String notes,
        LocalDateTime movementAt,
        LocalDateTime returnedAt,
        String receivedBy
) {
    public static InventoryMovementResponse from(InventoryMovement movement) {
        return new InventoryMovementResponse(
                movement.getId(),
                movement.getItem().getId(),
                movement.getItem().getName(),
                movement.getType(),
                movement.getQuantity(),
                movement.getResponsiblePerson(),
                movement.getDestinationArea(),
                movement.getNotes(),
                movement.getMovementAt(),
                movement.getReturnedAt(),
                movement.getReceivedBy()
        );
    }
}
