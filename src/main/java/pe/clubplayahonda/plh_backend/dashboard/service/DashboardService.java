package pe.clubplayahonda.plh_backend.dashboard.service;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.clubplayahonda.plh_backend.auth.model.UserRole;
import pe.clubplayahonda.plh_backend.auth.repository.UserRepository;
import pe.clubplayahonda.plh_backend.condominium.model.CondominiumStatus;
import pe.clubplayahonda.plh_backend.condominium.repository.CondominiumPointRepository;
import pe.clubplayahonda.plh_backend.condominium.repository.CondominiumRepository;
import pe.clubplayahonda.plh_backend.dashboard.dto.DashboardSummary;
import pe.clubplayahonda.plh_backend.employee.model.Employee;
import pe.clubplayahonda.plh_backend.employee.repository.EmployeeRepository;
import pe.clubplayahonda.plh_backend.inventory.dto.InventoryMovementResponse;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryCategory;
import pe.clubplayahonda.plh_backend.inventory.model.InventoryItem;
import pe.clubplayahonda.plh_backend.inventory.repository.InventoryItemRepository;
import pe.clubplayahonda.plh_backend.inventory.repository.InventoryMovementRepository;
import pe.clubplayahonda.plh_backend.services.repository.UtilityConsumptionRepository;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final UtilityConsumptionRepository consumptionRepository;
    private final CondominiumRepository condominiumRepository;
    private final CondominiumPointRepository pointRepository;

    public DashboardService(UserRepository userRepository,
                            EmployeeRepository employeeRepository,
                            InventoryItemRepository inventoryItemRepository,
                            InventoryMovementRepository inventoryMovementRepository,
                            UtilityConsumptionRepository consumptionRepository,
                            CondominiumRepository condominiumRepository,
                            CondominiumPointRepository pointRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.consumptionRepository = consumptionRepository;
        this.condominiumRepository = condominiumRepository;
        this.pointRepository = pointRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummary summary() {
        return new DashboardSummary(
                userStats(),
                staffStats(),
                inventoryStats(),
                consumptionStats(),
                communityStats(),
                recentMovements());
    }

    private DashboardSummary.UserStats userStats() {
        return new DashboardSummary.UserStats(
                userRepository.count(),
                userRepository.countByRole(UserRole.OWNER),
                userRepository.countByRole(UserRole.EMPLOYEE),
                userRepository.countByRole(UserRole.RESIDENT),
                userRepository.countByRoleOrBoardMember(UserRole.BOARD),
                userRepository.countByRole(UserRole.ADMIN),
                userRepository.countByRole(UserRole.ASSISTANT_ADMIN),
                userRepository.countByEnabled(false));
    }

    private DashboardSummary.StaffStats staffStats() {
        long total = 0;
        long active = 0;
        Map<pe.clubplayahonda.plh_backend.auth.model.EmployeeType, Long> raw = new EnumMap<>(
                pe.clubplayahonda.plh_backend.auth.model.EmployeeType.class);
        for (Employee employee : employeeRepository.findAll()) {
            total++;
            if (employee.isActive()) {
                active++;
            }
            raw.merge(employee.getEmployeeType(), 1L, Long::sum);
        }
        Map<String, Long> byType = new LinkedHashMap<>();
        raw.forEach((type, count) -> byType.put(type.name(), count));
        return new DashboardSummary.StaffStats(total, active, Math.max(0, total - active), byType);
    }

    private DashboardSummary.InventoryStats inventoryStats() {
        long total = 0;
        long lowStock = 0;
        long outOfStock = 0;
        Map<InventoryCategory, Long> raw = new EnumMap<>(InventoryCategory.class);
        for (InventoryItem item : inventoryItemRepository.findAll()) {
            total++;
            raw.merge(item.getCategory(), 1L, Long::sum);
            if (item.getQuantity() == 0) {
                outOfStock++;
            } else if (item.isLowStock()) {
                lowStock++;
            }
        }
        Map<String, Long> byCategory = new LinkedHashMap<>();
        raw.forEach((category, count) -> byCategory.put(category.name(), count));
        return new DashboardSummary.InventoryStats(total, lowStock, outOfStock, byCategory);
    }

    private DashboardSummary.ConsumptionStats consumptionStats() {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1);
        LocalDate previousStart = monthStart.minusMonths(1);

        return new DashboardSummary.ConsumptionStats(
                consumptionRepository.sumWaterBetween(monthStart, monthEnd),
                consumptionRepository.sumElectricityBetween(monthStart, monthEnd),
                consumptionRepository.sumWaterBetween(previousStart, monthStart),
                consumptionRepository.sumElectricityBetween(previousStart, monthStart),
                consumptionRepository.countByPeriodStartGreaterThanEqualAndPeriodStartLessThan(monthStart, monthEnd));
    }

    private DashboardSummary.CommunityStats communityStats() {
        long total = condominiumRepository.count();
        long active = condominiumRepository.countByStatus(CondominiumStatus.ACTIVE);
        return new DashboardSummary.CommunityStats(total, active, pointRepository.count());
    }

    private java.util.List<InventoryMovementResponse> recentMovements() {
        return inventoryMovementRepository.findTop5ByOrderByMovementAtDesc().stream()
                .map(InventoryMovementResponse::from)
                .toList();
    }
}
