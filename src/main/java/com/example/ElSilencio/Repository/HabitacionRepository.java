package com.example.ElSilencio.Repository;

import com.example.ElSilencio.Model.HabitacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitacionRepository  extends JpaRepository<HabitacionModel, Long> {
    List<HabitacionModel> findByEstado(String estado);

}
