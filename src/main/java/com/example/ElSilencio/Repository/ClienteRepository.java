package com.example.ElSilencio.Repository;

import com.example.ElSilencio.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteModel, Long>  {
    Optional<ClienteModel> findByDni(String dni);
    Optional<ClienteModel> findByUsername(String username);
    Optional<ClienteModel> findByEmail(String email);
    boolean existsByDni(String dni);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
