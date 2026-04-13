package kg.alatoo.smarthousebackendsystem.user.controller;

import jakarta.validation.Valid;
import kg.alatoo.smarthousebackendsystem.user.payload.request.LoginRequest;
import kg.alatoo.smarthousebackendsystem.user.payload.request.RegisterRequest;
import kg.alatoo.smarthousebackendsystem.user.payload.response.AuthResponse;
import kg.alatoo.smarthousebackendsystem.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}