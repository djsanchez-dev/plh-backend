package pe.clubplayahonda.plh_backend.condominium.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.clubplayahonda.plh_backend.condominium.dto.CondominiumPointRequest;
import pe.clubplayahonda.plh_backend.condominium.dto.CondominiumPointResponse;
import pe.clubplayahonda.plh_backend.condominium.dto.CondominiumRequest;
import pe.clubplayahonda.plh_backend.condominium.dto.CondominiumResponse;
import pe.clubplayahonda.plh_backend.condominium.model.Condominium;
import pe.clubplayahonda.plh_backend.condominium.model.CondominiumPoint;
import pe.clubplayahonda.plh_backend.condominium.model.CondominiumStatus;
import pe.clubplayahonda.plh_backend.condominium.model.PointStatus;
import pe.clubplayahonda.plh_backend.condominium.repository.CondominiumPointRepository;
import pe.clubplayahonda.plh_backend.condominium.repository.CondominiumRepository;

@Service
public class CondominiumService {

    private final CondominiumRepository condominiumRepository;
    private final CondominiumPointRepository pointRepository;

    public CondominiumService(CondominiumRepository condominiumRepository,
                              CondominiumPointRepository pointRepository) {
        this.condominiumRepository = condominiumRepository;
        this.pointRepository = pointRepository;
    }

    // ---------- Condominios ----------

    @Transactional(readOnly = true)
    public List<CondominiumResponse> listCondominiums() {
        return condominiumRepository.findAllByOrderByCodeAsc().stream()
                .map(CondominiumResponse::from)
                .toList();
    }

    @Transactional
    public CondominiumResponse createCondominium(CondominiumRequest request) {
        String code = normalize(request.code());
        if (condominiumRepository.existsByCodeIgnoreCase(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un condominio con ese código");
        }

        Condominium condominium = new Condominium(
                code,
                request.name().trim(),
                request.country().trim(),
                request.city().trim(),
                request.address().trim());
        condominium.update(
                condominium.getName(),
                condominium.getCountry(),
                condominium.getCity(),
                condominium.getAddress(),
                request.latitude(),
                request.longitude(),
                request.status() == null ? CondominiumStatus.ACTIVE : request.status());
        return CondominiumResponse.from(condominiumRepository.save(condominium));
    }

    @Transactional
    public CondominiumResponse updateCondominium(UUID id, CondominiumRequest request) {
        Condominium condominium = condominiumRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Condominio no encontrado"));

        String code = normalize(request.code());
        if (!condominium.getCode().equalsIgnoreCase(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código no se puede modificar una vez creado");
        }

        condominium.update(
                request.name().trim(),
                request.country().trim(),
                request.city().trim(),
                request.address().trim(),
                request.latitude(),
                request.longitude(),
                request.status() == null ? CondominiumStatus.ACTIVE : request.status());
        return CondominiumResponse.from(condominiumRepository.save(condominium));
    }

    @Transactional
    public void deleteCondominium(UUID id) {
        Condominium condominium = condominiumRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Condominio no encontrado"));
        if (pointRepository.countByCondominiumId(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: primero elimina los puntos del condominio");
        }
        condominiumRepository.delete(condominium);
    }

    // ---------- Puntos ----------

    @Transactional(readOnly = true)
    public List<CondominiumPointResponse> listPoints() {
        return pointRepository.findAllByOrderByCodeAsc().stream()
                .map(CondominiumPointResponse::from)
                .toList();
    }

    @Transactional
    public CondominiumPointResponse createPoint(CondominiumPointRequest request) {
        Condominium condominium = findCondominium(request.condominiumId());
        String code = normalize(request.code());
        if (pointRepository.existsByCondominiumIdAndCodeIgnoreCase(condominium.getId(), code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un punto con ese código en el condominio");
        }

        CondominiumPoint point = new CondominiumPoint(
                condominium,
                code,
                request.name().trim(),
                request.pointType(),
                request.address() == null ? null : request.address().trim());
        point.updateDetails(
                code,
                request.name().trim(),
                request.pointType(),
                request.address() == null ? null : request.address().trim(),
                null,
                null,
                request.status() == null ? PointStatus.ACTIVE : request.status());
        return CondominiumPointResponse.from(pointRepository.save(point));
    }

    @Transactional
    public CondominiumPointResponse updatePoint(UUID id, CondominiumPointRequest request) {
        CondominiumPoint point = pointRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Punto no encontrado"));
        Condominium condominium = findCondominium(request.condominiumId());

        String code = normalize(request.code());
        if (!point.getCode().equalsIgnoreCase(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código no se puede modificar una vez creado");
        }

        point.updateDetails(
                code,
                request.name().trim(),
                request.pointType(),
                request.address() == null ? null : request.address().trim(),
                point.getLatitude(),
                point.getLongitude(),
                request.status() == null ? PointStatus.ACTIVE : request.status());
        // El condominio al que pertenece sí puede cambiar.
        point.setCondominium(condominium);
        return CondominiumPointResponse.from(pointRepository.save(point));
    }

    @Transactional
    public void deletePoint(UUID id) {
        if (!pointRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Punto no encontrado");
        }
        pointRepository.deleteById(id);
    }

    private Condominium findCondominium(UUID id) {
        return condominiumRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Condominio no encontrado"));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
