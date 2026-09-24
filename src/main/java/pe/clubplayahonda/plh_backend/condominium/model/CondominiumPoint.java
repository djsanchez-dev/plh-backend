package pe.clubplayahonda.plh_backend.condominium.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "condominium_points")
public class CondominiumPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condominium_id", nullable = false)
    private Condominium condominium;

    @Column(nullable = false, length = 60)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false, length = 32)
    private PointType pointType;

    @Column(length = 255)
    private String address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PointStatus status = PointStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_point_id")
    private CondominiumPoint parentPoint;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CondominiumPoint() {
    }

    public CondominiumPoint(Condominium condominium, String code, String name, PointType pointType, String address) {
        this.condominium = condominium;
        this.code = code;
        this.name = name;
        this.pointType = pointType;
        this.address = address;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public Condominium getCondominium() { return condominium; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public PointType getPointType() { return pointType; }
    public String getAddress() { return address; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public PointStatus getStatus() { return status; }
    public CondominiumPoint getParentPoint() { return parentPoint; }
    public Instant getCreatedAt() { return createdAt; }

    public void setCondominium(Condominium condominium) {
        this.condominium = condominium;
    }

    public void updateDetails(
            String code,
            String name,
            PointType pointType,
            String address,
            Double latitude,
            Double longitude,
            PointStatus status) {
        this.code = code;
        this.name = name;
        this.pointType = pointType;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }
}
