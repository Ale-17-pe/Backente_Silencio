package com.example.ElSilencio.Service;

import com.example.ElSilencio.Model.UsuarioModel;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<UsuarioModel> findAll();
    Optional<UsuarioModel> findById(Long id);
    Optional<UsuarioModel> findByUsername(String username);
    UsuarioModel save(UsuarioModel usuario);
    void delete(Long id);
}
