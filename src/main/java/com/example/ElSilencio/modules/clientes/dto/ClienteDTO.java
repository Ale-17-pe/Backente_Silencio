package com.example.ElSilencio.modules.clientes.dto;

public record ClienteDTO(
        Long id,
        String nombre,
        String apellido,
        String nombreCompleto,
        String dni,
        String email,
        String telefono,
        String username,
        String rol) {
}
