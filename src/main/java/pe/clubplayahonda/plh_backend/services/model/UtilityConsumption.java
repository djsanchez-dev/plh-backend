package pe.clubplayahonda.plh_backend.services.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import pe.clubplayahonda.plh_backend.auth.model.User;

@Entity
@Table(name = "utility_consumptions")
public class UtilityConsumption {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@Column(name = "period_start", nullable = false)
	private LocalDate periodStart;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private ConsumptionPeriod period;

	@Column(name = "water_liters", nullable = false)
	private double waterLiters;

	@Column(name = "electricity_kwh", nullable = false)
	private double electricityKwh;

	/** Lectura acumulada del medidor de agua (nulo en registros antiguos que solo guardaban consumo). */
	@Column(name = "water_reading")
	private Double waterReading;

	/** Lectura acumulada del medidor de luz (kWh del contador). */
	@Column(name = "electricity_reading")
	private Double electricityReading;

	protected UtilityConsumption() {
	}

	public UtilityConsumption(
			User owner,
			LocalDate periodStart,
			ConsumptionPeriod period,
			double waterLiters,
			double electricityKwh) {
		this.owner = owner;
		this.periodStart = periodStart;
		this.period = period;
		this.waterLiters = waterLiters;
		this.electricityKwh = electricityKwh;
	}

	public UUID getId() { return id; }
	public User getOwner() { return owner; }
	public LocalDate getPeriodStart() { return periodStart; }
	public ConsumptionPeriod getPeriod() { return period; }
	public double getWaterLiters() { return waterLiters; }
	public double getElectricityKwh() { return electricityKwh; }
	public Double getWaterReading() { return waterReading; }
	public Double getElectricityReading() { return electricityReading; }

	public void setReadings(Double waterReading, Double electricityReading) {
		this.waterReading = waterReading;
		this.electricityReading = electricityReading;
	}

	public void update(
			LocalDate periodStart,
			ConsumptionPeriod period,
			double waterLiters,
			double electricityKwh) {
		this.periodStart = periodStart;
		this.period = period;
		this.waterLiters = waterLiters;
		this.electricityKwh = electricityKwh;
	}
}
