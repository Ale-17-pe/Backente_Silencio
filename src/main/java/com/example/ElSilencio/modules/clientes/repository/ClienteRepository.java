package com.example.ElSilencio.modules.clientes.repository;

import com.example.ElSilencio.modules.clientes.model.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteModel, Long> {
    Optional<ClienteModel> findByDni(String dni);

    Optional<ClienteModel> findByEmail(String email);

    Optional<ClienteModel> findByUsername(String username);

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
