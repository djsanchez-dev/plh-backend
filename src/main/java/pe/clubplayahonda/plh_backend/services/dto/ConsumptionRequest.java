package pe.clubplayahonda.plh_backend.services.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import pe.clubplayahonda.plh_backend.services.model.ConsumptionPeriod;

public record ConsumptionRequest(
		@NotNull LocalDate periodStart,
		@NotNull ConsumptionPeriod period,
		@DecimalMin(value = "0.0") double waterLiters,
		@DecimalMin(value = "0.0") double electricityKwh) {
}
