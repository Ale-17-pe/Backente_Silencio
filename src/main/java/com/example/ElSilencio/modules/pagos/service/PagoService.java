package com.example.ElSilencio.modules.pagos.service;

import com.example.ElSilencio.modules.pagos.model.EstadoPagoEnum;
import com.example.ElSilencio.modules.pagos.model.PagoModel;
import com.example.ElSilencio.modules.pagos.repository.PagoRepository;
import com.example.ElSilencio.modules.reservas.model.ReservaModel;
import com.example.ElSilencio.modules.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    public List<PagoModel> findAll() {
        return pagoRepository.findAll();
    }

    public Optional<PagoModel> findById(Long id) {
        return pagoRepository.findById(id);
    }

    public List<PagoModel> findByReservaId(Long reservaId) {
        return pagoRepository.findByReservaId(reservaId);
    }

    public List<PagoModel> findByEstadoPago(EstadoPagoEnum estado) {
        return pagoRepository.findByEstadoPago(estado);
    }

    public PagoModel save(PagoModel pago) {
        return pagoRepository.save(pago);
    }

    public void deleteById(Long id) {
        pagoRepository.deleteById(id);
    }

    public ReservaModel findReservaById(Long reservaId) {
        return reservaRepository.findById(reservaId).orElse(null);
    }
}
