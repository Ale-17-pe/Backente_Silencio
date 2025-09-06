package com.example.ElSilencio.Repository;

import com.example.ElSilencio.Model.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteModel, Long>  {
    Optional<ClienteModel> findByDni(String dni);
}
