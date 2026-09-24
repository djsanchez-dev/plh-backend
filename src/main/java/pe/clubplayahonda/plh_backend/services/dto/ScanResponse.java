package pe.clubplayahonda.plh_backend.services.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Datos que ve la persona encargada al escanear el QR de una propiedad:
 * ficha del propietario, última lectura anterior y la medición de hoy (si existe).
 */
public record ScanResponse(
		UUID propertyId,
		String lot,
		String ownerName,
		String ownerPhone,
		LocalDate previousDate,
		Double previousWaterReading,
		Double previousElectricityReading,
		boolean todayExists,
		Double waterReading,
		Double electricityReading,
		Double waterLiters,
		Double electricityKwh) {
}
