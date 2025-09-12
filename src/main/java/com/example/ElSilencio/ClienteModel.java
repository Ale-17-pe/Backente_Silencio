    package com.example.ElSilencio;

    import com.example.ElSilencio.Model.ReservaModel;
    import com.example.ElSilencio.Model.UsuarioModel;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotBlank;
    import lombok.*;

    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Data
    @Getter @Setter
    @Table(name = "clientes")
    @NoArgsConstructor
    @AllArgsConstructor
    public class ClienteModel {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "El nombre es obligatorio")
        @Column(nullable = false)
        private String nombre;

        @NotBlank(message = "El apellido es obligatorio")
        @Column(nullable = false)
        private String apellido;

        @NotBlank(message = "El DNI es obligatorio")
        @Column(nullable = false, unique = true)
        private String dni;

        @NotBlank(message = "El email es obligatorio")
        @Column(nullable = false, unique = true)
        private String email;

        @Column(name = "telefono")
        private String telefono;

        @NotBlank(message = "El username es obligatorio")
        @Column(nullable = false, unique = true)
        private String username;

        @OneToOne
        @JoinColumn(name = "usuario_id", referencedColumnName = "id")
        private UsuarioModel usuario;

        @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ReservaModel> reservas = new ArrayList<>();
    /*
        public ClienteModel() {
        }

        public ClienteModel(String nombre, String apellido, String dni, String email,
                            String telefono, String username, String password) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.dni = dni;
            this.email = email;
            this.telefono = telefono;
            this.username = username;
            this.password = password;
        }

        // Getters y setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public String getDni() {
            return dni;
        }

        public void setDni(String dni) {
            this.dni = dni;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

     */
    }
