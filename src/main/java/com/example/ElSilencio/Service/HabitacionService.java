package com.example.ElSilencio.Service;

import com.example.ElSilencio.Model.HabitacionModel;

import java.util.List;
import java.util.Optional;

public interface HabitacionService {
    List<HabitacionModel> findAll();
    Optional<HabitacionModel> findById(Long id);
    List<HabitacionModel> findByEstado(String estado);
    HabitacionModel save(HabitacionModel habitacion);
    void deleteById(Long id);
}
