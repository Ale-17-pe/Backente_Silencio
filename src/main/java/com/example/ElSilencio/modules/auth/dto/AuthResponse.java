package com.example.ElSilencio.modules.auth.dto;

import com.example.ElSilencio.modules.clientes.dto.ClienteDTO;

public record AuthResponse(
        String token,
        String refreshToken,
        ClienteDTO cliente,
        long expiresIn) {
}
