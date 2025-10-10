package com.example.ElSilencio.Service.Impl;

import com.example.ElSilencio.Model.ClienteModel;
import com.example.ElSilencio.Repository.ClienteRepository;
import com.example.ElSilencio.Service.ClienteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }


    @Override
    public List<ClienteModel> findAll() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<ClienteModel> findById(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Optional<ClienteModel> findByDni(String dni) {
        return clienteRepository.findByDni(dni);
    }

    @Override
    public Optional<ClienteModel> findByUsername(String username) {
        return clienteRepository.findByUsername(username);
    }

    @Override
    public Optional<ClienteModel> findByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    @Override
    public ClienteModel save(ClienteModel cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public void deleteById(Long id) {
        clienteRepository.deleteById(id);

    }

    @Override
    public boolean existsByDni(String dni) {
        return clienteRepository.existsByDni(dni);
    }

    @Override
    public boolean existsByUsername(String username) {
        return clienteRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

}
