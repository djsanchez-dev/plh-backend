package pe.clubplayahonda.plh_backend.inventory.model;

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

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InventoryCategory category;

    @Column(nullable = false)
    private int quantity;

    /**
     * Stock mínimo: al llegar a esta cantidad el artículo aparece como "stock bajo".
     * Se usa Integer (columna anulable) para que el backend arranque aunque la columna
     * se agregue sobre filas existentes todavía sin valor.
     */
    @Column(name = "min_stock")
    private Integer minStock;

    @Column(length = 40)
    private String unit;

    @Column(length = 120)
    private String location;

    @Column(length = 200)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected InventoryItem() {
    }

    public InventoryItem(String code, String name, InventoryCategory category, int quantity, int minStock,
                         String unit, String location, String notes) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.minStock = Math.max(0, minStock);
        this.unit = unit;
        this.location = location;
        this.notes = notes;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public InventoryCategory getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMinStock() {
        return minStock == null ? 0 : minStock;
    }

    public String getUnit() {
        return unit;
    }

    public String getLocation() {
        return location;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isLowStock() {
        int resolvedMinStock = getMinStock();
        return resolvedMinStock > 0 && quantity <= resolvedMinStock;
    }

    public void updateDetails(String code, String name, InventoryCategory category, int quantity, int minStock,
                              String unit, String location, String notes) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.minStock = Math.max(0, minStock);
        this.unit = unit;
        this.location = location;
        this.notes = notes;
        this.updatedAt = Instant.now();
    }

    public void adjustQuantity(int delta) {
        this.quantity = Math.max(0, this.quantity + delta);
        this.updatedAt = Instant.now();
    }
}
