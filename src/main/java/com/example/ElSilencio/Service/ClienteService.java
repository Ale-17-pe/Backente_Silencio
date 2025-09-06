package com.example.ElSilencio.Service;

import com.example.ElSilencio.Model.ClienteModel;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<ClienteModel> findAll();
    Optional<ClienteModel> findById(Long id);
    Optional<ClienteModel> findByDni(String dni);
    ClienteModel save(ClienteModel cliente);
    void delete(Long id);
}
