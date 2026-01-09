package com.example.ElSilencio.modules.clientes.model;

import com.example.ElSilencio.modules.auth.model.UsuarioModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Setter @Getter
public class ClienteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefono;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "foto_url")
    private String fotoUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id")
    private UsuarioModel usuario;

    public ClienteModel(Long id, String nombre, String apellido, String dni, String email, String telefono, String fotoUrl, String username, UsuarioModel usuario) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.fotoUrl = fotoUrl;
        this.username = username;
        this.usuario = usuario;
    }

    public ClienteModel() {
    }
}
