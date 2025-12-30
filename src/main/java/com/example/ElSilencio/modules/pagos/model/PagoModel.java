package com.example.ElSilencio.modules.pagos.model;

import com.example.ElSilencio.modules.reservas.model.ReservaModel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private ReservaModel reserva;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPagoEnum metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPagoEnum estadoPago = EstadoPagoEnum.PENDIENTE_VERIFICACION;

    @Column(name = "evidencia_url")
    private String evidenciaUrl;

    @Column(name = "verificado_por")
    private String verificadoPor;

    @Column(name = "fecha_verificacion")
    private LocalDateTime fechaVerificacion;

    @Column(name = "motivo_rechazo")
    private String motivoRechazo;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    public String getMetodo() {
        return metodoPago != null ? metodoPago.getDisplayName() : null;
    }
}
