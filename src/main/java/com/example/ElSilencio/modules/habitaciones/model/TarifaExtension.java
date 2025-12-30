package com.example.ElSilencio.modules.habitaciones.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tarifas_extension")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaExtension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_habitacion", unique = true)
    private String tipoHabitacion;

    @Column(name = "precio_por_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPorHora;

    @Column(name = "es_tarifa_default")
    private boolean esTarifaDefault = false;
}
