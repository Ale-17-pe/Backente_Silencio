package com.example.ElSilencio.Service;

import com.example.ElSilencio.Model.UsuarioModel;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<UsuarioModel> findAll();
    Optional<UsuarioModel> findById(Long id);
    UsuarioModel findByUsername(String username);
    UsuarioModel save(UsuarioModel usuario);
    void deleteById(Long id);
    boolean existsByUsername(String username);
}
