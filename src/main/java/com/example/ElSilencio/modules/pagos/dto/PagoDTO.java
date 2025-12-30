package com.example.ElSilencio.modules.pagos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoDTO(
        Long id,
        BigDecimal monto,
        String metodo,
        LocalDateTime fechaPago) {
}
