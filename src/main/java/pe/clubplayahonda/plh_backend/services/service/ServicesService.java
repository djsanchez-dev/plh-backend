package pe.clubplayahonda.plh_backend.services.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.clubplayahonda.plh_backend.auth.model.UserRole;
import pe.clubplayahonda.plh_backend.auth.repository.UserRepository;
import pe.clubplayahonda.plh_backend.services.dto.ConsumptionResponse;
import pe.clubplayahonda.plh_backend.services.dto.ConsumptionRequest;
import pe.clubplayahonda.plh_backend.services.dto.OwnerSummary;
import pe.clubplayahonda.plh_backend.services.dto.ScanConsumptionRequest;
import pe.clubplayahonda.plh_backend.services.dto.ScanResponse;
import pe.clubplayahonda.plh_backend.services.dto.TodayResponse;
import pe.clubplayahonda.plh_backend.services.model.ConsumptionPeriod;
import pe.clubplayahonda.plh_backend.services.model.UtilityConsumption;
import pe.clubplayahonda.plh_backend.services.repository.UtilityConsumptionRepository;
import pe.clubplayahonda.plh_backend.property.model.Property;
import pe.clubplayahonda.plh_backend.property.repository.PropertyRepository;
import pe.clubplayahonda.plh_backend.auth.model.User;

/**
 * Consumo de servicios por propietario.
 * No siembra datos: la lista sale exactamente de lo que existe en la base.
 * Un propietario sin directiva solo consulta su propio consumo.
 */
@Service
public class ServicesService {

    private final UserRepository userRepository;
    private final UtilityConsumptionRepository consumptionRepository;
    private final PropertyRepository propertyRepository;

    public ServicesService(UserRepository userRepository, UtilityConsumptionRepository consumptionRepository,
            PropertyRepository propertyRepository) {
        this.userRepository = userRepository;
        this.consumptionRepository = consumptionRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional(readOnly = true)
    public List<OwnerSummary> listOwners(String requesterEmail) {
        User requester = findByEmail(requesterEmail);
        // Propietario "común": solo se lista a sí mismo para ver lo suyo.
        if (isPlainOwner(requester)) {
            return List.of(OwnerSummary.from(requester));
        }
        return userRepository.findByRoleOrderByLastNameAscFirstNameAsc(UserRole.OWNER).stream()
                .sorted(Comparator.comparing((User user) -> user.getLastName() == null ? "" : user.getLastName(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(user -> user.getFirstName() == null ? "" : user.getFirstName(), String.CASE_INSENSITIVE_ORDER))
                .map(OwnerSummary::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsumptionResponse> history(UUID ownerId, String requesterEmail) {
        User requester = findByEmail(requesterEmail);
        if (isPlainOwner(requester) && !requester.getId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puedes consultar tu propio consumo");
        }
        if (!userRepository.existsById(ownerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado");
        }
        // Mediciones diarias: el historial completo ordenado por fecha, sin dividir por periodo.
        return consumptionRepository.findByOwnerIdOrderByPeriodStartAsc(ownerId)
                .stream().map(ConsumptionResponse::from).toList();
    }

    /** Propietario sin marca de directiva: acceso restringido a sus propios datos. */
    private boolean isPlainOwner(User user) {
        return user.getRole() == UserRole.OWNER && !user.isBoardMember();
    }

    private User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));
    }

    @Transactional
    public ConsumptionResponse addConsumption(UUID ownerId, ConsumptionRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));
        if (owner.getRole() != UserRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no es propietario");
        }
        boolean duplicate = consumptionRepository.existsByOwnerIdAndPeriodStart(
                ownerId, request.periodStart());
        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una medición de consumo para esa fecha");
        }
        return ConsumptionResponse.from(consumptionRepository.save(new pe.clubplayahonda.plh_backend.services.model.UtilityConsumption(
                owner,
                request.periodStart(),
                request.period(),
                request.waterLiters(),
                request.electricityKwh())));
    }

    @Transactional
    public ConsumptionResponse updateConsumption(UUID id, ConsumptionRequest request) {
        var consumption = consumptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consumo no encontrado"));
        consumption.update(
                request.periodStart(),
                request.period(),
                request.waterLiters(),
                request.electricityKwh());
        return ConsumptionResponse.from(consumptionRepository.save(consumption));
    }

    @Transactional
    public void deleteConsumption(UUID id) {
        if (!consumptionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consumo no encontrado");
        }
        consumptionRepository.deleteById(id);
    }

    // ---------------------------------------------------------------------
    // Flujo QR: lectura de medidores por propiedad
    // ---------------------------------------------------------------------

