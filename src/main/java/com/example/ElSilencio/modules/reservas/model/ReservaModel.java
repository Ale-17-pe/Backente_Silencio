package com.example.ElSilencio.modules.reservas.model;

import com.example.ElSilencio.modules.clientes.model.ClienteModel;
import com.example.ElSilencio.modules.habitaciones.model.HabitacionModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_reserva", unique = true, nullable = false, length = 12)
    private String codigoReserva;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteModel cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "habitacion_id", nullable = false)
    private HabitacionModel habitacion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "hora_checkin")
    private LocalDateTime horaCheckin;

    @Column(name = "hora_checkout")
    private LocalDateTime horaCheckout;

    @Column(name = "horas_extension")
    private Integer horasExtension = 0;

    @Column(name = "costo_extension", precision = 10, scale = 2)
    private BigDecimal costoExtension = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal total;

    @Column(length = 20, nullable = false)
    private String estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_reserva", length = 20)
    private TipoReservaEnum tipoReserva = TipoReservaEnum.ONLINE;

    @Column(name = "notas", length = 500)
    private String notas;

    @PrePersist
    public void generarCodigo() {
        if (this.codigoReserva == null) {
            this.codigoReserva = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public BigDecimal getTotalConExtension() {
        return total.add(costoExtension != null ? costoExtension : BigDecimal.ZERO);
    }
}
