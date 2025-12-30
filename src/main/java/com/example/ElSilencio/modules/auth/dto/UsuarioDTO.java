package com.example.ElSilencio.modules.auth.dto;

import com.example.ElSilencio.modules.auth.model.RolEnum;

public record UsuarioDTO(
        Long id,
        String username,
        String email,
        RolEnum rol,
        boolean activo) {
}
