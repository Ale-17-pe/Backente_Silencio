package com.example.ElSilencio.Service.Impl;

import com.example.ElSilencio.Model.ReservaModel;
import com.example.ElSilencio.Repository.ReservaRepository;
import com.example.ElSilencio.Service.ReservaService;
import org.springframework.stereotype.Service;

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
        return List.of();
    }

    @Override
    public Optional<ReservaModel> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public ReservaModel save(ReservaModel reserva) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
