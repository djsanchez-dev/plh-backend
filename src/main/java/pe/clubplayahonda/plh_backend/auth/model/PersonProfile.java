package pe.clubplayahonda.plh_backend.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "person_profiles")
public class PersonProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "first_name", nullable = false, length = 60)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 60)
	private String lastName;

	@Column(name = "document_type", length = 20)
	private String documentType;

	@Column(name = "document_number", length = 30)
	private String documentNumber;

	@Column(length = 30)
	private String phone;

	@Column(length = 255)
	private String address;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	protected PersonProfile() {
	}

	public PersonProfile(User user, String firstName, String lastName) {
		this.user = user;
		this.firstName = firstName == null ? "" : firstName.trim();
		this.lastName = lastName == null ? "" : lastName.trim();
		this.createdAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
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

	public void updateProfile(String firstName, String lastName, String documentType, String documentNumber, String phone, String address) {
		this.firstName = firstName == null ? "" : firstName.trim();
		this.lastName = lastName == null ? "" : lastName.trim();
		this.documentType = normalize(documentType);
		this.documentNumber = normalize(documentNumber);
		this.phone = normalize(phone);
		this.address = normalize(address);
	}

	private String normalize(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
