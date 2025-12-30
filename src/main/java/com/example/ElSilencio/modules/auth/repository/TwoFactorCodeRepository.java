package com.example.ElSilencio.modules.auth.repository;

import com.example.ElSilencio.modules.auth.model.TwoFactorCode;
import com.example.ElSilencio.modules.auth.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TwoFactorCodeRepository extends JpaRepository<TwoFactorCode, Long> {
    Optional<TwoFactorCode> findByUsuarioAndCodigoAndUsadoFalse(UsuarioModel usuario, String codigo);

    void deleteByUsuario(UsuarioModel usuario);
}
