package com.example.ElSilencio.modules.reportes.controller;

import com.example.ElSilencio.modules.pagos.model.PagoModel;
import com.example.ElSilencio.modules.pagos.service.PagoService;
import com.example.ElSilencio.modules.reservas.model.ReservaModel;
import com.example.ElSilencio.modules.reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Generación de reportes y exportación")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
public class ReportController {

        private final ReservaService reservaService;
        private final PagoService pagoService;

        @GetMapping("/resumen")
        @Operation(summary = "Obtener resumen general del sistema")
        public ResponseEntity<ResumenDTO> getResumen() {
                List<ReservaModel> reservas = reservaService.findAll();
                List<PagoModel> pagos = pagoService.findAll();

                BigDecimal ingresosTotales = pagos.stream()
                                .map(PagoModel::getMonto)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                long reservasActivas = reservas.stream()
                                .filter(r -> "EN_CURSO".equals(r.getEstado()) || "ACTIVA".equals(r.getEstado()))
                                .count();

                long reservasCompletadas = reservas.stream()
                                .filter(r -> "COMPLETADA".equals(r.getEstado()))
                                .count();

                long pagosPendientes = pagos.stream()
                                .filter(p -> "PENDIENTE_VERIFICACION".equals(p.getEstadoPago().name()))
                                .count();

                return ResponseEntity.ok(new ResumenDTO(
                                reservas.size(),
                                reservasActivas,
                                reservasCompletadas,
                                pagosPendientes,
                                ingresosTotales));
        }

        @GetMapping("/reservas")
        @Operation(summary = "Obtener reporte de reservas con filtros")
        public ResponseEntity<List<ReservaReporteDTO>> getReportReservas(
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                        @RequestParam(required = false) String estado) {

                List<ReservaModel> reservas = reservaService.findAll();

                // Aplicar filtros
                if (desde != null) {
                        reservas = reservas.stream()
                                        .filter(r -> !r.getFechaInicio().isBefore(desde))
                                        .collect(Collectors.toList());
                }
                if (hasta != null) {
                        reservas = reservas.stream()
                                        .filter(r -> !r.getFechaInicio().isAfter(hasta))
                                        .collect(Collectors.toList());
                }
                if (estado != null && !estado.isEmpty()) {
                        reservas = reservas.stream()
                                        .filter(r -> estado.equals(r.getEstado()))
                                        .collect(Collectors.toList());
                }

                List<ReservaReporteDTO> reporte = reservas.stream()
                                .map(r -> new ReservaReporteDTO(
                                                r.getId(),
                                                r.getCodigoReserva(),
                                                r.getCliente() != null
                                                                ? r.getCliente().getNombre() + " "
                                                                                + r.getCliente().getApellido()
                                                                : null,
                                                r.getCliente() != null ? r.getCliente().getDni() : null,
                                                r.getHabitacion() != null ? r.getHabitacion().getNumero() : null,
                                                r.getHabitacion() != null ? r.getHabitacion().getTipo() : null,
                                                r.getFechaInicio(),
                                                r.getFechaFin(),
                                                r.getEstado(),
                                                r.getTotal()))
                                .toList();

                return ResponseEntity.ok(reporte);
        }

        @GetMapping("/ingresos")
        @Operation(summary = "Obtener reporte de ingresos agrupados por período")
        public ResponseEntity<List<IngresosPeriodoDTO>> getReportIngresos(
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                        @RequestParam(defaultValue = "dia") String agrupar) { // dia, mes

                List<PagoModel> pagos = pagoService.findAll();

                // Aplicar filtros de fecha
                if (desde != null) {
                        LocalDateTime desdeTime = desde.atStartOfDay();
                        pagos = pagos.stream()
                                        .filter(p -> !p.getFechaPago().isBefore(desdeTime))
                                        .collect(Collectors.toList());
                }
                if (hasta != null) {
                        LocalDateTime hastaTime = hasta.atTime(23, 59, 59);
                        pagos = pagos.stream()
                                        .filter(p -> !p.getFechaPago().isAfter(hastaTime))
                                        .collect(Collectors.toList());
                }

                // Agrupar por período
                Map<String, BigDecimal> agrupados;
                if ("mes".equals(agrupar)) {
                        agrupados = pagos.stream()
                                        .collect(Collectors.groupingBy(
                                                        p -> p.getFechaPago().toLocalDate().withDayOfMonth(1)
                                                                        .toString(),
                                                        Collectors.reducing(BigDecimal.ZERO, PagoModel::getMonto,
                                                                        BigDecimal::add)));
                } else {
                        agrupados = pagos.stream()
                                        .collect(Collectors.groupingBy(
                                                        p -> p.getFechaPago().toLocalDate().toString(),
                                                        Collectors.reducing(BigDecimal.ZERO, PagoModel::getMonto,
                                                                        BigDecimal::add)));
                }

                List<IngresosPeriodoDTO> resultado = agrupados.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(e -> new IngresosPeriodoDTO(e.getKey(), e.getValue()))
                                .toList();

                return ResponseEntity.ok(resultado);
        }

