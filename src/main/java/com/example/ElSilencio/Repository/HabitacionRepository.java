package com.example.ElSilencio.Repository;

import com.example.ElSilencio.Model.ClienteModel;
import com.example.ElSilencio.Model.HabitacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitacionRepository  extends JpaRepository<HabitacionModel, Long> {
    List<HabitacionModel> findByEstado(String estado);
    List<HabitacionModel> findByTipo(String tipo);
    Optional<HabitacionModel> findByNumero(String numero);
    List<HabitacionModel> findByPrecioBetween(BigDecimal min, BigDecimal max);

}
