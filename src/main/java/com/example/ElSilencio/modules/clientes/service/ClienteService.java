package com.example.ElSilencio.modules.clientes.service;

import com.example.ElSilencio.modules.clientes.model.ClienteModel;
import com.example.ElSilencio.modules.clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<ClienteModel> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<ClienteModel> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<ClienteModel> findByDni(String dni) {
        return clienteRepository.findByDni(dni);
    }

    public Optional<ClienteModel> findByUsername(String username) {
        return clienteRepository.findByUsername(username);
    }

    public ClienteModel save(ClienteModel cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }

    public boolean existsByDni(String dni) {
        return clienteRepository.existsByDni(dni);
    }

    public boolean existsByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return clienteRepository.existsByUsername(username);
    }
}