    /** Ficha que ve la persona encargada al escanear el QR de una propiedad. */
    @Transactional(readOnly = true)
    public ScanResponse scan(UUID qrCode) {
        Property property = findProperty(qrCode);
        User owner = property.getOwner();
        if (owner == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La propiedad no tiene propietario registrado");
        }
        LocalDate today = LocalDate.now();
        UtilityConsumption previous = previousReading(owner.getId(), today);
        UtilityConsumption todayRecord = consumptionRepository
                .findByOwnerIdAndPeriodStart(owner.getId(), today).orElse(null);
        return new ScanResponse(
                property.getId(),
                property.getLot(),
                owner.getFullName(),
                owner.getPhone(),
                previous == null ? null : previous.getPeriodStart(),
                previous == null ? null : previous.getWaterReading(),
                previous == null ? null : previous.getElectricityReading(),
                todayRecord != null,
                todayRecord == null ? null : todayRecord.getWaterReading(),
                todayRecord == null ? null : todayRecord.getElectricityReading(),
                todayRecord == null ? null : todayRecord.getWaterLiters(),
                todayRecord == null ? null : todayRecord.getElectricityKwh());
    }

    /**
     * Guarda (o corrige) la medición de HOY de la propiedad escaneada.
     * Recibe lecturas acumuladas de los medidores y calcula el consumo del día.
     */
    @Transactional
    public ConsumptionResponse saveScan(UUID qrCode, ScanConsumptionRequest request) {
        Property property = findProperty(qrCode);
        User owner = property.getOwner();
        if (owner == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La propiedad no tiene propietario registrado");
        }

        LocalDate today = LocalDate.now();
        UtilityConsumption previous = previousReading(owner.getId(), today);

        if (previous != null && request.waterReading() < previous.getWaterReading()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La lectura de agua (" + request.waterReading() + " L) es menor a la del "
                            + previous.getPeriodStart() + " (" + previous.getWaterReading() + " L)");
        }
        if (previous != null && request.electricityReading() < previous.getElectricityReading()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La lectura de luz (" + request.electricityReading() + " kWh) es menor a la del "
                            + previous.getPeriodStart() + " (" + previous.getElectricityReading() + " kWh)");
        }

        // Primera lectura = línea base (consumo 0); a partir de ahí: lectura actual − última lectura.
        double waterLiters = previous == null ? 0 : request.waterReading() - previous.getWaterReading();
        double electricityKwh = previous == null ? 0 : request.electricityReading() - previous.getElectricityReading();

        UtilityConsumption record = consumptionRepository
                .findByOwnerIdAndPeriodStart(owner.getId(), today)
                .orElseGet(() -> new UtilityConsumption(owner, today, ConsumptionPeriod.DAY, 0, 0));
        record.update(today, ConsumptionPeriod.DAY, waterLiters, electricityKwh);
        record.setReadings(request.waterReading(), request.electricityReading());
        return ConsumptionResponse.from(consumptionRepository.save(record));
    }

    /** Avance del recorrido: propiedades medidas hoy y pendientes. */
    @Transactional(readOnly = true)
    public TodayResponse today() {
        LocalDate today = LocalDate.now();
        java.util.Set<UUID> measuredOwnerIds = consumptionRepository.findByPeriodStart(today).stream()
                .map(consumption -> consumption.getOwner().getId())
                .collect(java.util.stream.Collectors.toSet());

        java.util.List<TodayResponse.Item> items = propertyRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(
                        (Property property) -> property.getLot() == null ? "" : property.getLot(),
                        String.CASE_INSENSITIVE_ORDER))
                .map(property -> new TodayResponse.Item(
                        property.getQrCode(),
                        property.getLot(),
                        property.getOwner() == null ? "—" : property.getOwner().getFullName(),
                        property.getOwner() != null && measuredOwnerIds.contains(property.getOwner().getId())))
                .toList();
        long measured = items.stream().filter(TodayResponse.Item::measured).count();
        return new TodayResponse(items.size(), (int) measured, items);
    }

    private Property findProperty(UUID qrCode) {
        return propertyRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Código de propiedad no válido"));
    }

    /** Último registro con lecturas de medidor anterior a la fecha indicada. */
    private UtilityConsumption previousReading(UUID ownerId, LocalDate before) {
        return consumptionRepository
                .findFirstByOwnerIdAndPeriodStartLessThanAndWaterReadingIsNotNullAndElectricityReadingIsNotNullOrderByPeriodStartDesc(ownerId, before)
                .orElse(null);
    }
}
