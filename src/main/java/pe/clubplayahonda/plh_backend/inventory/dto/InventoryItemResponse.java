package pe.clubplayahonda.plh_backend.inventory.dto;

import java.time.Instant;
import java.util.UUID;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryItem;

public record InventoryItemResponse(
        UUID id,
        String code,
        String name,
        String category,
        int quantity,
        int minStock,
        boolean lowStock,
        String unit,
        String location,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static InventoryItemResponse from(InventoryItem item) {
        return new InventoryItemResponse(
                item.getId(),
                item.getCode(),
                item.getName(),
                item.getCategory().name(),
                item.getQuantity(),
                item.getMinStock(),
                item.isLowStock() || item.getQuantity() == 0,
                item.getUnit(),
                item.getLocation(),
                item.getNotes(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
