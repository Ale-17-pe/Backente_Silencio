package com.example.ElSilencio.modules.habitaciones.repository;

import com.example.ElSilencio.modules.habitaciones.model.HabitacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitacionRepository extends JpaRepository<HabitacionModel, Long> {
    Optional<HabitacionModel> findByNumero(String numero);

    List<HabitacionModel> findByEstado(String estado);

    List<HabitacionModel> findByTipo(String tipo);
}
