package com.example.ElSilencio.Service.Impl;

import com.example.ElSilencio.Model.PagoModel;
import com.example.ElSilencio.Repository.PagoRepository;
import com.example.ElSilencio.Service.PagoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagoServideImpl implements PagoService {
    private final PagoRepository pagoRepository;

    public PagoServideImpl(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }


    @Override
    public List<PagoModel> findAll() {
        return pagoRepository.findAll();
    }

    @Override
    public Optional<PagoModel> findById(Long id) {
        return pagoRepository.findById(id);
    }

    @Override
    public List<PagoModel> findByReservaId(Long reservaId) {
        return pagoRepository.findByReservaId(reservaId);
    }

    @Override
    public List<PagoModel> findByMetodo(String metodo) {
        return pagoRepository.findByMetodo(metodo);
    }

    @Override
    public List<PagoModel> findByFechaPagoBetween(LocalDateTime inicio, LocalDateTime fin) {
        return pagoRepository.findByFechaPagoBetween(inicio,fin);
    }

    @Override
    public PagoModel save(PagoModel pago) {
        return pagoRepository.save(pago);
    }

    @Override
    public void deleteByiId(Long id) {
        pagoRepository.deleteById(id);
    }

}
