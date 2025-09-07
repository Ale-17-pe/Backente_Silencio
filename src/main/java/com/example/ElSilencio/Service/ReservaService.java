package com.example.ElSilencio.Service;

import com.example.ElSilencio.Model.ReservaModel;

import java.util.*;

public interface ReservaService {
    List<ReservaModel> findAll();
    Optional<ReservaModel> findById(Long id);
    ReservaModel save(ReservaModel reserva);
    void deleteById(Long id);
}
