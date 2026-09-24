package pe.clubplayahonda.plh_backend.employee.service;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.clubplayahonda.plh_backend.auth.model.EmployeeType;
import pe.clubplayahonda.plh_backend.employee.dto.EmployeeRequest;
import pe.clubplayahonda.plh_backend.employee.dto.EmployeeResponse;
import pe.clubplayahonda.plh_backend.employee.model.Employee;
import pe.clubplayahonda.plh_backend.employee.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> listEmployees() {
        return employeeRepository.findAllByOrderByLastNameAscFirstNameAsc().stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (normalizedEmail != null && employeeRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un empleado con ese correo");
        }

        Employee employee = new Employee(
                request.firstName(),
                request.lastName(),
                normalizedEmail,
                validateEmployeeType(request.employeeType()),
                request.documentType(),
                request.documentNumber(),
                request.phone(),
                request.address(),
                request.active() != null ? request.active() : true);

        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse updateEmployee(UUID id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        String normalizedEmail = normalizeEmail(request.email());
        employeeRepository.findByEmailIgnoreCase(normalizedEmail)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un empleado con ese correo");
                });

        employee.updateDetails(
                request.firstName(),
                request.lastName(),
                normalizedEmail,
                validateEmployeeType(request.employeeType()),
                request.documentType(),
                request.documentNumber(),
                request.phone(),
                request.address(),
                request.active() != null ? request.active() : employee.isActive());

        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado");
        }
        employeeRepository.deleteById(id);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim();
    }

    private EmployeeType validateEmployeeType(EmployeeType employeeType) {
        if (employeeType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de empleado es obligatorio");
        }
        return employeeType;
    }
}
