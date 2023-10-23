package com.neoris.turnosrotativos.repositories;
import com.neoris.turnosrotativos.entities.Concepto;
import com.neoris.turnosrotativos.entities.EmpleadoModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConceptoRepository extends CrudRepository<Concepto,Integer> {

    Optional<Concepto> findById(Integer id);

}
