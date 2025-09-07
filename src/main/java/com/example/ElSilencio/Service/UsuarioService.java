package com.example.ElSilencio.Service;

import com.example.ElSilencio.Model.UsuarioModel;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<UsuarioModel> findAll();
    Optional<UsuarioModel> findById(Long id);
    UsuarioModel save(UsuarioModel usuarioModel);
    void deleteById(Long id);
}
