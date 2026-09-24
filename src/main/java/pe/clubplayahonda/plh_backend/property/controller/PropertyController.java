package pe.clubplayahonda.plh_backend.property.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.clubplayahonda.plh_backend.property.dto.PropertyResponse;
import pe.clubplayahonda.plh_backend.property.service.PropertyService;

/**
 * Propiedades físicas del condominio.
 *
 * Lectura: ADMIN, ASSISTANT_ADMIN y BOARD (impresión de etiquetas QR).
 */
@RestController
@RequestMapping("/api/properties")
public class PropertyController {

	private final PropertyService propertyService;

	public PropertyController(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD')")
	public List<PropertyResponse> list() {
		return propertyService.list();
	}
}
