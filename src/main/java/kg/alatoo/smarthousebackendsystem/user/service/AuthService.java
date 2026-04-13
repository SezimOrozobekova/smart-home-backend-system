package kg.alatoo.smarthousebackendsystem.user.service;


import kg.alatoo.smarthousebackendsystem.security.jwt.JwtService;
import kg.alatoo.smarthousebackendsystem.user.entity.Role;
import kg.alatoo.smarthousebackendsystem.user.entity.User;
import kg.alatoo.smarthousebackendsystem.user.entity.UserStatus;
import kg.alatoo.smarthousebackendsystem.user.payload.request.LoginRequest;
import kg.alatoo.smarthousebackendsystem.user.payload.request.RegisterRequest;
import kg.alatoo.smarthousebackendsystem.user.payload.response.AuthResponse;
import kg.alatoo.smarthousebackendsystem.user.repository.RoleRepository;
import kg.alatoo.smarthousebackendsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role ROLE_USER not found"));

        User user = new User();
        user.setEmail(email);
        user.setName(request.getName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);
        user.setStatus(UserStatus.CREATED);
        user.setRole(role);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new DisabledException("User is inactive");
        }

        String token = jwtService.generateToken(authentication);
        return new AuthResponse(token);
    }
}