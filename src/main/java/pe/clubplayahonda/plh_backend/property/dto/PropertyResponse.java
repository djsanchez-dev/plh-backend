package pe.clubplayahonda.plh_backend.property.dto;

import java.util.UUID;

/** Datos mínimos para imprimir la etiqueta QR de una propiedad. */
public record PropertyResponse(UUID id, UUID qrCode, String lot, String ownerName) {
}
