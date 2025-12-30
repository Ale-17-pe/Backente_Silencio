package com.example.ElSilencio.modules.reservas.service;

import com.example.ElSilencio.modules.reservas.model.ReservaModel;
import com.example.ElSilencio.modules.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public List<ReservaModel> findAll() {
        return reservaRepository.findAll();
    }

    public Optional<ReservaModel> findById(Long id) {
        return reservaRepository.findById(id);
    }

    public Optional<ReservaModel> findByCodigoReserva(String codigo) {
        return reservaRepository.findByCodigoReserva(codigo);
    }

    public List<ReservaModel> findByClienteId(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    public List<ReservaModel> findByEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }

    public List<ReservaModel> findByDateRange(LocalDate inicio, LocalDate fin) {
        return reservaRepository.findByDateRange(inicio, fin);
    }

    public boolean existsConflictingReservation(Long habitacionId, LocalDate inicio, LocalDate fin) {
        return reservaRepository.existsConflictingReservation(habitacionId, inicio, fin);
    }

    public ReservaModel save(ReservaModel reserva) {
        return reservaRepository.save(reserva);
    }

    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }
}
