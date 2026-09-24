package pe.clubplayahonda.plh_backend.condominium.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.clubplayahonda.plh_backend.condominium.dto.CondominiumPointRequest;
import pe.clubplayahonda.plh_backend.condominium.dto.CondominiumPointResponse;
import pe.clubplayahonda.plh_backend.condominium.dto.CondominiumRequest;
import pe.clubplayahonda.plh_backend.condominium.dto.CondominiumResponse;
import pe.clubplayahonda.plh_backend.condominium.service.CondominiumService;

/**
 * Condominios y sus puntos.
 *
 * Lectura: ADMIN, ASISTENTE ADMIN y DIRECTIVA.
 * Escritura: solo ADMIN y ASISTENTE ADMIN.
 */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD')")
public class CondominiumController {

    private final CondominiumService condominiumService;

    public CondominiumController(CondominiumService condominiumService) {
        this.condominiumService = condominiumService;
    }

    // ---------- Condominios ----------

    @GetMapping("/condominiums")
    public List<CondominiumResponse> list() {
        return condominiumService.listCondominiums();
    }

    @PostMapping("/condominiums")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public CondominiumResponse create(@Valid @RequestBody CondominiumRequest request) {
        return condominiumService.createCondominium(request);
    }

    @PutMapping("/condominiums/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public CondominiumResponse update(@PathVariable UUID id, @Valid @RequestBody CondominiumRequest request) {
        return condominiumService.updateCondominium(id, request);
    }

    @DeleteMapping("/condominiums/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public void delete(@PathVariable UUID id) {
        condominiumService.deleteCondominium(id);
    }

    // ---------- Puntos ----------

    @GetMapping("/condominium-points")
    public List<CondominiumPointResponse> listPoints() {
        return condominiumService.listPoints();
    }

    @PostMapping("/condominium-points")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public CondominiumPointResponse createPoint(@Valid @RequestBody CondominiumPointRequest request) {
        return condominiumService.createPoint(request);
    }

    @PutMapping("/condominium-points/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public CondominiumPointResponse updatePoint(@PathVariable UUID id, @Valid @RequestBody CondominiumPointRequest request) {
        return condominiumService.updatePoint(id, request);
    }

    @DeleteMapping("/condominium-points/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public void deletePoint(@PathVariable UUID id) {
        condominiumService.deletePoint(id);
    }
}
