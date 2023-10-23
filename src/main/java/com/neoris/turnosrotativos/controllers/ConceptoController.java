package com.neoris.turnosrotativos.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.neoris.turnosrotativos.entities.Concepto;
import java.util.ArrayList;

import com.neoris.turnosrotativos.services.ConceptoService;

@RestController
@RequestMapping("/concepto")
public class ConceptoController {
    @Autowired
    ConceptoService conceptoService;

    @GetMapping()
    public ArrayList<Concepto> obtenerConcepto(){return conceptoService.obtenerConcepto();}



}
