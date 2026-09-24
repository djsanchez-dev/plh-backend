package pe.clubplayahonda.plh_backend.services.dto;

import jakarta.validation.constraints.DecimalMin;

/**
 * Lecturas acumuladas de los medidores al escanear.
 * El consumo del día se calcula en el backend: lectura actual − última lectura.
 */
public record ScanConsumptionRequest(
		@DecimalMin(value = "0.0", message = "La lectura de agua no puede ser negativa")
		double waterReading,
		@DecimalMin(value = "0.0", message = "La lectura de luz no puede ser negativa")
		double electricityReading) {
}
