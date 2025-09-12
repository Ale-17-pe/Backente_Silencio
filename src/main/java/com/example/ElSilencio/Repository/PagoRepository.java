package com.example.ElSilencio.Repository;

import com.example.ElSilencio.Model.PagoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<PagoModel, Long> {
    List<PagoModel> findByReservaId(Long reservasId);
    List<PagoModel> findByMetodo(String metodo);
    List<PagoModel> findByFechaPagoBetween(LocalDateTime inicio, LocalDateTime fin);
}
