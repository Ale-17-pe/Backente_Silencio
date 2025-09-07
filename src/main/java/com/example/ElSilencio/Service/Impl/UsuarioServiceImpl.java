package com.example.ElSilencio.Service.Impl;

import com.example.ElSilencio.Model.UsuarioModel;
import com.example.ElSilencio.Repository.UsuarioRepository;
import com.example.ElSilencio.Service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    public List<UsuarioModel> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<UsuarioModel> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public UsuarioModel save(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}
