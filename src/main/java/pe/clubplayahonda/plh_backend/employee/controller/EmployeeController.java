package pe.clubplayahonda.plh_backend.employee.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.clubplayahonda.plh_backend.employee.dto.EmployeeRequest;
import pe.clubplayahonda.plh_backend.employee.dto.EmployeeResponse;
import pe.clubplayahonda.plh_backend.employee.service.EmployeeService;

/**
 * Plantilla de empleados del condominio (fichas sin cuenta de acceso).
 *
 * Lectura: ADMIN, ASISTENTE ADMIN, DIRECTIVA y EMPLEADO (inventario usa la lista de responsables).
 * Escritura: solo ADMIN y ASISTENTE ADMIN.
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD','EMPLOYEE')")
    public List<EmployeeResponse> list() {
        return employeeService.listEmployees();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public EmployeeResponse update(@PathVariable UUID id, @Valid @RequestBody EmployeeRequest request) {
        return employeeService.updateEmployee(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public void delete(@PathVariable UUID id) {
        this.employeeService.deleteEmployee(id);
    }
}
