package pe.clubplayahonda.plh_backend.auth.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 100)
	private String passwordHash;

	@Column(name = "full_name", nullable = false, length = 120)
	private String fullName;

	@Column(name = "first_name", length = 60)
	private String firstName;

	@Column(name = "last_name", length = 60)
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserRole role;

	@Enumerated(EnumType.STRING)
	@Column(name = "employee_type", length = 30)
	private EmployeeType employeeType;

	/** Miembro de la directiva (aplica solo a propietarios: la directiva se forma con algunos de ellos). */
	@Column(name = "board_member", nullable = false)
	private boolean boardMember;

	/** Cargo dentro de la directiva (Presidente, Tesorero, etc.). */
	@Enumerated(EnumType.STRING)
	@Column(name = "board_position", length = 30)
	private BoardPosition boardPosition;

	/** Propiedad del propietario (número de lote / identificador de la casa). El QR físico vive en la tabla `properties`. */
	@Column(length = 100)
	private String property;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private PersonProfile profile;

	@Column(name = "document_type", length = 20)
	private String documentType;

	@Column(name = "document_number", length = 30)
	private String documentNumber;

	@Column(length = 30)
	private String phone;

	@Column(length = 255)
	private String address;

	@Column(nullable = false)
	private boolean enabled = true;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@PrePersist
	void onCreate() {
		createdAt = Instant.now();
	}

	protected User() {
	}

	public User(String email, String passwordHash, String firstName, String lastName, UserRole role) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
		this.firstName = normalizeName(firstName);
		this.lastName = normalizeName(lastName);
		this.fullName = buildFullName();
		this.profile = new PersonProfile(this, this.firstName, this.lastName);
	}

	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public PersonProfile getProfile() {
		if (profile == null) {
			profile = new PersonProfile(this, this.firstName, this.lastName);
		}
		return profile;
	}

	public void setProfile(PersonProfile profile) {
		this.profile = profile;
		if (profile != null) {
			this.firstName = profile.getFirstName();
			this.lastName = profile.getLastName();
			this.documentType = profile.getDocumentType();
			this.documentNumber = profile.getDocumentNumber();
			this.phone = profile.getPhone();
			this.address = profile.getAddress();
			this.fullName = buildFullName();
		}
	}

	public String getFullName() {
		if (profile != null && profile.getFirstName() != null && profile.getLastName() != null) {
			return (profile.getFirstName() + " " + profile.getLastName()).trim();
		}
		if (firstName != null && lastName != null) {
			return buildFullName();
		}
		return fullName;
	}

	public String getFirstName() {
		if (profile != null && profile.getFirstName() != null) {
			return profile.getFirstName();
		}
		if (firstName != null) {
			return firstName;
		}
		return legacyNamePart(true);
	}

	public String getLastName() {
		if (profile != null && profile.getLastName() != null) {
			return profile.getLastName();
		}
		if (lastName != null) {
			return lastName;
		}
		return legacyNamePart(false);
	}

	public UserRole getRole() {
		return role;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public EmployeeType getEmployeeType() {
		return employeeType;
	}

	public boolean isBoardMember() {
		return boardMember;
	}

	public BoardPosition getBoardPosition() {
		return boardPosition;
	}

	public String getProperty() {
		return property;
	}

	public String getDocumentType() {
		return documentType;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public String getPhone() {
		return phone;
	}

	public String getAddress() {
		return address;
	}

	public void updateProfile(
			String firstName,
			String lastName,
			String documentType,
			String documentNumber,
			String phone,
			String address) {
		this.firstName = normalizeName(firstName);
		this.lastName = normalizeName(lastName);
		this.fullName = buildFullName();
		this.documentType = normalize(documentType);
		this.documentNumber = normalize(documentNumber);
		this.phone = normalize(phone);
		this.address = normalize(address);
		if (profile == null) {
			profile = new PersonProfile(this, this.firstName, this.lastName);
		}
		profile.updateProfile(this.firstName, this.lastName, this.documentType, this.documentNumber, this.phone, this.address);
	}

	public void updateAdministrativeProfile(
			String firstName,
			String lastName,
			UserRole role,
			EmployeeType employeeType,
			String documentType,
			String documentNumber,
			String phone,
			String address,
			boolean boardMember,
			BoardPosition boardPosition,
			String property) {
		updateProfile(firstName, lastName, documentType, documentNumber, phone, address);
		this.role = role;
		this.employeeType = employeeType;
		// La directiva se forma con propietarios: el marcador solo aplica si el rol es OWNER.
		this.boardMember = boardMember && role == UserRole.OWNER;
		this.boardPosition = (this.boardMember || role == UserRole.BOARD) ? boardPosition : null;
		this.property = normalize(property);
	}

	private String normalize(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}

	private String normalizeName(String value) {
		String normalized = normalize(value);
		return normalized == null ? "" : normalized;
	}

	private String buildFullName() {
		String resolvedFirst = firstName == null ? "" : firstName.trim();
		String resolvedLast = lastName == null ? "" : lastName.trim();
		return (resolvedFirst + " " + resolvedLast).trim();
	}

	private String legacyNamePart(boolean first) {
		if (fullName == null || fullName.isBlank()) {
			return null;
		}
		String[] parts = fullName.trim().split("\\s+", 2);
		return first ? parts[0] : parts.length > 1 ? parts[1] : parts[0];
	}
}
