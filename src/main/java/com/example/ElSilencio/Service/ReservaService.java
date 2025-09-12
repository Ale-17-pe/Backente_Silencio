package com.example.ElSilencio.Service;

import com.example.ElSilencio.Model.ReservaModel;
import com.example.ElSilencio.Model.UsuarioModel;

import java.time.LocalDate;
import java.util.*;

public interface ReservaService {
    List<ReservaModel> findAll();
    Optional<ReservaModel> findById(Long id);

    //Guarda o actualiza la reserva
    ReservaModel save(ReservaModel reserva);
    // Elemina el Id o la reserva
    void deleteById(Long id);

    List<ReservaModel> findByClienteId(Long clienteId);
    List<ReservaModel> findByHabitacionId(Long HabitacionId);
    List<ReservaModel> findByEstado(String estado);
    List<ReservaModel> findByFechaInicioBetween(LocalDate fechasInicio, LocalDate fechaFin);
}
