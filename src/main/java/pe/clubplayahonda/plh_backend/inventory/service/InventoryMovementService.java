package pe.clubplayahonda.plh_backend.inventory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.clubplayahonda.plh_backend.inventory.dto.InventoryMovementRequest;
import pe.clubplayahonda.plh_backend.inventory.dto.InventoryMovementResponse;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryCategory;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryItem;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryMovement;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryMovementType;
import pe.clubplayahonda.plh_backend.inventory.repository.InventoryItemRepository;
import pe.clubplayahonda.plh_backend.inventory.repository.InventoryMovementRepository;

@Service
public class InventoryMovementService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    public InventoryMovementService(InventoryItemRepository inventoryItemRepository,
                                   InventoryMovementRepository inventoryMovementRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryMovementResponse> listMovementsByItem(UUID itemId) {
        return inventoryMovementRepository.findByItemIdOrderByMovementAtDesc(itemId).stream()
                .map(InventoryMovementResponse::from)
                .toList();
    }

    @Transactional
    public InventoryMovementResponse createMovement(UUID itemId, InventoryMovementRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud de movimiento es obligatoria");
        }

        if (request.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de movimiento es obligatorio");
        }

        if (request.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor que cero");
        }

        if (request.responsiblePerson() == null || request.responsiblePerson().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El responsable es obligatorio");
        }

        if (request.movementAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha y hora es obligatoria");
        }

        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado"));

        int delta = switch (request.type()) {
            case LOAN -> -request.quantity();
            case RETURN -> request.quantity();
            case CONSUMPTION -> -request.quantity();
            case ADJUSTMENT -> request.quantity();
        };

        if ((request.type() == InventoryMovementType.LOAN || request.type() == InventoryMovementType.CONSUMPTION)
                && item.getQuantity() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay suficiente stock para este movimiento");
        }

        item.adjustQuantity(delta);

        InventoryMovement movement = new InventoryMovement(
                item,
                request.type(),
                request.quantity(),
                request.responsiblePerson().trim(),
                request.destinationArea() == null ? null : request.destinationArea().trim(),
                request.notes() == null ? null : request.notes().trim(),
                request.movementAt(),
                request.type() == InventoryMovementType.RETURN ? LocalDateTime.now() : null,
                request.receivedBy() == null ? null : request.receivedBy().trim()
        );

        inventoryMovementRepository.save(movement);
        return InventoryMovementResponse.from(movement);
    }

    @Transactional
    public InventoryMovementResponse registerReturn(UUID itemId, UUID movementId, String receivedBy, LocalDateTime returnedAt) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado"));

        InventoryMovement movement = inventoryMovementRepository.findById(movementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"));

        if (!movement.getItem().getId().equals(itemId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El movimiento no pertenece a este artículo");
        }

        if (movement.getReturnedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este movimiento ya fue cerrado");
        }

        if (movement.getType() != InventoryMovementType.LOAN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se puede registrar devolución de un préstamo activo");
        }

        if (receivedBy == null || receivedBy.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La persona que recibió es obligatoria");
        }

        item.adjustQuantity(movement.getQuantity());

        movement.setReturnedAt(returnedAt == null ? LocalDateTime.now() : returnedAt);
        movement.setReceivedBy(receivedBy.trim());
        inventoryMovementRepository.save(movement);
        inventoryItemRepository.save(item);
        return InventoryMovementResponse.from(movement);
    }

    @Transactional(readOnly = true)
    public List<InventoryMovementResponse> listActiveLoans() {
        return inventoryMovementRepository
                .findByTypeAndReturnedAtIsNullOrderByMovementAtDesc(InventoryMovementType.LOAN).stream()
                .map(InventoryMovementResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryMovementResponse> findByArea(String area) {
        return inventoryMovementRepository.findByDestinationAreaContainingIgnoreCaseOrderByMovementAtDesc(area)
                .stream()
                .map(InventoryMovementResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryMovementResponse> findByResponsible(String person) {
        return inventoryMovementRepository.findByResponsiblePersonContainingIgnoreCaseOrderByMovementAtDesc(person)
                .stream()
                .map(InventoryMovementResponse::from)
                .toList();
    }
}
