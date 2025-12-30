package com.example.ElSilencio.modules.auth.controller;

import com.example.ElSilencio.modules.auth.dto.AuthResponse;
import com.example.ElSilencio.modules.auth.dto.LoginRequest;
import com.example.ElSilencio.modules.auth.dto.RegisterRequest;
import com.example.ElSilencio.modules.auth.model.RolEnum;
import com.example.ElSilencio.modules.auth.model.UsuarioModel;
import com.example.ElSilencio.modules.auth.repository.UsuarioRepository;
import com.example.ElSilencio.modules.auth.service.TwoFactorService;
import com.example.ElSilencio.modules.clientes.dto.ClienteDTO;
import com.example.ElSilencio.modules.clientes.model.ClienteModel;
import com.example.ElSilencio.modules.clientes.service.ClienteService;
import com.example.ElSilencio.shared.exception.BadRequestException;
import com.example.ElSilencio.shared.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "API de autenticacion y 2FA")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final ClienteService clienteService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TwoFactorService twoFactorService;

    @PostMapping("/login")
    @Operation(summary = "Login - Si requiere 2FA, devuelve pendiente2FA=true")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UsuarioModel usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        if (twoFactorService.requiere2FA(usuario)) {
            twoFactorService.generarYEnviarCodigo(usuario);
            return ResponseEntity.ok(new Login2FAResponse(
                    true, request.username(),
                    "Codigo de verificacion enviado a " + ocultarEmail(usuario.getEmail())));
        }

        return completarLogin(request.username());
    }

    @PostMapping("/verify-2fa")
    @Operation(summary = "Verificar codigo 2FA y completar login")
    public ResponseEntity<AuthResponse> verify2FA(@Valid @RequestBody Verify2FARequest request) {
        UsuarioModel usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        if (!twoFactorService.verificarCodigo(usuario, request.codigo())) {
            throw new BadRequestException("Codigo invalido o expirado");
        }

        return completarLogin(request.username());
    }

    @PostMapping("/resend-2fa")
    @Operation(summary = "Reenviar codigo 2FA")
    public ResponseEntity<String> resend2FA(@RequestParam String username) {
        UsuarioModel usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        twoFactorService.generarYEnviarCodigo(usuario);
        return ResponseEntity.ok("Codigo reenviado a " + ocultarEmail(usuario.getEmail()));
    }

    private ResponseEntity<AuthResponse> completarLogin(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        ClienteDTO clienteDTO = getClienteDTOByUsername(username);

        return ResponseEntity.ok(new AuthResponse(token, refreshToken, clienteDTO, jwtService.getExpirationTime()));
    }

    private String ocultarEmail(String email) {
        if (email == null || !email.contains("@"))
            return "***";
        String[] parts = email.split("@");
        String name = parts[0];
        if (name.length() <= 2)
            return "**@" + parts[1];
        return name.substring(0, 2) + "***@" + parts[1];
    }

    @PostMapping("/register")
    @Operation(summary = "Registro de cliente")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (clienteService.existsByDni(request.dni())) {
            throw new BadRequestException("Ya existe un cliente con ese DNI");
        }
        if (clienteService.existsByEmail(request.email())) {
            throw new BadRequestException("Ya existe un cliente con ese email");
        }
        if (clienteService.existsByUsername(request.username())) {
            throw new BadRequestException("Ya existe un cliente con ese username");
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setUsername(request.username());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setEmail(request.email());
        usuario.setRol(RolEnum.CLIENTE);

        ClienteModel cliente = new ClienteModel();
        cliente.setNombre(request.nombre());
        cliente.setApellido(request.apellido());
        cliente.setDni(request.dni());
        cliente.setEmail(request.email());
        cliente.setTelefono(request.telefono());
        cliente.setUsername(request.username());
        cliente.setUsuario(usuario);

        usuarioRepository.save(usuario);
        clienteService.save(cliente);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return ResponseEntity
                .ok(new AuthResponse(token, refreshToken, mapToDTO(cliente), jwtService.getExpirationTime()));
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener usuario actual")
    public ResponseEntity<ClienteDTO> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(getClienteDTOByUsername(auth.getName()));
    }

    private ClienteDTO getClienteDTOByUsername(String username) {
        return clienteService.findByUsername(username)
                .map(this::mapToDTO)
                .orElseGet(() -> {
                    UsuarioModel usuario = usuarioRepository.findByUsername(username).orElse(null);
                    if (usuario != null) {
                        return new ClienteDTO(usuario.getId(), usuario.getUsername(), "",
                                usuario.getUsername(), "", usuario.getEmail(), "",
                                usuario.getUsername(), usuario.getRol().name());
                    }
                    return null;
                });
    }

    private ClienteDTO mapToDTO(ClienteModel cliente) {
        return new ClienteDTO(cliente.getId(), cliente.getNombre(), cliente.getApellido(),
                cliente.getNombre() + " " + cliente.getApellido(),
                cliente.getDni(), cliente.getEmail(), cliente.getTelefono(),
                cliente.getUsername(),
                cliente.getUsuario() != null ? cliente.getUsuario().getRol().name() : "CLIENTE");
    }

    // ==================== PASSWORD RECOVERY ====================

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperacion de contraseña - envia codigo al email")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        UsuarioModel usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("No existe una cuenta con ese email"));

        // Usamos el servicio 2FA para generar y enviar el código
        twoFactorService.generarYEnviarCodigo(usuario);

        return ResponseEntity.ok(new MessageResponse(
                "Codigo de recuperacion enviado a " + ocultarEmail(usuario.getEmail())));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña con codigo recibido por email")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        UsuarioModel usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("No existe una cuenta con ese email"));

        // Verificar el código
        if (!twoFactorService.verificarCodigo(usuario, request.codigo())) {
            throw new BadRequestException("Codigo invalido o expirado");
        }

        // Actualizar la contraseña
        usuario.setPassword(passwordEncoder.encode(request.newPassword()));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new MessageResponse("Contraseña actualizada exitosamente"));
    }

    // ==================== DTOs ====================

    public record Login2FAResponse(boolean pendiente2FA, String username, String mensaje) {
    }

    public record Verify2FARequest(@NotBlank String username, @NotBlank String codigo) {
    }

    public record ForgotPasswordRequest(@NotBlank @jakarta.validation.constraints.Email String email) {
    }

    public record ResetPasswordRequest(
            @NotBlank @jakarta.validation.constraints.Email String email,
            @NotBlank String codigo,
            @NotBlank @jakarta.validation.constraints.Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String newPassword) {
    }

    public record MessageResponse(String message) {
    }
}
