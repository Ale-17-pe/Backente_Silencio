package com.example.ElSilencio.modules.auth.controller;

import com.example.ElSilencio.modules.auth.dto.UsuarioDTO;
import com.example.ElSilencio.modules.auth.model.RolEnum;
import com.example.ElSilencio.modules.auth.model.UsuarioModel;
import com.example.ElSilencio.modules.auth.service.UsuarioService;
import com.example.ElSilencio.shared.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> usuarios = usuarioService.findAll().stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id) {
        UsuarioModel usuario = usuarioService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        return ResponseEntity.ok(mapToDTO(usuario));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario (empleado)")
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody CrearUsuarioRequest request) {
        UsuarioModel usuario = usuarioService.crear(
                request.username(),
                request.email(),
                request.password(),
                request.rol());
        return ResponseEntity.ok(mapToDTO(usuario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequest request) {
        UsuarioModel usuario = usuarioService.actualizar(
                id,
                request.username(),
                request.email(),
                request.rol());
        return ResponseEntity.ok(mapToDTO(usuario));
    }

    @PatchMapping("/{id}/rol")
    @Operation(summary = "Cambiar rol de usuario")
    public ResponseEntity<UsuarioDTO> cambiarRol(@PathVariable Long id, @RequestParam RolEnum rol) {
        UsuarioModel usuario = usuarioService.cambiarRol(id, rol);
        return ResponseEntity.ok(mapToDTO(usuario));
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "Resetear contraseña (genera una nueva aleatoria)")
    public ResponseEntity<ResetPasswordResponse> resetearPassword(@PathVariable Long id) {
        String nuevaPassword = usuarioService.resetearPassword(id);
        return ResponseEntity.ok(new ResetPasswordResponse(
                id,
                nuevaPassword,
                "Contraseña reseteada exitosamente. Comparte esta contraseña temporal con el usuario."));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioDTO mapToDTO(UsuarioModel u) {
        return new UsuarioDTO(u.getId(), u.getUsername(), u.getEmail(), u.getRol(), true);
    }

    // Request/Response records
    public record CrearUsuarioRequest(
            @NotBlank String username,
            @NotBlank @Email String email,
            @NotBlank String password,
            @NotNull RolEnum rol) {
    }

    public record ActualizarUsuarioRequest(
            @NotBlank String username,
            @NotBlank @Email String email,
            @NotNull RolEnum rol) {
    }

    public record ResetPasswordResponse(
            Long usuarioId,
            String nuevaPassword,
            String mensaje) {
    }
}
