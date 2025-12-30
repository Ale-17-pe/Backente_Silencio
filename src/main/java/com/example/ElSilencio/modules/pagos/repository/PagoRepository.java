package com.example.ElSilencio.modules.pagos.repository;

import com.example.ElSilencio.modules.pagos.model.EstadoPagoEnum;
import com.example.ElSilencio.modules.pagos.model.PagoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<PagoModel, Long> {
    List<PagoModel> findByReservaId(Long reservaId);

    List<PagoModel> findByEstadoPago(EstadoPagoEnum estadoPago);
}
