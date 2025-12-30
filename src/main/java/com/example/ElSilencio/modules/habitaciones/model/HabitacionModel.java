package com.example.ElSilencio.modules.habitaciones.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "habitaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String numero;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio; // Precio por noche/día

    @Column(name = "precio_hora", precision = 10, scale = 2)
    private BigDecimal precioHora; // Precio por hora adicional (extensión)

    @Column(name = "precio_12_horas", precision = 10, scale = 2)
    private BigDecimal precio12Horas; // Precio modalidad 12 horas

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(length = 500)
    private String descripcion;
}
