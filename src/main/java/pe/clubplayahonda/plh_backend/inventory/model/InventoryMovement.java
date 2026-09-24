package pe.clubplayahonda.plh_backend.inventory.model;

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
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InventoryMovementType type;

    @Column(nullable = false)
    private int quantity;

    @Column(length = 120)
    private String responsiblePerson;

    @Column(length = 120)
    private String destinationArea;

    @Column(length = 300)
    private String notes;

    @Column(name = "movement_at", nullable = false)
    private LocalDateTime movementAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "received_by", length = 120)
    private String receivedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected InventoryMovement() {
    }

    public InventoryMovement(InventoryItem item, InventoryMovementType type, int quantity,
                            String responsiblePerson, String destinationArea, String notes,
                            LocalDateTime movementAt, LocalDateTime returnedAt, String receivedBy) {
        this.item = item;
        this.type = type;
        this.quantity = quantity;
        this.responsiblePerson = responsiblePerson;
        this.destinationArea = destinationArea;
        this.notes = notes;
        this.movementAt = movementAt;
        this.returnedAt = returnedAt;
        this.receivedBy = receivedBy;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public InventoryItem getItem() {
        return item;
    }

    public InventoryMovementType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public String getDestinationArea() {
        return destinationArea;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getMovementAt() {
        return movementAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }
}
