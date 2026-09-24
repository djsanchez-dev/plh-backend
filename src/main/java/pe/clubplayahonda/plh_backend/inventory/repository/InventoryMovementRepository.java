package pe.clubplayahonda.plh_backend.inventory.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryMovement;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryMovementType;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    List<InventoryMovement> findByItemIdOrderByMovementAtDesc(UUID itemId);
    List<InventoryMovement> findByDestinationAreaContainingIgnoreCaseOrderByMovementAtDesc(String area);
    List<InventoryMovement> findByResponsiblePersonContainingIgnoreCaseOrderByMovementAtDesc(String person);
    List<InventoryMovement> findByTypeAndReturnedAtIsNullOrderByMovementAtDesc(InventoryMovementType type);
    List<InventoryMovement> findTop5ByOrderByMovementAtDesc();
    boolean existsByItemIdAndTypeAndReturnedAtIsNull(UUID itemId, InventoryMovementType type);
    void deleteByItemId(UUID itemId);
}
