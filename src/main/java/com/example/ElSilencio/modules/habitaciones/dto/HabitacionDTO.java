package com.example.ElSilencio.modules.habitaciones.dto;

import java.math.BigDecimal;

public record HabitacionDTO(
                Long id,
                String numero,
                String tipo,
                BigDecimal precio,
                String estado,
                String imagenUrl,
                String descripcion) {
}
