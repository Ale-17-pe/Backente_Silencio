package com.example.ElSilencio.Service;

import com.example.ElSilencio.Model.PagoModel;
import java.util.*;

public interface PagoService {
    List<PagoModel> findAll();
    Optional<PagoModel> findById(Long id);
    List<PagoModel> findByReservaId(Long reservaId);
    PagoModel save(PagoModel pago);
    void delete(Long id);
}
