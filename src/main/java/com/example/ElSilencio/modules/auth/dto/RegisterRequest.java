package com.example.ElSilencio.modules.auth.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,

        @NotBlank(message = "El apellido es obligatorio") String apellido,

        @NotBlank(message = "El DNI es obligatorio") @Size(min = 8, max = 12, message = "El DNI debe tener entre 8 y 12 caracteres") String dni,

        @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email,

        String telefono,

        @NotBlank(message = "El username es obligatorio") @Size(min = 4, max = 20, message = "El username debe tener entre 4 y 20 caracteres") String username,

        @NotBlank(message = "La contraseña es obligatoria") @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password) {
}
