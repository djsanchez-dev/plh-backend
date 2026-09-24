package pe.clubplayahonda.plh_backend.employee.dto;

import java.util.UUID;
import pe.clubplayahonda.plh_backend.auth.model.EmployeeType;
import pe.clubplayahonda.plh_backend.employee.model.Employee;

public record EmployeeResponse(
        UUID id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        EmployeeType employeeType,
        String documentType,
        String documentNumber,
        String phone,
        String address,
        boolean active) {

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getEmployeeType(),
                employee.getDocumentType(),
                employee.getDocumentNumber(),
                employee.getPhone(),
                employee.getAddress(),
                employee.isActive());
    }
}
