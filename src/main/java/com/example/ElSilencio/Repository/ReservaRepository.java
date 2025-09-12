package com.example.ElSilencio.Repository;

import com.example.ElSilencio.Model.ClienteModel;
import com.example.ElSilencio.Model.HabitacionModel;
import com.example.ElSilencio.Model.ReservaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaModel, Long> {
    List<ReservaModel> findByClienteId(Long clienteId);
    List<ReservaModel> findByEstado(String estado);
    List<ReservaModel> findByHabitacionId(Long habitacionId);
    List<ReservaModel> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);

}