        @GetMapping("/exportar/reservas")
        @Operation(summary = "Exportar reservas a CSV")
        public ResponseEntity<byte[]> exportarReservasCSV(
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

                List<ReservaModel> reservas = reservaService.findAll();

                if (desde != null) {
                        reservas = reservas.stream()
                                        .filter(r -> !r.getFechaInicio().isBefore(desde))
                                        .collect(Collectors.toList());
                }
                if (hasta != null) {
                        reservas = reservas.stream()
                                        .filter(r -> !r.getFechaInicio().isAfter(hasta))
                                        .collect(Collectors.toList());
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintWriter writer = new PrintWriter(out);

                // Header
                writer.println("Codigo,Cliente,DNI,Habitacion,Tipo,Fecha Inicio,Fecha Fin,Estado,Total");

                // Data
                for (ReservaModel r : reservas) {
                        writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%.2f%n",
                                        r.getCodigoReserva(),
                                        r.getCliente() != null
                                                        ? r.getCliente().getNombre() + " "
                                                                        + r.getCliente().getApellido()
                                                        : "",
                                        r.getCliente() != null ? r.getCliente().getDni() : "",
                                        r.getHabitacion() != null ? r.getHabitacion().getNumero() : "",
                                        r.getHabitacion() != null ? r.getHabitacion().getTipo() : "",
                                        r.getFechaInicio(),
                                        r.getFechaFin(),
                                        r.getEstado(),
                                        r.getTotal());
                }

                writer.flush();
                byte[] data = out.toByteArray();

                String filename = "reservas_" + LocalDate.now() + ".csv";

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                                .contentType(MediaType.parseMediaType("text/csv"))
                                .body(data);
        }

        @GetMapping("/exportar/ingresos")
        @Operation(summary = "Exportar ingresos a CSV")
        public ResponseEntity<byte[]> exportarIngresosCSV(
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

                List<PagoModel> pagos = pagoService.findAll();

                if (desde != null) {
                        LocalDateTime desdeTime = desde.atStartOfDay();
                        pagos = pagos.stream()
                                        .filter(p -> !p.getFechaPago().isBefore(desdeTime))
                                        .collect(Collectors.toList());
                }
                if (hasta != null) {
                        LocalDateTime hastaTime = hasta.atTime(23, 59, 59);
                        pagos = pagos.stream()
                                        .filter(p -> !p.getFechaPago().isAfter(hastaTime))
                                        .collect(Collectors.toList());
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintWriter writer = new PrintWriter(out);

                // Header
                writer.println("Fecha,Reserva,Cliente,Metodo,Monto,Estado");

                // Data
                for (PagoModel p : pagos) {
                        writer.printf("%s,%s,%s,%s,%.2f,%s%n",
                                        p.getFechaPago().toLocalDate(),
                                        p.getReserva().getCodigoReserva(),
                                        p.getReserva().getCliente() != null
                                                        ? p.getReserva().getCliente().getNombre() + " "
                                                                        + p.getReserva().getCliente().getApellido()
                                                        : "",
                                        p.getMetodoPago().name(),
                                        p.getMonto(),
                                        p.getEstadoPago().name());
                }

                writer.flush();
                byte[] data = out.toByteArray();

                String filename = "ingresos_" + LocalDate.now() + ".csv";

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                                .contentType(MediaType.parseMediaType("text/csv"))
                                .body(data);
        }

        // DTOs
        public record ResumenDTO(
                        long totalReservas,
                        long reservasActivas,
                        long reservasCompletadas,
                        long pagosPendientes,
                        BigDecimal ingresosTotales) {
        }

        public record ReservaReporteDTO(
                        Long id,
                        String codigoReserva,
                        String clienteNombre,
                        String clienteDni,
                        String habitacionNumero,
                        String habitacionTipo,
                        LocalDate fechaInicio,
                        LocalDate fechaFin,
                        String estado,
                        BigDecimal total) {
        }

        public record IngresosPeriodoDTO(
                        String periodo,
                        BigDecimal monto) {
        }
}
