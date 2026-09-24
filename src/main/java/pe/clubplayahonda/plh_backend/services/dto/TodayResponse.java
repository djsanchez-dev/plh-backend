package pe.clubplayahonda.plh_backend.services.dto;

import java.util.List;
import java.util.UUID;

/** Avance del recorrido del día: propiedades medidas y pendientes. */
public record TodayResponse(int total, int measured, List<Item> items) {

	public record Item(UUID qrCode, String lot, String ownerName, boolean measured) {
	}
}
