package com.example.ElSilencio.Service.Impl;

import com.example.ElSilencio.Model.HabitacionModel;
import com.example.ElSilencio.Repository.HabitacionRepository;
import com.example.ElSilencio.Service.HabitacionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HabitacionServiceImpl implements HabitacionService{

    private final HabitacionRepository habitacionRepository;

    public HabitacionServiceImpl(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }


    @Override
    public List<HabitacionModel> findAll() {
        return  habitacionRepository.findAll();
    }

    @Override
    public Optional<HabitacionModel> findById(Long id) {
        return habitacionRepository.findById(id);
    }

    @Override
    public List<HabitacionModel> findByEstado(String estado) {
        return habitacionRepository.findByEstado(estado);
    }

    @Override
    public Optional<HabitacionModel> findByNumero(String numero) {
        return habitacionRepository.findByNumero(numero);
    }

    @Override
    public List<HabitacionModel> findByTipo(String tipo) {
        return habitacionRepository.findByTipo(tipo);
    }

    @Override
    public HabitacionModel save(HabitacionModel habitacion) {
        return habitacionRepository.save(habitacion);
    }

    @Override
    public void deleteById(Long id) {
        habitacionRepository.deleteById(id);
    }
}
