package pe.clubplayahonda.plh_backend.condominium.dto;

import java.util.UUID;
import pe.clubplayahonda.plh_backend.condominium.model.CondominiumPoint;

public record CondominiumPointResponse(
        UUID id,
        UUID condominiumId,
        String condominiumName,
        String code,
        String name,
        String pointType,
        String address,
        String status) {

    public static CondominiumPointResponse from(CondominiumPoint point) {
        return new CondominiumPointResponse(
                point.getId(),
                point.getCondominium().getId(),
                point.getCondominium().getName(),
                point.getCode(),
                point.getName(),
                point.getPointType().name(),
                point.getAddress(),
                point.getStatus() == null ? "ACTIVE" : point.getStatus().name());
    }
}
