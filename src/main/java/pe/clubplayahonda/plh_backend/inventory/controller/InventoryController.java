package pe.clubplayahonda.plh_backend.inventory.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.clubplayahonda.plh_backend.inventory.dto.InventoryItemRequest;
import pe.clubplayahonda.plh_backend.inventory.dto.InventoryItemResponse;
import pe.clubplayahonda.plh_backend.inventory.dto.InventoryMovementRequest;
import pe.clubplayahonda.plh_backend.inventory.dto.InventoryMovementResponse;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryCategory;
import pe.clubplayahonda.plh_backend.inventory.service.InventoryMovementService;
import pe.clubplayahonda.plh_backend.inventory.service.InventoryService;

/**
 * Inventario del condominio.
 *
 * Lectura: ADMIN, ASISTENTE ADMIN, DIRECTIVA y EMPLEADO.
 * Escritura: ADMIN, ASISTENTE ADMIN y EMPLEADO.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryMovementService inventoryMovementService;

    public InventoryController(InventoryService inventoryService, InventoryMovementService inventoryMovementService) {
        this.inventoryService = inventoryService;
        this.inventoryMovementService = inventoryMovementService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD','EMPLOYEE')")
    public List<InventoryItemResponse> list() {
        return inventoryService.listItems();
    }

    @GetMapping("/category")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD','EMPLOYEE')")
    public List<InventoryItemResponse> listByCategory(@RequestParam InventoryCategory category) {
        return inventoryService.listByCategory(category);
    }

    /** Préstamos activos: evita pedir el historial de cada artículo (N+1). */
    @GetMapping("/movements/active-loans")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD','EMPLOYEE')")
    public List<InventoryMovementResponse> activeLoans() {
        return inventoryMovementService.listActiveLoans();
    }

    @GetMapping("/{id}/movements")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD','EMPLOYEE')")
    public List<InventoryMovementResponse> listMovements(@PathVariable UUID id) {
        return inventoryMovementService.listMovementsByItem(id);
    }

    @PostMapping("/{id}/movements")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','EMPLOYEE')")
    public InventoryMovementResponse createMovement(@PathVariable UUID id, @Valid @RequestBody InventoryMovementRequest request) {
        return inventoryMovementService.createMovement(id, request);
    }

    @PostMapping("/{id}/movements/{movementId}/return")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','EMPLOYEE')")
    public InventoryMovementResponse registerReturn(
            @PathVariable UUID id,
            @PathVariable UUID movementId,
            @RequestParam(required = false) String receivedBy) {
        return inventoryMovementService.registerReturn(id, movementId, receivedBy, null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','EMPLOYEE')")
    public InventoryItemResponse create(@Valid @RequestBody InventoryItemRequest request) {
        return inventoryService.createItem(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','EMPLOYEE')")
    public InventoryItemResponse update(@PathVariable UUID id, @Valid @RequestBody InventoryItemRequest request) {
        return inventoryService.updateItem(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','EMPLOYEE')")
    public void delete(@PathVariable UUID id) {
        inventoryService.deleteItem(id);
    }
}
