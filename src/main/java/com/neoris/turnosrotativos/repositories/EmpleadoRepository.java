package com.neoris.turnosrotativos.repositories;
import com.neoris.turnosrotativos.entities.EmpleadoModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpleadoRepository extends CrudRepository<EmpleadoModel, Integer>{
    Optional<EmpleadoModel> findByEmail(String email);
    Optional<EmpleadoModel> findById(Integer id);

    Optional<EmpleadoModel> findByNroDocumento(Integer nroDocumento);



}
