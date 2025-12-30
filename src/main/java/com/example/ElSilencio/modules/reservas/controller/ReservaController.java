package com.example.ElSilencio.modules.reservas.controller;

import com.example.ElSilencio.modules.clientes.model.ClienteModel;
import com.example.ElSilencio.modules.clientes.repository.ClienteRepository;
import com.example.ElSilencio.modules.habitaciones.model.HabitacionModel;
import com.example.ElSilencio.modules.habitaciones.service.HabitacionService;
import com.example.ElSilencio.modules.pagos.model.EstadoPagoEnum;
import com.example.ElSilencio.modules.pagos.model.MetodoPagoEnum;
import com.example.ElSilencio.modules.pagos.model.PagoModel;
import com.example.ElSilencio.modules.pagos.service.PagoService;
import com.example.ElSilencio.modules.reservas.dto.ReservaDTO;
import com.example.ElSilencio.modules.reservas.model.ReservaModel;
import com.example.ElSilencio.modules.reservas.model.TipoReservaEnum;
import com.example.ElSilencio.modules.reservas.service.ReservaService;
import com.example.ElSilencio.shared.exception.BadRequestException;
import com.example.ElSilencio.shared.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "API de reservas y walk-in")
public class ReservaController {

    private final ReservaService reservaService;
    private final HabitacionService habitacionService;
    private final PagoService pagoService;
    private final ClienteRepository clienteRepository;

    private static final String UPLOAD_DIR = "uploads/evidencias/";

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Listar todas las reservas")
    public ResponseEntity<List<ReservaDTO>> listar() {
        return ResponseEntity.ok(reservaService.findAll().stream().map(this::mapToDTO).toList());
    }

    // ENDPOINT PÚBLICO - Para pantalla de monitor sin autenticación
    @GetMapping("/monitor-publico")
    @Operation(summary = "Obtener habitaciones ocupadas para pantalla pública")
    public ResponseEntity<List<MonitorPublicoDTO>> monitorPublico() {
        // Solo retornar reservas EN_CURSO con datos mínimos
        var reservasActivas = reservaService.findAll().stream()
                .filter(r -> "EN_CURSO".equals(r.getEstado()))
                .map(r -> new MonitorPublicoDTO(
                        r.getId(),
                        r.getHabitacion().getNumero(),
                        r.getHabitacion().getTipo(),
                        r.getFechaInicio(),
                        r.getFechaFin(),
                        r.getHoraCheckin(),
                        r.getNotas()))
                .toList();
        return ResponseEntity.ok(reservasActivas);
    }

    public record MonitorPublicoDTO(
            Long id,
            String habitacionNumero,
            String habitacionTipo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            LocalDateTime horaCheckin,
            String notas) {
    }

    @GetMapping("/mis-reservas")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener reservas del cliente autenticado")
    public ResponseEntity<List<ReservaDTO>> misReservas() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Buscar cliente por username, si no existe retornar lista vacía
        var clienteOpt = clienteRepository.findByUsername(username);
        if (clienteOpt.isEmpty()) {
            // Puede ser admin/recepcionista sin registro de cliente
            return ResponseEntity.ok(List.of());
        }

