package com.example.ElSilencio.modules.reservas.repository;

import com.example.ElSilencio.modules.reservas.model.ReservaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaModel, Long> {
    List<ReservaModel> findByClienteId(Long clienteId);

    List<ReservaModel> findByHabitacionId(Long habitacionId);

    List<ReservaModel> findByEstado(String estado);

    Optional<ReservaModel> findByCodigoReserva(String codigoReserva);

    @Query("SELECT r FROM ReservaModel r WHERE r.fechaInicio >= :inicio AND r.fechaFin <= :fin")
    List<ReservaModel> findByDateRange(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COUNT(r) > 0 FROM ReservaModel r WHERE r.habitacion.id = :habitacionId " +
            "AND r.estado NOT IN ('CANCELADA', 'COMPLETADA') " +
            "AND ((r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio))")
    boolean existsConflictingReservation(@Param("habitacionId") Long habitacionId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
