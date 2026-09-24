package pe.clubplayahonda.plh_backend.services.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.clubplayahonda.plh_backend.services.dto.ConsumptionResponse;
import pe.clubplayahonda.plh_backend.services.dto.ConsumptionRequest;
import pe.clubplayahonda.plh_backend.services.dto.OwnerSummary;
import pe.clubplayahonda.plh_backend.services.dto.ScanConsumptionRequest;
import pe.clubplayahonda.plh_backend.services.dto.ScanResponse;
import pe.clubplayahonda.plh_backend.services.dto.TodayResponse;
import pe.clubplayahonda.plh_backend.services.service.ServicesService;

/**
 * Consumo de servicios (agua/luz) por propietario: mediciones diarias.
 *
 * Lectura: ADMIN, ASISTENTE ADMIN, DIRECTIVA, EMPLEADO y PROPIETARIO
 * (el propietario sin directiva solo consulta su propio consumo).
 * Escritura: ADMIN, ASISTENTE ADMIN y EMPLEADO (la directiva solo consulta).
 */
@RestController
@RequestMapping("/api/services")
public class ServicesController {

    private final ServicesService servicesService;

    public ServicesController(ServicesService servicesService) {
        this.servicesService = servicesService;
    }

    @GetMapping("/owners")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD','EMPLOYEE','OWNER')")
    public List<OwnerSummary> owners(Authentication authentication) {
        return servicesService.listOwners(authentication.getName());
    }

    @GetMapping("/owners/{ownerId}/consumption")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD','EMPLOYEE','OWNER')")
    public List<ConsumptionResponse> consumption(
            @PathVariable UUID ownerId,
            Authentication authentication) {
        return servicesService.history(ownerId, authentication.getName());
    }

    @PostMapping("/owners/{ownerId}/consumption")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','EMPLOYEE')")
    public ConsumptionResponse addConsumption(
            @PathVariable UUID ownerId,
            @Valid @RequestBody ConsumptionRequest request) {
        return servicesService.addConsumption(ownerId, request);
    }

    @PutMapping("/consumption/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','EMPLOYEE')")
    public ConsumptionResponse updateConsumption(
            @PathVariable UUID id,
            @Valid @RequestBody ConsumptionRequest request) {
        return servicesService.updateConsumption(id, request);
    }

    @DeleteMapping("/consumption/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','EMPLOYEE')")
    public void deleteConsumption(@PathVariable UUID id) {
        servicesService.deleteConsumption(id);
    }

    // ---------------------------------------------------------------------
    // Flujo QR: la persona encargada escanea la propiedad y registra lecturas
    // ---------------------------------------------------------------------

    /** Ficha del propietario/propiedad y medición de hoy (si ya existe). */
    @GetMapping("/scan/{qrCode}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD','EMPLOYEE')")
    public ScanResponse scan(@PathVariable UUID qrCode) {
        return servicesService.scan(qrCode);
    }

    /** Guarda o corrige la medición de hoy a partir de las lecturas de los medidores. */
    @PostMapping("/scan/{qrCode}/consumption")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','EMPLOYEE')")
    public ConsumptionResponse saveScan(
            @PathVariable UUID qrCode,
            @Valid @RequestBody ScanConsumptionRequest request) {
        return servicesService.saveScan(qrCode, request);
    }

    /** Avance del recorrido diario: propiedades medidas y pendientes de hoy. */
    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD','EMPLOYEE')")
    public TodayResponse today() {
        return servicesService.today();
    }
}
