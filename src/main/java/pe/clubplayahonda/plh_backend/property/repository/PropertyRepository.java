package pe.clubplayahonda.plh_backend.property.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.clubplayahonda.plh_backend.property.model.Property;

public interface PropertyRepository extends JpaRepository<Property, UUID> {

	Optional<Property> findByQrCode(UUID qrCode);

	Optional<Property> findFirstByOwnerId(UUID ownerId);

	List<Property> findByOwnerId(UUID ownerId);

	/** Casa libre con el mismo lote: se reclama al crear un nuevo propietario (conserva el QR). */
	Optional<Property> findFirstByOwnerIsNullAndLotIgnoreCase(String lot);
}
