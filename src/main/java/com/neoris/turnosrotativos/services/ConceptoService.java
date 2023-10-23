package com.neoris.turnosrotativos.services;
import com.neoris.turnosrotativos.repositories.ConceptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.neoris.turnosrotativos.entities.Concepto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Optional;


@Service
public class ConceptoService {
    @Autowired
    ConceptoRepository conceptoRepository;

    //metodo para mostrar los conceptos
    public ArrayList<Concepto> obtenerConcepto(){
        return (ArrayList<Concepto>)conceptoRepository.findAll();
    }

    public Optional<Concepto> obtenerConceptoPorID(Integer idConcepto) {
      return conceptoRepository.findById(idConcepto);
    }

    public Integer obtenerHsMinimasPorId(Integer nroConcepto) {
        return conceptoRepository.findById(nroConcepto).get().getHsMinimo();
    }

    public Integer obtenerHsMaximasPorId(Integer nroConcepto) {
        return conceptoRepository.findById(nroConcepto).get().getHsMaximo();
    }
}
