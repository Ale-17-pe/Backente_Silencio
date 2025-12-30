package com.example.ElSilencio.modules.habitaciones.controller;

import com.example.ElSilencio.modules.habitaciones.dto.HabitacionDTO;
import com.example.ElSilencio.modules.habitaciones.model.HabitacionModel;
import com.example.ElSilencio.modules.habitaciones.service.HabitacionService;
import com.example.ElSilencio.shared.exception.BadRequestException;
import com.example.ElSilencio.shared.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
@Tag(name = "Habitaciones", description = "API para gestion de habitaciones")
public class HabitacionController {

        private final HabitacionService habitacionService;

        @GetMapping
        @Operation(summary = "Listar todas las habitaciones")
        public ResponseEntity<List<HabitacionDTO>> listar() {
                List<HabitacionDTO> habitaciones = habitacionService.findAll().stream()
                                .map(this::mapToDTO).toList();
                return ResponseEntity.ok(habitaciones);
        }

        @GetMapping("/disponibles")
        @Operation(summary = "Listar habitaciones disponibles")
        public ResponseEntity<List<HabitacionDTO>> listarDisponibles() {
                List<HabitacionDTO> habitaciones = habitacionService.findByEstado("DISPONIBLE").stream()
                                .map(this::mapToDTO).toList();
                return ResponseEntity.ok(habitaciones);
        }

        @GetMapping("/tipos-disponibles")
        @Operation(summary = "Listar tipos de habitacion con disponibilidad (para vista publica)")
        public ResponseEntity<List<TipoHabitacionDTO>> getTiposDisponibles() {
                var todas = habitacionService.findAll();

                // Si no hay habitaciones, devolver lista vacia
                if (todas == null || todas.isEmpty()) {
                        return ResponseEntity.ok(java.util.Collections.emptyList());
                }

                // Agrupar por tipo
                var tiposMap = todas.stream()
                                .collect(java.util.stream.Collectors.groupingBy(HabitacionModel::getTipo));

                List<TipoHabitacionDTO> tipos = tiposMap.entrySet().stream()
                                .filter(entry -> !entry.getValue().isEmpty())
                                .map(entry -> {
                                        var habitaciones = entry.getValue();
                                        long disponibles = habitaciones.stream()
                                                        .filter(h -> "DISPONIBLE".equalsIgnoreCase(h.getEstado()))
                                                        .count();

                                        // Tomar precio e imagen de la primera habitacion del tipo
                                        var primera = habitaciones.get(0);

                                        return new TipoHabitacionDTO(
                                                        entry.getKey(),
                                                        primera.getPrecio(),
                                                        primera.getDescripcion(),
                                                        primera.getImagenUrl(),
                                                        (int) disponibles,
                                                        habitaciones.size(),
                                                        disponibles > 0);
                                })
                                .toList();

                return ResponseEntity.ok(tipos);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Obtener habitacion por ID")
        public ResponseEntity<HabitacionDTO> obtener(@PathVariable Long id) {
                HabitacionModel habitacion = habitacionService.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Habitacion", id));
                return ResponseEntity.ok(mapToDTO(habitacion));
        }

        @PostMapping
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
        @Operation(summary = "Crear nueva habitacion")
        public ResponseEntity<HabitacionDTO> crear(@Valid @RequestBody HabitacionRequest request) {
                if (habitacionService.findByNumero(request.numero()).isPresent()) {
                        throw new BadRequestException("Ya existe una habitacion con el numero " + request.numero());
                }

                HabitacionModel habitacion = new HabitacionModel();
                habitacion.setNumero(request.numero());
                habitacion.setTipo(request.tipo());
                habitacion.setPrecio(request.precio());
                habitacion.setEstado(request.estado() != null ? request.estado() : "DISPONIBLE");

                return ResponseEntity.ok(mapToDTO(habitacionService.save(habitacion)));
        }

        @PostMapping("/crear-lote")
        @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
        @Operation(summary = "Crear habitaciones en lote")
        public ResponseEntity<CrearLoteResponse> crearLote(@Valid @RequestBody CrearLoteRequest request) {
                if (request.numeroDesde() > request.numeroHasta()) {
                        throw new BadRequestException("El numero inicial debe ser menor o igual al final");
                }

                int creadas = 0;
                int omitidas = 0;

                for (int num = request.numeroDesde(); num <= request.numeroHasta(); num++) {
                        String numero = String.valueOf(num);
                        if (habitacionService.findByNumero(numero).isEmpty()) {
                                HabitacionModel habitacion = new HabitacionModel();
                                habitacion.setNumero(numero);
                                habitacion.setTipo(request.tipo());
                                habitacion.setPrecio(request.precio());
                                habitacion.setEstado("DISPONIBLE");
                                habitacionService.save(habitacion);
                                creadas++;
                        } else {
                                omitidas++;
                        }
                }

                return ResponseEntity.ok(new CrearLoteResponse(creadas, omitidas,
                                "Lote creado: " + creadas + " habitaciones, " + omitidas + " omitidas por duplicado"));
        }

        public record CrearLoteRequest(
                        @NotNull Integer numeroDesde,
                        @NotNull Integer numeroHasta,
                        @NotBlank String tipo,
                        @NotNull java.math.BigDecimal precio) {
        }

        public record CrearLoteResponse(int creadas, int omitidas, String message) {
        }

        @PatchMapping("/{id}/estado")
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
        @Operation(summary = "Cambiar estado de habitacion (DISPONIBLE, LIMPIEZA, MANTENIMIENTO)")
        public ResponseEntity<HabitacionDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
                HabitacionModel habitacion = habitacionService.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Habitacion", id));

                String estadoUpper = estado.toUpperCase();
                if (!List.of("DISPONIBLE", "LIMPIEZA", "MANTENIMIENTO", "OCUPADA", "RESERVADA").contains(estadoUpper)) {
                        throw new BadRequestException(
                                        "Estado invalido. Use: DISPONIBLE, LIMPIEZA, MANTENIMIENTO, RESERVADA");
                }

                habitacion.setEstado(estadoUpper);
                return ResponseEntity.ok(mapToDTO(habitacionService.save(habitacion)));
        }

