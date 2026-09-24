package pe.clubplayahonda.plh_backend.inventory.service;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.clubplayahonda.plh_backend.inventory.dto.InventoryItemRequest;
import pe.clubplayahonda.plh_backend.inventory.dto.InventoryItemResponse;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryCategory;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryItem;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryMovementType;
import pe.clubplayahonda.plh_backend.inventory.repository.InventoryItemRepository;
import pe.clubplayahonda.plh_backend.inventory.repository.InventoryMovementRepository;

@Service
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    public InventoryService(
            InventoryItemRepository inventoryItemRepository,
            InventoryMovementRepository inventoryMovementRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> listItems() {
        return inventoryItemRepository.findAllByOrderByNameAsc().stream()
                .map(InventoryItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> listByCategory(InventoryCategory category) {
        return inventoryItemRepository.findByCategoryOrderByNameAsc(category).stream()
                .map(InventoryItemResponse::from)
                .toList();
    }

    @Transactional
    public InventoryItemResponse createItem(InventoryItemRequest request) {
        String normalizedCode = normalizeCode(request.code());
        if (inventoryItemRepository.findByCodeIgnoreCase(normalizedCode).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con ese código");
        }

        InventoryItem item = new InventoryItem(
                normalizedCode,
                request.name().trim(),
                request.category(),
                Math.max(0, request.quantity()),
                Math.max(0, request.minStock()),
                request.unit() == null ? null : request.unit().trim(),
                request.location() == null ? null : request.location().trim(),
                request.notes() == null ? null : request.notes().trim()
        );

        return InventoryItemResponse.from(inventoryItemRepository.save(item));
    }

    @Transactional
    public InventoryItemResponse updateItem(UUID id, InventoryItemRequest request) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado"));

        String normalizedCode = normalizeCode(request.code());
        inventoryItemRepository.findByCodeIgnoreCase(normalizedCode)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con ese código");
                });

        item.updateDetails(
                normalizedCode,
                request.name().trim(),
                request.category(),
                Math.max(0, request.quantity()),
                Math.max(0, request.minStock()),
                request.unit() == null ? null : request.unit().trim(),
                request.location() == null ? null : request.location().trim(),
                request.notes() == null ? null : request.notes().trim()
        );

        return InventoryItemResponse.from(inventoryItemRepository.save(item));
    }

    @Transactional
    public void deleteItem(UUID id) {
        if (!inventoryItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado");
        }

        // Salvaguarda: no eliminar un artículo que alguien tiene físicamente prestado.
        if (inventoryMovementRepository.existsByItemIdAndTypeAndReturnedAtIsNull(
                id, InventoryMovementType.LOAN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este artículo tiene un préstamo activo. Registra la devolución antes de eliminarlo.");
        }

        // Borrado en cascada: primero sus movimientos, luego el artículo.
        inventoryMovementRepository.deleteByItemId(id);
        inventoryItemRepository.deleteById(id);
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim();
    }
}
