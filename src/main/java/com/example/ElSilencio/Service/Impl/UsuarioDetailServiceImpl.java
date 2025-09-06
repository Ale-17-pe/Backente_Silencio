package com.example.ElSilencio.Service.Impl;

import com.example.ElSilencio.Model.UsuarioModel;
import com.example.ElSilencio.Repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UsuarioDetailServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioModel usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toUpperCase()))
        );
    }

}
