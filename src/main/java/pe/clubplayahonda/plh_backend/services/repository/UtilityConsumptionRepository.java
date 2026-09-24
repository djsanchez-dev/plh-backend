package pe.clubplayahonda.plh_backend.services.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.clubplayahonda.plh_backend.services.model.ConsumptionPeriod;
import pe.clubplayahonda.plh_backend.services.model.UtilityConsumption;

public interface UtilityConsumptionRepository extends JpaRepository<UtilityConsumption, UUID> {

	List<UtilityConsumption> findByOwnerIdOrderByPeriodStartAsc(UUID ownerId);

	/** Una sola medición por propietario y fecha (el periodo ya no divide el historial). */
	boolean existsByOwnerIdAndPeriodStart(UUID ownerId, LocalDate periodStart);

	Optional<UtilityConsumption> findByOwnerIdAndPeriodStart(UUID ownerId, LocalDate periodStart);

	List<UtilityConsumption> findByPeriodStart(LocalDate periodStart);

	/** Última lectura de medidor anterior a una fecha (para calcular el consumo del día). */
	Optional<UtilityConsumption> findFirstByOwnerIdAndPeriodStartLessThanAndWaterReadingIsNotNullAndElectricityReadingIsNotNullOrderByPeriodStartDesc(UUID ownerId, LocalDate periodStart);

	long countByOwnerId(UUID ownerId);

	@Query("select coalesce(sum(u.waterLiters), 0) from UtilityConsumption u "
			+ "where u.periodStart >= :start and u.periodStart < :end")
	double sumWaterBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

	@Query("select coalesce(sum(u.electricityKwh), 0) from UtilityConsumption u "
			+ "where u.periodStart >= :start and u.periodStart < :end")
	double sumElectricityBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

	long countByPeriodStartGreaterThanEqualAndPeriodStartLessThan(LocalDate start, LocalDate end);
}