        @PutMapping("/{id}/precios")
        @PreAuthorize("hasRole('ADMINISTRADOR')")
        @Operation(summary = "Actualizar precios de habitación")
        public ResponseEntity<?> actualizarPrecios(@PathVariable Long id,
                        @RequestBody ActualizarPreciosRequest request) {
                HabitacionModel habitacion = habitacionService.findById(id).orElse(null);
                if (habitacion == null) {
                        return ResponseEntity.notFound().build();
                }

                if (request.precio != null)
                        habitacion.setPrecio(request.precio);
                if (request.precio12Horas != null)
                        habitacion.setPrecio12Horas(request.precio12Horas);
                if (request.precioHora != null)
                        habitacion.setPrecioHora(request.precioHora);

                habitacionService.save(habitacion);
                return ResponseEntity.ok(mapToDTO(habitacion));
        }

        public record ActualizarPreciosRequest(BigDecimal precio, BigDecimal precio12Horas, BigDecimal precioHora) {
        }

        @PutMapping("/precios-tipo/{tipo}")
        @PreAuthorize("hasRole('ADMINISTRADOR')")
        @Operation(summary = "Actualizar precios de todas las habitaciones de un tipo")
        public ResponseEntity<?> actualizarPreciosPorTipo(@PathVariable String tipo,
                        @RequestBody ActualizarPreciosRequest request) {
                var habitaciones = habitacionService.findAll().stream()
                                .filter(h -> tipo.equalsIgnoreCase(h.getTipo()))
                                .toList();

                if (habitaciones.isEmpty()) {
                        return ResponseEntity.badRequest().body("No hay habitaciones del tipo: " + tipo);
                }

                for (var hab : habitaciones) {
                        if (request.precio() != null)
                                hab.setPrecio(request.precio());
                        if (request.precio12Horas() != null)
                                hab.setPrecio12Horas(request.precio12Horas());
                        if (request.precioHora() != null)
                                hab.setPrecioHora(request.precioHora());
                        habitacionService.save(hab);
                }

                return ResponseEntity.ok(java.util.Map.of(
                                "mensaje",
                                "Precios actualizados para " + habitaciones.size() + " habitaciones tipo " + tipo,
                                "actualizadas", habitaciones.size()));
        }

        @GetMapping("/dashboard")
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
        @Operation(summary = "Dashboard de habitaciones por estado")
        public ResponseEntity<DashboardHabitaciones> dashboard() {
                var todas = habitacionService.findAll();

                var disponibles = todas.stream().filter(h -> "DISPONIBLE".equalsIgnoreCase(h.getEstado()))
                                .sorted((a, b) -> a.getPrecio().compareTo(b.getPrecio()))
                                .map(this::mapToDTO).toList();
                var ocupadas = todas.stream().filter(h -> "OCUPADA".equalsIgnoreCase(h.getEstado()))
                                .map(this::mapToDTO).toList();
                var limpieza = todas.stream().filter(h -> "LIMPIEZA".equalsIgnoreCase(h.getEstado()) ||
                                "MANTENIMIENTO".equalsIgnoreCase(h.getEstado()))
                                .map(this::mapToDTO).toList();

                return ResponseEntity.ok(new DashboardHabitaciones(disponibles, ocupadas, limpieza,
                                disponibles.size(), ocupadas.size(), limpieza.size(), todas.size()));
        }

        private HabitacionDTO mapToDTO(HabitacionModel h) {
                return new HabitacionDTO(h.getId(), h.getNumero(), h.getTipo(), h.getPrecio(), h.getEstado(),
                                h.getImagenUrl(),
                                h.getDescripcion());
        }

        public record HabitacionRequest(
                        @NotBlank String numero,
                        @NotBlank String tipo,
                        @NotNull @Positive BigDecimal precio,
                        String estado) {
        }

        public record DashboardHabitaciones(
                        List<HabitacionDTO> disponibles,
                        List<HabitacionDTO> ocupadas,
                        List<HabitacionDTO> enLimpieza,
                        int totalDisponibles,
                        int totalOcupadas,
                        int totalLimpieza,
                        int totalHabitaciones) {
        }

        public record TipoHabitacionDTO(
                        String tipo,
                        BigDecimal precio,
                        String descripcion,
                        String imagenUrl,
                        int disponibles,
                        int total,
                        boolean hayDisponibles) {
        }
}
