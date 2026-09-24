package pe.clubplayahonda.plh_backend.auth.controller;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.clubplayahonda.plh_backend.auth.dto.AdminPasswordResetRequest;
import pe.clubplayahonda.plh_backend.auth.dto.AdminPersonRequest;
import pe.clubplayahonda.plh_backend.auth.dto.AdminPersonUpdateRequest;
import pe.clubplayahonda.plh_backend.auth.dto.UserResponse;
import pe.clubplayahonda.plh_backend.auth.model.UserRole;
import pe.clubplayahonda.plh_backend.auth.service.AuthService;

/**
 * Administración de personas.
 *
 * Lectura (listados): ADMIN, ASSISTANT_ADMIN y BOARD (la directiva, formada por
 * propietarios, solo consulta).
 * Escritura (crear/editar/borrar/aprobar/restablecer): solo ADMIN y ASSISTANT_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/people")
public class AdminPersonController {

    private final AuthService authService;

    public AdminPersonController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD')")
    public List<UserResponse> list(Authentication authentication) {
        return authService.listPeople(authentication.getName());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public List<UserResponse> listPending(Authentication authentication) {
        return authService.listPendingPeople(authentication.getName());
    }

    @GetMapping("/owners")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD')")
    public List<UserResponse> listOwners(Authentication authentication) {
        return authService.listPeople(authentication.getName(), UserRole.OWNER);
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD')")
    public List<UserResponse> listEmployees(Authentication authentication) {
        return authService.listPeople(authentication.getName(), UserRole.EMPLOYEE, UserRole.ASSISTANT_ADMIN);
    }

    @GetMapping("/directives")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN','BOARD')")
    public List<UserResponse> listDirectives(Authentication authentication) {
        return authService.listBoardMembers(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public UserResponse create(
            Authentication authentication,
            @Valid @RequestBody AdminPersonRequest request) {
        return authService.createPerson(request, authentication.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public UserResponse update(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody AdminPersonUpdateRequest request) {
        return authService.updatePerson(id, request, authentication.getName());
    }

    @PutMapping("/{id}/enabled")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public UserResponse updateEnabled(
            @PathVariable UUID id,
            @RequestParam boolean enabled,
            Authentication authentication) {
        return authService.setPersonEnabled(id, enabled, authentication.getName());
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public UserResponse resetPassword(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody AdminPasswordResetRequest request) {
        return authService.resetPassword(id, request.newPassword(), authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','ASSISTANT_ADMIN')")
    public void delete(
            @PathVariable UUID id,
            Authentication authentication) {
        authService.deletePerson(id, authentication.getName());
    }
}
