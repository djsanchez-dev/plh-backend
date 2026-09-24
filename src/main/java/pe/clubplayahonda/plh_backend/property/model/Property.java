package pe.clubplayahonda.plh_backend.property.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import pe.clubplayahonda.plh_backend.auth.model.User;

/**
 * Propiedad física del condominio (lote / casa).
 * El QR pertenece a la propiedad, no al propietario: si cambia el dueño,
 * la etiqueta impresa sigue funcionando (el registro se desvincula y se reclama).
 */
@Entity
@Table(name = "properties")
public class Property {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	/** Código opaco que codifica el QR (la URL /m/{qrCode}). No expone datos. */
	@Column(name = "qr_code", nullable = false, unique = true)
	private UUID qrCode;

	/** Número de lote / identificador visible de la casa. */
	@Column(length = 100)
	private String lot;

	/** Propietario actual; nulo si la casa aún no tiene dueño registrado. */
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id", unique = true)
	private User owner;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@PrePersist
	void onCreate() {
		createdAt = Instant.now();
		if (qrCode == null) {
			qrCode = UUID.randomUUID();
		}
	}

	protected Property() {
	}

	public Property(String lot) {
		this.lot = normalize(lot);
		this.qrCode = UUID.randomUUID();
	}

	public UUID getId() {
		return id;
	}

	public UUID getQrCode() {
		return qrCode;
	}

	public String getLot() {
		return lot;
	}

	public User getOwner() {
		return owner;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setLot(String lot) {
		this.lot = normalize(lot);
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	private String normalize(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
