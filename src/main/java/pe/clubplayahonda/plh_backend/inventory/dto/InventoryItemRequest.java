package pe.clubplayahonda.plh_backend.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryCategory;

public record InventoryItemRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 80, message = "El código no puede superar 80 caracteres")
        String code,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String name,

        @NotNull(message = "La categoría es obligatoria")
        InventoryCategory category,

        @Min(value = 0, message = "La cantidad no puede ser negativa")
        int quantity,

        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        int minStock,

        @Size(max = 40, message = "La unidad no puede superar 40 caracteres")
        String unit,

        @Size(max = 120, message = "La ubicación no puede superar 120 caracteres")
        String location,

        @Size(max = 200, message = "Las notas no pueden superar 200 caracteres")
        String notes
) {}
