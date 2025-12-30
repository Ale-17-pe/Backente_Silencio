package com.example.ElSilencio.shared.config;

import com.example.ElSilencio.modules.auth.model.RolEnum;
import com.example.ElSilencio.modules.auth.model.UsuarioModel;
import com.example.ElSilencio.modules.auth.repository.UsuarioRepository;
import com.example.ElSilencio.modules.habitaciones.model.HabitacionModel;
import com.example.ElSilencio.modules.habitaciones.repository.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepo,
            HabitacionRepository habitacionRepo) {
        return args -> {
            // Crear usuario Admin si no existe
            if (usuarioRepo.findByUsername("alex").isEmpty()) {
                UsuarioModel admin = new UsuarioModel();
                admin.setUsername("alex");
                admin.setPassword(passwordEncoder.encode("alex123"));
                admin.setEmail("tayson2814celeste@gmail.com");
                admin.setRol(RolEnum.ADMINISTRADOR);
                usuarioRepo.save(admin);
                System.out.println(">>> Usuario ADMIN creado: alex / alex123");
            }

            // Crear Recepcionista si no existe
            if (usuarioRepo.findByUsername("fabri").isEmpty()) {
                UsuarioModel recep = new UsuarioModel();
                recep.setUsername("fabri");
                recep.setPassword(passwordEncoder.encode("fabri123"));
                recep.setEmail("f.alexandrogallardoq@gmail.com");
                recep.setRol(RolEnum.RECEPCIONISTA);
                usuarioRepo.save(recep);
                System.out.println(">>> Usuario RECEPCIONISTA creado: fabri / fabri123");
            }

            // Cliente de prueba
            if (usuarioRepo.findByUsername("cliente1").isEmpty()) {
                UsuarioModel cliente = new UsuarioModel();
                cliente.setUsername("cliente1");
                cliente.setPassword(passwordEncoder.encode("cliente123"));
                cliente.setEmail("cliente@test.com");
                cliente.setRol(RolEnum.CLIENTE);
                usuarioRepo.save(cliente);
                System.out.println(">>> Usuario CLIENTE creado: cliente1 / cliente123");
            }

            // Crear habitaciones de prueba si no existen
            if (habitacionRepo.findByNumero("101").isEmpty()) {
                // SIMPLE: 80/día, 50/12h, 10/hora extra
                habitacionRepo.save(crearHabitacion("101", "SIMPLE", new BigDecimal("80.00"), new BigDecimal("50.00"),
                        new BigDecimal("10.00")));
                habitacionRepo.save(crearHabitacion("102", "SIMPLE", new BigDecimal("80.00"), new BigDecimal("50.00"),
                        new BigDecimal("10.00")));
                // DOBLE: 120/día, 70/12h, 15/hora extra
                habitacionRepo.save(crearHabitacion("201", "DOBLE", new BigDecimal("120.00"), new BigDecimal("70.00"),
                        new BigDecimal("15.00")));
                habitacionRepo.save(crearHabitacion("202", "DOBLE", new BigDecimal("120.00"), new BigDecimal("70.00"),
                        new BigDecimal("15.00")));
                // SUITE: 200/día, 120/12h, 25/hora extra
                habitacionRepo.save(crearHabitacion("301", "SUITE", new BigDecimal("200.00"), new BigDecimal("120.00"),
                        new BigDecimal("25.00")));
                habitacionRepo.save(crearHabitacion("302", "SUITE", new BigDecimal("200.00"), new BigDecimal("120.00"),
                        new BigDecimal("25.00")));
                System.out.println(">>> 6 habitaciones de prueba creadas con precios");
            }

            System.out.println("=".repeat(50));
            System.out.println("DATOS INICIALES CARGADOS - LISTO PARA PRUEBAS");
            System.out.println("Admin: tayson2814celeste@gmail.com / admin123 (requiere 2FA)");
            System.out.println("Recepcion: f.alexandrogallardoq@gmail.com / recep123 (requiere 2FA)");
            System.out.println("Cliente: cliente1 / cliente123");
            System.out.println("=".repeat(50));
        };
    }

    private HabitacionModel crearHabitacion(String numero, String tipo, BigDecimal precioDia, BigDecimal precio12h,
            BigDecimal precioHora) {
        HabitacionModel h = new HabitacionModel();
        h.setNumero(numero);
        h.setTipo(tipo);
        h.setPrecio(precioDia);
        h.setPrecio12Horas(precio12h);
        h.setPrecioHora(precioHora);
        h.setEstado("DISPONIBLE");
        return h;
    }
}
