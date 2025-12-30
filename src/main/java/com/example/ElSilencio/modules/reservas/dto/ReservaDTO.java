package com.example.ElSilencio.modules.reservas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ReservaDTO(
                Long id,
                String codigoReserva,
                // Cliente completo
                ClienteInfo cliente,
                // Habitacion completa
                HabitacionInfo habitacion,
                // Fechas
                LocalDate fechaInicio,
                LocalDate fechaFin,
                LocalDateTime horaCheckin,
                LocalDateTime horaCheckout,
                // Montos
                BigDecimal total,
                BigDecimal costoExtension,
                Integer horasExtension,
                // Estado
                String estado,
                String tipoReserva,
                String notas,
                // Pagos con detalles
                List<PagoInfo> pagos) {

        public record ClienteInfo(
                        Long id,
                        String nombre,
                        String apellido,
                        String dni,
                        String email,
                        String telefono) {
        }

        public record HabitacionInfo(
                        Long id,
                        String numero,
                        String tipo,
                        BigDecimal precio,
                        String imagenUrl) {
        }

        public record PagoInfo(
                        Long id,
                        BigDecimal monto,
                        String metodoPago,
                        String estadoPago,
                        String evidenciaUrl,
                        LocalDateTime fechaPago,
                        String verificadoPor,
                        LocalDateTime fechaVerificacion,
                        String motivoRechazo) {
        }
}
