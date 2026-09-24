package pe.clubplayahonda.plh_backend.auth.dto;

import java.util.UUID;
import pe.clubplayahonda.plh_backend.auth.model.User;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String fullName,
        String role,
        String employeeType,
        String documentType,
        String documentNumber,
        String phone,
        String address,
        boolean boardMember,
        String boardPosition,
        String property,
        boolean enabled) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getRole().name(),
                user.getEmployeeType() == null ? null : user.getEmployeeType().name(),
                user.getDocumentType(),
                user.getDocumentNumber(),
                user.getPhone(),
                user.getAddress(),
                user.isBoardMember(),
                user.getBoardPosition() == null ? null : user.getBoardPosition().name(),
                user.getProperty(),
                user.isEnabled());
    }
}
