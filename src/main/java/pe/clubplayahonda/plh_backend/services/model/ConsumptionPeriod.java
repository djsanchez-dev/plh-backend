package pe.clubplayahonda.plh_backend.services.model;

/**
 * Periodo de una medición de consumo.
 * Las mediciones se toman a diario (agua y luz), por lo que el sistema solo usa DAY.
 * WEEK y MONTH se conservan para poder leer registros legados ya guardados en la base.
 */
public enum ConsumptionPeriod {
	DAY,
	WEEK,
	MONTH
}
