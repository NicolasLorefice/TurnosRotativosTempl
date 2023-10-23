package com.neoris.turnosrotativos.repositories;

import com.neoris.turnosrotativos.entities.JornadaModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface JornadaRepository extends CrudRepository<JornadaModel,Integer> {

   public Optional<JornadaModel> findById(Integer integer);

   public List<JornadaModel> findByNroDocumentoAndFechaDeJornada(Integer nroDocumento, String fecha);

   public List<JornadaModel> findByNroDocumento(Integer nroDocumento);

   List<JornadaModel> findByFechaDeJornada(String fechaDeJornada );
}
