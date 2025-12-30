package com.example.ElSilencio.modules.auth.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "two_factor_codes")
@Data
@NoArgsConstructor
public class TwoFactorCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @Column(nullable = false, length = 6)
    private String codigo;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(nullable = false)
    private boolean usado = false;

    public TwoFactorCode(UsuarioModel usuario, String codigo) {
        this.usuario = usuario;
        this.codigo = codigo;
        this.fechaExpiracion = LocalDateTime.now().plusMinutes(10);
        this.usado = false;
    }

    public boolean isValido() {
        return !usado && LocalDateTime.now().isBefore(fechaExpiracion);
    }
}
