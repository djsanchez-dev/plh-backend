package pe.clubplayahonda.plh_backend.dashboard.dto;

import java.util.List;
import java.util.Map;
import pe.clubplayahonda.plh_backend.inventory.dto.InventoryMovementResponse;

/**
 * Resumen operativo del condominio para el dashboard.
 * Todos los valores salen de la base de datos: sin datos de prueba.
 */
public record DashboardSummary(
        UserStats users,
        StaffStats staff,
        InventoryStats inventory,
        ConsumptionStats consumption,
        CommunityStats community,
        List<InventoryMovementResponse> recentMovements) {

    public record UserStats(
            long total,
            long owners,
            long employeeAccounts,
            long residents,
            long board,
            long admins,
            long assistantAdmins,
            long pendingApproval) {
    }

    public record StaffStats(
            long total,
            long active,
            long inactive,
            Map<String, Long> byType) {
    }

    public record InventoryStats(
            long totalItems,
            long lowStock,
            long outOfStock,
            Map<String, Long> byCategory) {
    }

    public record ConsumptionStats(
            double currentMonthWater,
            double currentMonthKwh,
            double previousMonthWater,
            double previousMonthKwh,
            long currentMonthRecords) {
    }

    public record CommunityStats(
            long condominiums,
            long activeCondominiums,
            long points) {
    }
}
