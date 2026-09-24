package pe.clubplayahonda.plh_backend.services.dto;

import java.time.LocalDate;
import java.util.UUID;
import pe.clubplayahonda.plh_backend.services.model.UtilityConsumption;

public record ConsumptionResponse(
		UUID id,
		LocalDate periodStart,
		String period,
		double waterLiters,
		double electricityKwh,
		Double waterReading,
		Double electricityReading) {

	public static ConsumptionResponse from(UtilityConsumption consumption) {
		return new ConsumptionResponse(
				consumption.getId(),
				consumption.getPeriodStart(),
				consumption.getPeriod().name(),
				consumption.getWaterLiters(),
				consumption.getElectricityKwh(),
				consumption.getWaterReading(),
				consumption.getElectricityReading());
	}
}
