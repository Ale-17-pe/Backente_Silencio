package com.example.ElSilencio.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habitaciones")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HabitacionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true, length = 10)
    private String numero;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private String estado;

    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ReservaModel> reservas = new ArrayList<>();
/*
    public HabitacionModel(Long id, String tipo, String numero, BigDecimal precio, String estado) {
        this.id = id;
        this.tipo = tipo;
        this.numero = numero;
        this.precio = precio;
        this.estado = estado;
    }

    public HabitacionModel() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

 */
}
