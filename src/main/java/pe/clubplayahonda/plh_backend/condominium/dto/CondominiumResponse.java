package pe.clubplayahonda.plh_backend.condominium.dto;

import java.util.UUID;
import pe.clubplayahonda.plh_backend.condominium.model.Condominium;

public record CondominiumResponse(
        UUID id,
        String code,
        String name,
        String country,
        String city,
        String address,
        Double latitude,
        Double longitude,
        String status) {

    public static CondominiumResponse from(Condominium condominium) {
        return new CondominiumResponse(
                condominium.getId(),
                condominium.getCode(),
                condominium.getName(),
                condominium.getCountry(),
                condominium.getCity(),
                condominium.getAddress(),
                condominium.getLatitude(),
                condominium.getLongitude(),
                condominium.getStatus() == null ? "ACTIVE" : condominium.getStatus().name());
    }
}
