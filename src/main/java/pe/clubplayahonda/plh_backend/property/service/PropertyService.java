package pe.clubplayahonda.plh_backend.property.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.clubplayahonda.plh_backend.property.dto.PropertyResponse;
import pe.clubplayahonda.plh_backend.property.model.Property;
import pe.clubplayahonda.plh_backend.property.repository.PropertyRepository;

/** Listado de propiedades con su QR (para impresión de etiquetas). */
@Service
public class PropertyService {

	private final PropertyRepository propertyRepository;

	public PropertyService(PropertyRepository propertyRepository) {
		this.propertyRepository = propertyRepository;
	}

	@Transactional(readOnly = true)
	public List<PropertyResponse> list() {
		return propertyRepository.findAll().stream()
				.sorted(Comparator.comparing(
						(Property property) -> property.getLot() == null ? "" : property.getLot(),
						String.CASE_INSENSITIVE_ORDER))
				.map(property -> new PropertyResponse(
						property.getId(),
						property.getQrCode(),
						property.getLot(),
						property.getOwner() == null ? "—" : property.getOwner().getFullName()))
				.toList();
	}
}
