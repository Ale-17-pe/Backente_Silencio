package com.example.ElSilencio.modules.auth.repository;

import com.example.ElSilencio.modules.auth.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    Optional<UsuarioModel> findByUsername(String username);

    Optional<UsuarioModel> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
