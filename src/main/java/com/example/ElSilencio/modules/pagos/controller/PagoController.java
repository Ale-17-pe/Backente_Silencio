package com.example.ElSilencio.modules.pagos.controller;

import com.example.ElSilencio.modules.pagos.model.EstadoPagoEnum;
import com.example.ElSilencio.modules.pagos.model.PagoModel;
import com.example.ElSilencio.modules.pagos.service.PagoService;
import com.example.ElSilencio.modules.reservas.model.ReservaModel;
import com.example.ElSilencio.shared.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Gestión y verificación de pagos")
public class PagoController {

        private final PagoService pagoService;

        @GetMapping
        @Operation(summary = "Listar todos los pagos")
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
        public ResponseEntity<List<PagoDTO>> listar() {
                List<PagoDTO> pagos = pagoService.findAll().stream()
                                .map(this::mapToDTO)
                                .toList();
                return ResponseEntity.ok(pagos);
        }

        @GetMapping("/pendientes")
        @Operation(summary = "Listar pagos pendientes de verificación")
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
        public ResponseEntity<List<PagoDTO>> listarPendientes() {
                List<PagoDTO> pagos = pagoService.findByEstadoPago(EstadoPagoEnum.PENDIENTE_VERIFICACION).stream()
                                .map(this::mapToDTO)
                                .toList();
                return ResponseEntity.ok(pagos);
        }

        @PostMapping("/{id}/aprobar")
        @Operation(summary = "Aprobar un pago")
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
        public ResponseEntity<PagoDTO> aprobar(@PathVariable Long id, Authentication auth) {
                PagoModel pago = pagoService.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));

                pago.setEstadoPago(EstadoPagoEnum.VERIFICADO);
                pago.setVerificadoPor(auth.getName());
                pago.setFechaVerificacion(LocalDateTime.now());
                pago.setMotivoRechazo(null);

                PagoModel saved = pagoService.save(pago);
                return ResponseEntity.ok(mapToDTO(saved));
        }

        @PostMapping("/{id}/rechazar")
        @Operation(summary = "Rechazar un pago")
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
        public ResponseEntity<PagoDTO> rechazar(
                        @PathVariable Long id,
                        @RequestParam String motivo,
                        Authentication auth) {

                PagoModel pago = pagoService.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));

                pago.setEstadoPago(EstadoPagoEnum.RECHAZADO);
                pago.setVerificadoPor(auth.getName());
                pago.setFechaVerificacion(LocalDateTime.now());
                pago.setMotivoRechazo(motivo);

                PagoModel saved = pagoService.save(pago);
                return ResponseEntity.ok(mapToDTO(saved));
        }

        @PostMapping(value = "/registrar", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Registrar pago en recepción")
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
        public ResponseEntity<PagoDTO> registrarPago(
                        @RequestParam Long reservaId,
                        @RequestParam java.math.BigDecimal monto,
                        @RequestParam String metodoPago,
                        @RequestParam(required = false) org.springframework.web.multipart.MultipartFile evidencia,
                        Authentication auth) {

                ReservaModel reserva = pagoService.findReservaById(reservaId);
                if (reserva == null) {
                        throw new ResourceNotFoundException("Reserva", reservaId);
                }

                // Guardar evidencia si existe
                String evidenciaUrl = null;
                if (evidencia != null && !evidencia.isEmpty()) {
                        try {
                                java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads/evidencias/");
                                if (!java.nio.file.Files.exists(uploadPath)) {
                                        java.nio.file.Files.createDirectories(uploadPath);
                                }
                                String fileName = "pago_" + java.util.UUID.randomUUID().toString().substring(0, 8)
                                                + ".jpg";
                                java.nio.file.Files.copy(evidencia.getInputStream(), uploadPath.resolve(fileName));
                                evidenciaUrl = "uploads/evidencias/" + fileName;
                        } catch (java.io.IOException e) {
                                // Ignorar error de evidencia, el pago sigue siendo válido
                        }
                }

                // Determinar método de pago
                com.example.ElSilencio.modules.pagos.model.MetodoPagoEnum metodo = switch (metodoPago.toUpperCase()) {
                        case "YAPE" -> com.example.ElSilencio.modules.pagos.model.MetodoPagoEnum.YAPE;
                        case "PLIN" -> com.example.ElSilencio.modules.pagos.model.MetodoPagoEnum.PLIN;
                        case "TRANSFERENCIA" -> com.example.ElSilencio.modules.pagos.model.MetodoPagoEnum.TRANSFERENCIA;
                        default -> com.example.ElSilencio.modules.pagos.model.MetodoPagoEnum.EFECTIVO;
                };

                PagoModel pago = new PagoModel();
                pago.setReserva(reserva);
                pago.setMonto(monto);
                pago.setMetodoPago(metodo);
                pago.setFechaPago(LocalDateTime.now());
                pago.setEvidenciaUrl(evidenciaUrl);
                pago.setEstadoPago(EstadoPagoEnum.VERIFICADO); // Pago en recepción = verificado
                pago.setVerificadoPor(auth.getName());
                pago.setFechaVerificacion(LocalDateTime.now());

                PagoModel saved = pagoService.save(pago);
                return ResponseEntity.ok(mapToDTO(saved));
        }

        private PagoDTO mapToDTO(PagoModel p) {
                return new PagoDTO(
                                p.getId(),
                                p.getReserva().getId(),
                                p.getReserva().getCodigoReserva(),
                                p.getReserva().getCliente() != null
                                                ? p.getReserva().getCliente().getNombre() + " "
                                                                + p.getReserva().getCliente().getApellido()
                                                : null,
                                p.getReserva().getHabitacion() != null ? p.getReserva().getHabitacion().getNumero()
                                                : null,
                                p.getMonto(),
                                p.getMetodoPago().name(),
                                p.getEstadoPago().name(),
                                p.getEvidenciaUrl(),
                                p.getFechaPago(),
                                p.getVerificadoPor(),
                                p.getFechaVerificacion(),
                                p.getMotivoRechazo());
        }

        public record PagoDTO(
                        Long id,
                        Long reservaId,
                        String codigoReserva,
                        String clienteNombre,
                        String habitacionNumero,
                        java.math.BigDecimal monto,
                        String metodoPago,
                        String estadoPago,
                        String evidenciaUrl,
                        LocalDateTime fechaPago,
                        String verificadoPor,
                        LocalDateTime fechaVerificacion,
                        String motivoRechazo) {
        }
}
