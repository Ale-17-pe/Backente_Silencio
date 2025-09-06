package com.example.ElSilencio.Service.Impl;

import com.example.ElSilencio.Model.UsuarioModel;
import com.example.ElSilencio.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements com.example.ElSilencio.Service.UsuarioService {

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
    public Optional<UsuarioModel> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public UsuarioModel save(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);

    }

    @Override
    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }
}