        ClienteModel cliente = clienteOpt.get();
        return ResponseEntity.ok(reservaService.findByClienteId(cliente.getId())
                .stream().map(this::mapToDTO).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID")
    public ResponseEntity<ReservaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mapToDTO(reservaService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id))));
    }

    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Buscar reserva por codigo QR/numerico")
    public ResponseEntity<ReservaDTO> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(mapToDTO(reservaService.findByCodigoReserva(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva con codigo " + codigo + " no encontrada"))));
    }

    @GetMapping("/habitacion/{habitacionId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Obtener reserva activa por habitacion")
    public ResponseEntity<ReservaDTO> getReservaByHabitacion(@PathVariable Long habitacionId) {
        // Buscar reserva CONFIRMADA o PENDIENTE para esta habitación
        return ResponseEntity.ok(
                reservaService.findAll().stream()
                        .filter(r -> r.getHabitacion().getId().equals(habitacionId))
                        .filter(r -> List.of("CONFIRMADA", "PENDIENTE").contains(r.getEstado()))
                        .findFirst()
                        .map(this::mapToDTO)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No hay reserva activa para habitación " + habitacionId)));
    }

    @PostMapping("/reservar-por-tipo")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Crear reserva online por tipo de habitacion (auto-asigna habitacion)")
    public ResponseEntity<ReservaDTO> reservarPorTipo(
            @RequestBody ReservaPorTipoRequest request) {

        // Buscar primera habitacion disponible del tipo solicitado
        HabitacionModel habitacion = habitacionService.findFirstAvailableByTipo(request.tipoHabitacion())
                .orElseThrow(() -> new BadRequestException(
                        "No hay habitaciones disponibles del tipo " + request.tipoHabitacion()));

        // Obtener cliente autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        ClienteModel cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado"));

        // Validar fechas
        if (request.fechaInicio().isBefore(LocalDate.now())) {
            throw new BadRequestException("La fecha de inicio no puede ser anterior a hoy");
        }
        if (request.fechaFin().isBefore(request.fechaInicio()) || request.fechaFin().isEqual(request.fechaInicio())) {
            throw new BadRequestException("La fecha de fin debe ser posterior a la de inicio");
        }

        // Verificar conflictos
        if (reservaService.existsConflictingReservation(habitacion.getId(), request.fechaInicio(),
                request.fechaFin())) {
            // Buscar otra habitacion del mismo tipo
            habitacion = habitacionService.findAll().stream()
                    .filter(h -> request.tipoHabitacion().equalsIgnoreCase(h.getTipo()))
                    .filter(h -> "DISPONIBLE".equalsIgnoreCase(h.getEstado()))
                    .filter(h -> !reservaService.existsConflictingReservation(h.getId(), request.fechaInicio(),
                            request.fechaFin()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(
                            "No hay habitaciones disponibles del tipo " + request.tipoHabitacion()
                                    + " para esas fechas"));
        }

        // Calcular total
        long noches = java.time.temporal.ChronoUnit.DAYS.between(request.fechaInicio(), request.fechaFin());
        BigDecimal total = habitacion.getPrecio().multiply(new BigDecimal(noches));

        // Crear reserva
        ReservaModel reserva = new ReservaModel();
        reserva.setCliente(cliente);
        reserva.setHabitacion(habitacion);
        reserva.setFechaInicio(request.fechaInicio());
        reserva.setFechaFin(request.fechaFin());
        reserva.setTotal(total);
        reserva.setEstado("PENDIENTE"); // Pendiente de pago
        reserva.setTipoReserva(TipoReservaEnum.ONLINE);
        reserva.setNotas(request.notas());

        ReservaModel saved = reservaService.save(reserva);

        return ResponseEntity.ok(mapToDTO(saved));
    }

    public record ReservaPorTipoRequest(
            @NotBlank String tipoHabitacion,
            @NotNull LocalDate fechaInicio,
            @NotNull LocalDate fechaFin,
            String notas) {
    }

    @PostMapping(value = "/reservar-cliente", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Crear reserva cliente con modalidad horas/días y pago adelanto")
    public ResponseEntity<ReservaDTO> reservarCliente(
            @RequestParam @NotBlank String tipoHabitacion,
            @RequestParam @NotBlank String modalidad, // HORAS o DIAS
            @RequestParam @NotBlank String fechaEntrada,
            @RequestParam @NotBlank String horaEntrada,
            @RequestParam(defaultValue = "1") Integer duracionDias,
            @RequestParam @NotBlank String metodoPago,
            @RequestParam @NotNull BigDecimal montoAdelanto,
            @RequestParam(required = false) MultipartFile evidencia) {

        // Obtener cliente autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        ClienteModel cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado"));

        // Buscar habitación disponible del tipo
        HabitacionModel habitacion = habitacionService.findFirstAvailableByTipo(tipoHabitacion)
                .orElseThrow(
                        () -> new BadRequestException("No hay habitaciones disponibles del tipo " + tipoHabitacion));

        // Parsear fecha y hora
        LocalDate fecha = LocalDate.parse(fechaEntrada);
        LocalDateTime horaEntradaParsed = fecha.atTime(
                Integer.parseInt(horaEntrada.split(":")[0]),
                Integer.parseInt(horaEntrada.split(":")[1]));

        // Calcular precio
        BigDecimal precioBase = habitacion.getPrecio();
        BigDecimal total;
        LocalDate fechaFin;

        if ("HORAS".equalsIgnoreCase(modalidad)) {
            total = precioBase; // 12 horas = precio base
            fechaFin = fecha.plusDays(1); // Simplificado
        } else {
            total = precioBase.multiply(new BigDecimal(duracionDias));
            fechaFin = fecha.plusDays(duracionDias);
        }

        // Validar adelanto (mínimo 50%)
        BigDecimal adelantoMinimo = total.multiply(new BigDecimal("0.5"));
        if (montoAdelanto.compareTo(adelantoMinimo) < 0) {
            throw new BadRequestException("El adelanto mínimo es S/ " + adelantoMinimo);
        }

        // Guardar evidencia si aplica
        String evidenciaUrl = null;
        if (evidencia != null && !evidencia.isEmpty()) {
            evidenciaUrl = guardarEvidencia(evidencia);
        }

        // Validar: si no es efectivo, requiere evidencia
        if (!"EFECTIVO".equalsIgnoreCase(metodoPago) && evidenciaUrl == null) {
            throw new BadRequestException("Para pagos digitales es obligatorio subir el comprobante");
        }

        // Crear reserva
        ReservaModel reserva = new ReservaModel();
        reserva.setCliente(cliente);
        reserva.setHabitacion(habitacion);
        reserva.setFechaInicio(fecha);
        reserva.setFechaFin(fechaFin);
        reserva.setTotal(total);
        reserva.setNotas("Modalidad: " + modalidad + " | Hora entrada: " + horaEntrada);
        reserva.setTipoReserva(TipoReservaEnum.ONLINE);

        // Estado según pago
        boolean esEfectivo = "EFECTIVO".equalsIgnoreCase(metodoPago);
        reserva.setEstado(esEfectivo ? "PENDIENTE" : "CONFIRMADA");

        // Si el pago es digital (50% adelanto), marcar habitación como RESERVADA
        if (!esEfectivo) {
            habitacion.setEstado("RESERVADA");
            habitacionService.save(habitacion);
        }

        ReservaModel savedReserva = reservaService.save(reserva);

        // Crear pago de adelanto
        MetodoPagoEnum metodo = switch (metodoPago.toUpperCase()) {
            case "YAPE" -> MetodoPagoEnum.YAPE;
            case "PLIN" -> MetodoPagoEnum.PLIN;
            case "TRANSFERENCIA" -> MetodoPagoEnum.TRANSFERENCIA;
            default -> MetodoPagoEnum.EFECTIVO;
        };

        PagoModel pago = new PagoModel();
        pago.setReserva(savedReserva);
        pago.setMonto(montoAdelanto);
        pago.setMetodoPago(metodo);
        pago.setFechaPago(LocalDateTime.now());
        pago.setEvidenciaUrl(evidenciaUrl);
        pago.setEstadoPago(esEfectivo ? EstadoPagoEnum.PENDIENTE_VERIFICACION : EstadoPagoEnum.PENDIENTE_VERIFICACION);

        pagoService.save(pago);

        return ResponseEntity.ok(mapToDTO(savedReserva));
    }

    @PostMapping("/{id}/checkin")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Realizar check-in")
    public ResponseEntity<ReservaDTO> checkin(@PathVariable Long id) {
        ReservaModel reserva = reservaService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));

        if (!"CONFIRMADA".equalsIgnoreCase(reserva.getEstado())) {
            throw new BadRequestException("Solo se puede hacer check-in a reservas confirmadas");
        }

        reserva.setEstado("EN_CURSO");
        reserva.setHoraCheckin(LocalDateTime.now());
        reserva.getHabitacion().setEstado("OCUPADA");
        habitacionService.save(reserva.getHabitacion());

        return ResponseEntity.ok(mapToDTO(reservaService.save(reserva)));
    }

    @PostMapping("/{id}/checkout")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Realizar check-out")
    public ResponseEntity<ReservaDTO> checkout(@PathVariable Long id) {
        ReservaModel reserva = reservaService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));

        if (!"EN_CURSO".equalsIgnoreCase(reserva.getEstado())) {
            throw new BadRequestException("Solo se puede hacer check-out a reservas en curso");
        }

        reserva.setEstado("COMPLETADA");
        reserva.setHoraCheckout(LocalDateTime.now());
        reserva.getHabitacion().setEstado("LIMPIEZA");
        habitacionService.save(reserva.getHabitacion());

        return ResponseEntity.ok(mapToDTO(reservaService.save(reserva)));
    }

    @PostMapping(value = "/walkin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Crear reserva walk-in (cliente sin reserva)")
    public ResponseEntity<WalkInResponse> crearWalkIn(
            @RequestParam @NotBlank String nombre,
            @RequestParam @NotBlank String apellido,
            @RequestParam @NotBlank @Size(min = 8, max = 12) String dni,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String email,
            @RequestParam @NotNull Long habitacionId,
            @RequestParam @NotNull @Min(1) Integer noches,
            @RequestParam @NotNull MetodoPagoEnum metodoPago,
            @RequestParam @NotNull @Positive BigDecimal montoPagado,
            @RequestParam(required = false) MultipartFile evidencia,
            @RequestParam(required = false) String notas,
            @RequestParam(required = false, defaultValue = "HORAS") String modalidad) {
        if (metodoPago != MetodoPagoEnum.EFECTIVO && (evidencia == null || evidencia.isEmpty())) {
            throw new BadRequestException("Para pagos con " + metodoPago.getDisplayName() +
                    " es obligatorio subir foto del comprobante");
        }

        HabitacionModel habitacion = habitacionService.findById(habitacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion", habitacionId));

        if (!"DISPONIBLE".equalsIgnoreCase(habitacion.getEstado())) {
            throw new BadRequestException("La habitacion no esta disponible");
        }

        ClienteModel cliente = clienteRepository.findByDni(dni).orElseGet(() -> {
            ClienteModel nuevo = new ClienteModel();
            nuevo.setNombre(nombre);
            nuevo.setApellido(apellido);
            nuevo.setDni(dni);
            nuevo.setTelefono(telefono);
            nuevo.setEmail(email != null ? email : dni + "@walkin.temp");
            nuevo.setUsername("WALKIN-" + dni);
            return clienteRepository.save(nuevo);
        });

        LocalDate hoy = LocalDate.now();
        ReservaModel reserva = new ReservaModel();
        reserva.setCliente(cliente);
        reserva.setHabitacion(habitacion);
        reserva.setFechaInicio(hoy);
        reserva.setFechaFin(hoy.plusDays(noches));

        // Precio según modalidad: HORAS usa precio12Horas, DIAS usa precio normal
        BigDecimal precio = "HORAS".equalsIgnoreCase(modalidad) && habitacion.getPrecio12Horas() != null
                ? habitacion.getPrecio12Horas()
                : habitacion.getPrecio().multiply(new BigDecimal(noches));
        reserva.setTotal(precio);

        reserva.setTipoReserva(TipoReservaEnum.WALKIN);
        // Guardar modalidad en notas para que el monitor pueda detectar 12h vs días
        String notasCompletas = "Modalidad: " + modalidad.toUpperCase()
                + (notas != null && !notas.isEmpty() ? " | " + notas : "");
        reserva.setNotas(notasCompletas);

        String evidenciaUrl = null;
        if (evidencia != null && !evidencia.isEmpty()) {
            evidenciaUrl = guardarEvidencia(evidencia);
        }

        boolean pagoCompleto = montoPagado.compareTo(reserva.getTotal()) >= 0;
        boolean tieneEvidencia = evidenciaUrl != null;

        if (pagoCompleto && (metodoPago == MetodoPagoEnum.EFECTIVO || tieneEvidencia)) {
            reserva.setEstado("EN_CURSO");
            reserva.setHoraCheckin(LocalDateTime.now());
            habitacion.setEstado("OCUPADA");
            habitacionService.save(habitacion);
        } else {
            reserva.setEstado("CONFIRMADA");
        }

        ReservaModel savedReserva = reservaService.save(reserva);

        PagoModel pago = new PagoModel();
        pago.setReserva(savedReserva);
        pago.setMonto(montoPagado);
        pago.setMetodoPago(metodoPago);
        pago.setFechaPago(LocalDateTime.now());
        pago.setEvidenciaUrl(evidenciaUrl);

        if (metodoPago == MetodoPagoEnum.EFECTIVO || tieneEvidencia) {
            pago.setEstadoPago(EstadoPagoEnum.VERIFICADO);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            pago.setVerificadoPor(auth.getName());
            pago.setFechaVerificacion(LocalDateTime.now());
        } else {
            pago.setEstadoPago(EstadoPagoEnum.PENDIENTE_VERIFICACION);
        }

        pagoService.save(pago);

        String mensaje = "EN_CURSO".equals(savedReserva.getEstado())
                ? "Walk-In exitoso. Check-in completado."
                : "Walk-In creado. Pendiente verificacion.";

        return ResponseEntity.ok(new WalkInResponse(
                savedReserva.getId(), savedReserva.getCodigoReserva(),
                cliente.getNombre() + " " + cliente.getApellido(), cliente.getDni(),
                habitacion.getNumero(), habitacion.getTipo(),
                savedReserva.getFechaInicio(), savedReserva.getFechaFin(), noches,
                savedReserva.getTotal(), montoPagado,
                savedReserva.getTotal().subtract(montoPagado),
                savedReserva.getEstado(), metodoPago.getDisplayName(),
                evidenciaUrl != null, mensaje));
    }

    private String guardarEvidencia(MultipartFile archivo) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath))
                Files.createDirectories(uploadPath);
            String extension = archivo.getOriginalFilename() != null && archivo.getOriginalFilename().contains(".")
                    ? archivo.getOriginalFilename().substring(archivo.getOriginalFilename().lastIndexOf('.') + 1)
                    : "jpg";
            String fileName = "walkin_" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;
            Files.copy(archivo.getInputStream(), uploadPath.resolve(fileName));
            return UPLOAD_DIR + fileName;
        } catch (IOException e) {
            throw new BadRequestException("Error al guardar evidencia: " + e.getMessage());
        }
    }

    private ReservaDTO mapToDTO(ReservaModel r) {
        // Cliente info
        var cliente = r.getCliente();
        var clienteInfo = new ReservaDTO.ClienteInfo(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getDni(),
                cliente.getEmail(),
                cliente.getTelefono());

        // Habitacion info
        var hab = r.getHabitacion();
        var habitacionInfo = new ReservaDTO.HabitacionInfo(
                hab.getId(),
                hab.getNumero(),
                hab.getTipo(),
                hab.getPrecio(),
                hab.getImagenUrl());

        // Pagos info
        List<ReservaDTO.PagoInfo> pagosInfo = pagoService.findByReservaId(r.getId()).stream()
                .map(p -> new ReservaDTO.PagoInfo(
                        p.getId(),
                        p.getMonto(),
                        p.getMetodoPago() != null ? p.getMetodoPago().getDisplayName() : null,
                        p.getEstadoPago() != null ? p.getEstadoPago().name() : null,
                        p.getEvidenciaUrl(),
                        p.getFechaPago(),
                        p.getVerificadoPor(),
                        p.getFechaVerificacion(),
                        p.getMotivoRechazo()))
                .toList();

        return new ReservaDTO(
                r.getId(),
                r.getCodigoReserva(),
                clienteInfo,
                habitacionInfo,
                r.getFechaInicio(),
                r.getFechaFin(),
                r.getHoraCheckin(),
                r.getHoraCheckout(),
                r.getTotal(),
                r.getCostoExtension(),
                r.getHorasExtension(),
                r.getEstado(),
                r.getTipoReserva() != null ? r.getTipoReserva().name() : "ONLINE",
                r.getNotas(),
                pagosInfo);
    }

    public record WalkInResponse(
            Long reservaId, String codigoReserva, String nombreCliente, String dniCliente,
            String numeroHabitacion, String tipoHabitacion, LocalDate fechaInicio, LocalDate fechaFin,
            int noches, BigDecimal total, BigDecimal montoPagado, BigDecimal saldoPendiente,
            String estado, String metodoPago, boolean evidenciaSubida, String mensaje) {
    }

    // ===== EXTENSIÓN DE ESTADÍA =====
    @PostMapping("/{id}/extender")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
    @Operation(summary = "Extender estadía de una reserva activa")
    public ResponseEntity<?> extenderEstadia(
            @PathVariable Long id,
            @RequestBody ExtensionRequest request) {

        var reservaOpt = reservaService.findById(id);
        if (reservaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Reserva no encontrada");
        }

        ReservaModel reserva = reservaOpt.get();
        if (!"EN_CURSO".equals(reserva.getEstado())) {
            return ResponseEntity.badRequest().body("Solo se pueden extender reservas EN_CURSO");
        }

        var habitacion = reserva.getHabitacion();
        BigDecimal costo;
        int horasAdicionales;

        if ("HORAS".equalsIgnoreCase(request.tipo())) {
            // Extensión por horas
            BigDecimal precioHora = habitacion.getPrecioHora() != null
                    ? habitacion.getPrecioHora()
                    : new BigDecimal("10.00");
            costo = precioHora.multiply(new BigDecimal(request.cantidad()));
            horasAdicionales = request.cantidad();
        } else {
            // Extensión por días (convertir a horas para el tracking)
            costo = habitacion.getPrecio().multiply(new BigDecimal(request.cantidad()));
            horasAdicionales = request.cantidad() * 24;
        }

        // Actualizar extensión en reserva
        int horasActuales = reserva.getHorasExtension() != null ? reserva.getHorasExtension() : 0;
        reserva.setHorasExtension(horasActuales + horasAdicionales);

        BigDecimal costoActual = reserva.getCostoExtension() != null ? reserva.getCostoExtension() : BigDecimal.ZERO;
        reserva.setCostoExtension(costoActual.add(costo));

        reservaService.save(reserva);

        return ResponseEntity.ok(new ExtensionResponse(
                reserva.getId(),
                request.tipo(),
                request.cantidad(),
                costo,
                reserva.getHorasExtension(),
                reserva.getCostoExtension(),
                "Extensión registrada exitosamente"));
    }

    public record ExtensionRequest(
            String tipo, // "HORAS" o "DIAS"
            int cantidad,
            String metodoPago) {
    }

    public record ExtensionResponse(
            Long reservaId,
            String tipoExtension,
            int cantidadAgregada,
            BigDecimal costoExtension,
            int totalHorasExtension,
            BigDecimal totalCostoExtension,
            String mensaje) {
    }
}
