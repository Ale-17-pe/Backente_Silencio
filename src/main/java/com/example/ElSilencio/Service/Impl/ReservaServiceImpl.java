package com.example.ElSilencio.Service.Impl;

import com.example.ElSilencio.Model.*;
import com.example.ElSilencio.Repository.ReservaRepository;
import com.example.ElSilencio.Service.ReservaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {
    private final ReservaRepository reservaRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Override
    public List<ReservaModel> findAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Optional<ReservaModel> findById(Long id) {
        return reservaRepository.findById(id);
    }

    @Override
    public ReservaModel save(ReservaModel reserva) {
        return reservaRepository.save(reserva);
    }

    @Override
    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }

    @Override
    public List<ReservaModel> findByClienteId(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    @Override
    public List<ReservaModel> findByHabitacionId(Long habitacionId) {
        return reservaRepository.findByHabitacionId(habitacionId);
    }

    @Override
    public List<ReservaModel> findByEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }

    @Override
    public List<ReservaModel> findByFechaInicioBetween(LocalDate fechasInicio, LocalDate fechaFin) {
        return reservaRepository.findByFechaInicioBetween(fechasInicio, fechaFin);
    }

}
