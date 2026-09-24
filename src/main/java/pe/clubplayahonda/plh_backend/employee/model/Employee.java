package pe.clubplayahonda.plh_backend.employee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import pe.clubplayahonda.plh_backend.auth.model.EmployeeType;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 60)
    private String firstName;

    @Column(nullable = false, length = 60)
    private String lastName;

    @Column(length = 120)
    private String fullName;

    @Column(length = 254)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type", nullable = false, length = 30)
    private EmployeeType employeeType;

    @Column(name = "document_type", length = 20)
    private String documentType;

    @Column(name = "document_number", length = 30)
    private String documentNumber;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Employee() {
    }

    public Employee(
            String firstName,
            String lastName,
            String email,
            EmployeeType employeeType,
            String documentType,
            String documentNumber,
            String phone,
            String address,
            boolean active) {
        this.firstName = normalizeName(firstName);
        this.lastName = normalizeName(lastName);
        this.email = normalizeEmail(email);
        this.employeeType = employeeType;
        this.documentType = normalize(documentType);
        this.documentNumber = normalize(documentNumber);
        this.phone = normalize(phone);
        this.address = normalize(address);
        this.active = active;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.fullName = buildFullName();
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
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

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateDetails(
            String firstName,
            String lastName,
            String email,
            EmployeeType employeeType,
            String documentType,
            String documentNumber,
            String phone,
            String address,
            boolean active) {
        this.firstName = normalizeName(firstName);
        this.lastName = normalizeName(lastName);
        this.email = normalizeEmail(email);
        this.employeeType = employeeType;
        this.documentType = normalize(documentType);
        this.documentNumber = normalize(documentNumber);
        this.phone = normalize(phone);
        this.address = normalize(address);
        this.active = active;
        this.fullName = buildFullName();
        this.updatedAt = Instant.now();
    }

    private String buildFullName() {
        return (firstName + " " + lastName).trim();
    }

    private String normalizeName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        String normalized = value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        return normalized;
    }

    private String normalizeEmail(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
