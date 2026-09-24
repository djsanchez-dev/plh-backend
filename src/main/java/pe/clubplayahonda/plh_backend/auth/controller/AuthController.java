package pe.clubplayahonda.plh_backend.auth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.clubplayahonda.plh_backend.auth.dto.AuthResponse;
import pe.clubplayahonda.plh_backend.auth.dto.ChangePasswordRequest;
import pe.clubplayahonda.plh_backend.auth.dto.ProfileUpdateRequest;
import pe.clubplayahonda.plh_backend.auth.dto.LoginRequest;
import pe.clubplayahonda.plh_backend.auth.dto.RegisterRequest;
import pe.clubplayahonda.plh_backend.auth.dto.UserResponse;
import pe.clubplayahonda.plh_backend.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse currentUser(Authentication authentication) {
        return authService.currentUser(authentication.getName());
    }

    @PutMapping("/me")
    public UserResponse updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequest request) {
        return authService.updateCurrentUser(authentication.getName(), request);
    }

    @PutMapping("/me/password")
    public UserResponse changeOwnPassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        return authService.changeOwnPassword(authentication.getName(), request);
    }
}
