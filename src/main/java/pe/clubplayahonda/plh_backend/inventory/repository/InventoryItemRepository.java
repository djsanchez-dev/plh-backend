package pe.clubplayahonda.plh_backend.inventory.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryCategory;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    Optional<InventoryItem> findByCodeIgnoreCase(String code);
    List<InventoryItem> findAllByOrderByNameAsc();
    List<InventoryItem> findByCategoryOrderByNameAsc(InventoryCategory category);
}
