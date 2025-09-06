package com.example.ElSilencio.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class PagoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reserva_id", nullable = false)
    private ReservaModel reserva;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private String metodo;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;
/*
    public PagoModel(Long id, ReservaModel reserva, BigDecimal monto, String metodo, LocalDateTime fechaPago) {
        this.id = id;
        this.reserva = reserva;
        this.monto = monto;
        this.metodo = metodo;
        this.fechaPago = fechaPago;
    }

    public PagoModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReservaModel getReserva() {
        return reserva;
    }

    public void setReserva(ReservaModel reserva) {
        this.reserva = reserva;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }
    
 */
}
