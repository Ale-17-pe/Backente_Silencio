package com.example.ElSilencio.Service;

import com.example.ElSilencio.Model.ClienteModel;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    /*Obtienes los datos del cliente*/
    List<ClienteModel> findAll();
    /*Busca con su ID*/
    Optional<ClienteModel> findById(Long id);
    /*Busca por su DNI*/
    Optional<ClienteModel> findByDni(String dni);
    Optional<ClienteModel> findByUsername(String username);
    Optional<ClienteModel> findByEmail(String email);
    /*Guardo o actualiza*/
    ClienteModel save(ClienteModel cliente);
    /*elemina por su ID*/
    void deleteById(Long id);
    /*Verfica si existe un DNI espesifico*/
    boolean existsByDni(String dni);
    /*Verfica si existe con su DNI espesifico*/
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
