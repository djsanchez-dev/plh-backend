package pe.clubplayahonda.plh_backend.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.clubplayahonda.plh_backend.auth.model.EmployeeType;

public record EmployeeRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 60, message = "El nombre no puede superar 60 caracteres")
        String firstName,
        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 60, message = "El apellido no puede superar 60 caracteres")
        String lastName,
        @Email(message = "El correo no es válido")
        @Size(max = 254, message = "El correo no puede superar 254 caracteres")
        String email,
        @NotNull(message = "El tipo de empleado es obligatorio")
        EmployeeType employeeType,
        @Size(max = 20, message = "El tipo de documento no puede superar 20 caracteres")
        String documentType,
        @Size(max = 30, message = "El número de documento no puede superar 30 caracteres")
        String documentNumber,
        @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
        String phone,
        @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
        String address,
        Boolean active) {
}
