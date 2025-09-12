package com.example.ElSilencio.Service;

import com.example.ElSilencio.Model.PagoModel;

import java.time.LocalDateTime;
import java.util.*;

public interface PagoService {
    List<PagoModel> findAll();
    Optional<PagoModel> findById(Long id);
    //Hace la busqueda personalizada

    List<PagoModel> findByReservaId(Long reservaId);
    List<PagoModel> findByMetodo(String metodo);
    List<PagoModel> findByFechaPagoBetween(LocalDateTime inicio, LocalDateTime fin);
    PagoModel save(PagoModel pago);
    void deleteByiId(Long id);
}
