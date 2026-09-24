package pe.clubplayahonda.plh_backend.auth.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.clubplayahonda.plh_backend.auth.dto.AdminPersonRequest;
import pe.clubplayahonda.plh_backend.auth.dto.AdminPersonUpdateRequest;
import pe.clubplayahonda.plh_backend.auth.dto.AuthResponse;
import pe.clubplayahonda.plh_backend.auth.dto.ChangePasswordRequest;
import pe.clubplayahonda.plh_backend.auth.dto.LoginRequest;
import pe.clubplayahonda.plh_backend.auth.dto.ProfileUpdateRequest;
import pe.clubplayahonda.plh_backend.auth.dto.RegisterRequest;
import pe.clubplayahonda.plh_backend.auth.dto.UserResponse;
import pe.clubplayahonda.plh_backend.auth.model.BoardPosition;
import pe.clubplayahonda.plh_backend.auth.model.EmployeeType;
import pe.clubplayahonda.plh_backend.auth.model.User;
import pe.clubplayahonda.plh_backend.auth.model.UserRole;
import pe.clubplayahonda.plh_backend.auth.repository.UserRepository;
import pe.clubplayahonda.plh_backend.auth.security.JwtService;
import pe.clubplayahonda.plh_backend.property.model.Property;
import pe.clubplayahonda.plh_backend.property.repository.PropertyRepository;
import pe.clubplayahonda.plh_backend.services.repository.UtilityConsumptionRepository;

@Service
public class AuthService {

    /** Roles con permisos de administración de personas. */
    private static final Set<UserRole> MANAGER_ROLES = Set.of(UserRole.ADMIN, UserRole.ASSISTANT_ADMIN);

