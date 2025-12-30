package com.example.ElSilencio.modules.habitaciones.service;

import com.example.ElSilencio.modules.habitaciones.model.HabitacionModel;
import com.example.ElSilencio.modules.habitaciones.repository.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public List<HabitacionModel> findAll() {
        return habitacionRepository.findAll();
    }

    public Optional<HabitacionModel> findById(Long id) {
        return habitacionRepository.findById(id);
    }

    public Optional<HabitacionModel> findByNumero(String numero) {
        return habitacionRepository.findByNumero(numero);
    }

    public List<HabitacionModel> findByEstado(String estado) {
        return habitacionRepository.findByEstado(estado);
    }

    public HabitacionModel save(HabitacionModel habitacion) {
        return habitacionRepository.save(habitacion);
    }

    /**
     * Encuentra la primera habitacion disponible del tipo especificado
     * para auto-asignacion en reservas online
     */
    public Optional<HabitacionModel> findFirstAvailableByTipo(String tipo) {
        return habitacionRepository.findAll().stream()
                .filter(h -> tipo.equalsIgnoreCase(h.getTipo()))
                .filter(h -> "DISPONIBLE".equalsIgnoreCase(h.getEstado()))
                .findFirst();
    }

    public void deleteById(Long id) {
        habitacionRepository.deleteById(id);
    }
}
