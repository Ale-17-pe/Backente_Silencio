package com.example.ElSilencio.modules.habitaciones.repository;

import com.example.ElSilencio.modules.habitaciones.model.TarifaExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifaExtensionRepository extends JpaRepository<TarifaExtension, Long> {
    Optional<TarifaExtension> findByTipoHabitacion(String tipoHabitacion);

    Optional<TarifaExtension> findByEsTarifaDefaultTrue();
}
