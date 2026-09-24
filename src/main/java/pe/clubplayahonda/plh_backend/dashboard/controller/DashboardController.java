package pe.clubplayahonda.plh_backend.dashboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.clubplayahonda.plh_backend.dashboard.dto.DashboardSummary;
import pe.clubplayahonda.plh_backend.dashboard.service.DashboardService;

/**
 * Resumen operativo para el dashboard (data real de todos los módulos).
 * Cualquier usuario autenticado puede ver los contadores generales.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummary summary() {
        return dashboardService.summary();
    }
}
