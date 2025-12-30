package com.example.ElSilencio.modules.auth.service;

import com.example.ElSilencio.modules.auth.model.RolEnum;
import com.example.ElSilencio.modules.auth.model.UsuarioModel;
import com.example.ElSilencio.modules.auth.repository.UsuarioRepository;
import com.example.ElSilencio.shared.exception.BadRequestException;
import com.example.ElSilencio.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioModel> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<UsuarioModel> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<UsuarioModel> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public UsuarioModel save(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }

    public UsuarioModel crear(String username, String email, String password, RolEnum rol) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new BadRequestException("El username ya existe");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new BadRequestException("El email ya está registrado");
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    public UsuarioModel actualizar(Long id, String username, String email, RolEnum rol) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        if (!usuario.getUsername().equals(username) && usuarioRepository.existsByUsername(username)) {
            throw new BadRequestException("El username ya existe");
        }
        if (!usuario.getEmail().equals(email) && usuarioRepository.existsByEmail(email)) {
            throw new BadRequestException("El email ya está registrado");
        }

        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    public UsuarioModel cambiarRol(Long id, RolEnum nuevoRol) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        usuario.setRol(nuevoRol);
        return usuarioRepository.save(usuario);
    }

    public String resetearPassword(Long id) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        String nuevaPassword = UUID.randomUUID().toString().substring(0, 8);
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        return nuevaPassword;
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", id);
        }
        usuarioRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