    /** Roles con permiso de lectura (la directiva solo consulta). */
    private static final Set<UserRole> READER_ROLES = Set.of(UserRole.ADMIN, UserRole.ASSISTANT_ADMIN, UserRole.BOARD);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilityConsumptionRepository consumptionRepository;
    private final PropertyRepository propertyRepository;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UtilityConsumptionRepository consumptionRepository,
            PropertyRepository propertyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.consumptionRepository = consumptionRepository;
        this.propertyRepository = propertyRepository;
    }

    /**
     * Registro público: la cuenta nace DESHABILITADA y un administrador debe aprobarla.
     * Excepción: si el sistema está completamente vacío, el primer registro se convierte
     * en el ADMIN inicial (para no quedarse sin nadie que pueda activar cuentas).
     */
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        boolean bootstrap = userRepository.count() == 0;
        User user = new User(
                email,
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                bootstrap ? UserRole.ADMIN : UserRole.RESIDENT);
        if (!bootstrap) {
            user.setEnabled(false);
        }
        User saved = userRepository.save(user);
        if (!saved.isEnabled()) {
            // Cuenta pendiente de aprobación: NO se emite token todavía.
            // El administrador la activa desde el panel y recién ahí puede iniciar sesión.
            return new AuthResponse(
                    null,
                    "Bearer",
                    0,
                    saved.getId(),
                    saved.getEmail(),
                    saved.getFirstName(),
                    saved.getLastName(),
                    saved.getFullName(),
                    saved.getRole().name());
        }
        return createAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        return createAuthResponse(user);
    }

    public UserResponse currentUser(String email) {
        return UserResponse.from(findByEmail(email));
    }

    public UserResponse updateCurrentUser(String email, ProfileUpdateRequest request) {
        User user = findByEmail(email);
        user.updateProfile(
                request.firstName(),
                request.lastName(),
                request.documentType(),
                request.documentNumber(),
                request.phone(),
                request.address());
        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse changeOwnPassword(String email, ChangePasswordRequest request) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual no es correcta");
        }
        if (request.newPassword().equals(request.currentPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña nueva debe ser distinta a la actual");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        return UserResponse.from(userRepository.save(user));
    }

    public List<UserResponse> listPeople(String requesterEmail) {
        return listPeople(requesterEmail, UserRole.values());
    }

    public List<UserResponse> listPeople(String requesterEmail, UserRole... roles) {
        ensureReader(requesterEmail);
        List<UserRole> allowedRoles = roles == null || roles.length == 0
                ? Arrays.asList(UserRole.values())
                : Arrays.asList(roles);

        return userRepository.findAll().stream()
                .filter(user -> allowedRoles.contains(user.getRole()))
                .map(UserResponse::from)
                .toList();
    }

    /** Cuentas creadas por registro público que esperan aprobación. */
    public List<UserResponse> listPendingPeople(String requesterEmail) {
        ensureReader(requesterEmail);
        return userRepository.findByEnabledFalseOrderByCreatedAtAsc().stream()
                .map(UserResponse::from)
                .toList();
    }

    /**
     * Directiva del condominio: propietarios marcados como miembros (más los usuarios
     * con rol BOARD heredados). La directiva está conformada por algunos propietarios.
     */
    public List<UserResponse> listBoardMembers(String requesterEmail) {
        ensureReader(requesterEmail);
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.BOARD || user.isBoardMember())
                .sorted(byLastNameThenFirstName())
                .map(UserResponse::from)
                .toList();
    }

    public void deletePerson(UUID id, String requesterEmail) {
        User requester = ensureManager(requesterEmail);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));

        if (user.getId().equals(requester.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes eliminar tu propia cuenta");
        }
        if (consumptionRepository.countByOwnerId(user.getId()) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: la persona tiene registros de consumo asociados");
        }
        // La propiedad física sobrevive al propietario: solo se desvincula (el QR impreso sigue siendo válido).
        propertyRepository.saveAll(propertyRepository.findByOwnerId(user.getId()).stream()
                .peek(property -> property.setOwner(null))
                .toList());
        userRepository.delete(user);
    }

    public UserResponse createPerson(AdminPersonRequest request, String requesterEmail) {
        ensureManager(requesterEmail);
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        validateEmployeeType(request.role(), request.employeeType());
        if (request.role() == UserRole.OWNER && isBlank(request.property())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La propiedad (número de lote) es obligatoria para los propietarios");
        }
        boolean boardMember = resolveBoardMember(request.role(), request.boardMember());
        BoardPosition boardPosition = resolveBoardPosition(request.role(), boardMember, request.boardPosition());

        User user = new User(
                email,
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                request.role());
        user.updateAdministrativeProfile(
                request.firstName(),
                request.lastName(),
                request.role(),
                request.employeeType(),
                request.documentType(),
                request.documentNumber(),
                request.phone(),
                request.address(),
                boardMember,
                boardPosition,
                request.property());
        // Creada por un administrador: nace activa.
        user.setEnabled(true);
        User saved = userRepository.save(user);
        syncProperty(saved, request.role(), request.property());
        return UserResponse.from(saved);
    }

    public UserResponse updatePerson(UUID id, AdminPersonUpdateRequest request, String requesterEmail) {
        ensureManager(requesterEmail);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
        ensureCanManageRole(request.role());
        validateEmployeeType(request.role(), request.employeeType());
        boolean boardMember = resolveBoardMember(request.role(), request.boardMember());
        BoardPosition boardPosition = resolveBoardPosition(request.role(), boardMember, request.boardPosition());

        user.updateAdministrativeProfile(
                request.firstName(),
                request.lastName(),
                request.role(),
                request.employeeType(),
                request.documentType(),
                request.documentNumber(),
                request.phone(),
                request.address(),
                boardMember,
                boardPosition,
                request.property());
        User saved = userRepository.save(user);
        syncProperty(saved, request.role(), request.property());
        return UserResponse.from(saved);
    }

    /** Activa o desactiva el acceso de una cuenta (aprobación de registro y bloqueos). */
    public UserResponse setPersonEnabled(UUID id, boolean enabled, String requesterEmail) {
        User requester = ensureManager(requesterEmail);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
        if (!enabled && user.getId().equals(requester.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes desactivar tu propia cuenta");
        }
        user.setEnabled(enabled);
        return UserResponse.from(userRepository.save(user));
    }

    /** Un administrador restablece la contraseña de cualquier cuenta. */
    public UserResponse resetPassword(UUID id, String newPassword, String requesterEmail) {
        ensureManager(requesterEmail);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return UserResponse.from(userRepository.save(user));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    /** Valida que quien pide administración sea ADMIN o ASISTENTE ADMIN. */
    private User ensureManager(String requesterEmail) {
        return ensureRole(requesterEmail, MANAGER_ROLES, "No tienes permisos para administrar personas");
    }

    /** Valida que quien pide lectura sea ADMIN, ASISTENTE ADMIN o DIRECTIVA. */
    private User ensureReader(String requesterEmail) {
        return ensureRole(requesterEmail, READER_ROLES, "No tienes permisos para consultar las personas");
    }

    private User ensureRole(String requesterEmail, Set<UserRole> allowedRoles, String message) {
        if (requesterEmail == null || requesterEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        User requester = userRepository.findByEmailIgnoreCase(requesterEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));
        if (!allowedRoles.contains(requester.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
        return requester;
    }

    private void ensureCanManageRole(UserRole role) {
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol es obligatorio");
        }
    }

    private void validateEmployeeType(UserRole role, EmployeeType employeeType) {
        boolean employeeRole = role == UserRole.EMPLOYEE || role == UserRole.ASSISTANT_ADMIN;
        if (employeeRole && employeeType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de empleado es obligatorio para este rol");
        }
        if (!employeeRole && employeeType != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de empleado solo aplica a empleados");
        }
    }

    /** La directiva se forma con propietarios: el marcador solo aplica si el rol es OWNER. */
    private boolean resolveBoardMember(UserRole role, Boolean boardMember) {
        return role == UserRole.OWNER && Boolean.TRUE.equals(boardMember);
    }

    /** El cargo es obligatorio para todo miembro de directiva (propietario marcado o rol BOARD). */
    private BoardPosition resolveBoardPosition(UserRole role, boolean boardMember, BoardPosition boardPosition) {
        boolean board = boardMember || role == UserRole.BOARD;
        if (board && boardPosition == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cargo en la directiva es obligatorio");
        }
        return board ? boardPosition : null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Mantiene el registro físico de la propiedad (el QR) vinculado al propietario.
     * - Reclama una casa libre si el lote coincide (conserva el QR ya impreso).
     * - Si el rol deja de ser OWNER, solo desvincula: la propiedad permanece en el sistema.
     */
    private void syncProperty(User user, UserRole role, String lot) {
        if (role != UserRole.OWNER) {
            propertyRepository.saveAll(propertyRepository.findByOwnerId(user.getId()).stream()
                    .peek(property -> property.setOwner(null))
                    .toList());
            return;
        }
        if (isBlank(lot)) {
            return; // Sin lote no hay con qué identificar la casa: no se destruye el vínculo existente.
        }
        String normalized = lot.trim();
        Property owned = propertyRepository.findFirstByOwnerId(user.getId()).orElse(null);
        if (owned != null) {
            owned.setLot(normalized);
            propertyRepository.save(owned);
            return;
        }
        // ¿Existe ya una casa libre con ese lote? (por ejemplo, tras eliminar un propietario anterior)
        Property property = propertyRepository.findFirstByOwnerIsNullAndLotIgnoreCase(normalized)
                .orElseGet(() -> new Property(normalized));
        property.setLot(normalized);
        property.setOwner(user);
        propertyRepository.save(property);
    }

    private AuthResponse createAuthResponse(User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .disabled(!user.isEnabled())
                .build();
        return new AuthResponse(
                jwtService.generateToken(userDetails),
                "Bearer",
                jwtService.getExpirationMs(),
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getRole().name());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    // Ordenación por apellido usada en listados.
    static Comparator<User> byLastNameThenFirstName() {
        return Comparator.comparing((User user) -> user.getLastName() == null ? "" : user.getLastName(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(user -> user.getFirstName() == null ? "" : user.getFirstName(), String.CASE_INSENSITIVE_ORDER);
    }
}
