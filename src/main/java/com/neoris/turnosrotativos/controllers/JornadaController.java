package com.neoris.turnosrotativos.controllers;
import com.neoris.turnosrotativos.entities.JornadaModel;
import com.neoris.turnosrotativos.entities.dtos.JornadaDTO;
import com.neoris.turnosrotativos.services.JornadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/jornada")
public class JornadaController {

    @Autowired
    JornadaService jornadaService;
    //creo una jornada nueva
    @PostMapping
    public ResponseEntity<Object> crearJornada(@Valid @RequestBody JornadaDTO jornadaDTO){
        return jornadaService.crearJornada(jornadaDTO);
    }

    @GetMapping
    public List<JornadaModel>obtenerJornada(
            @RequestParam(name = "fechaDeJornada", required = false) String fechaDeJornada,
            @RequestParam(name = "nroDocumento", required = false) Integer nroDocumento
    ){
        if(fechaDeJornada!= null && nroDocumento==null){
            return (List<JornadaModel>) jornadaService.obtenerJornadaPorFecha(fechaDeJornada);
        }
        else if(fechaDeJornada!=null && nroDocumento !=null){
            return (List<JornadaModel>) jornadaService.obtenerJornadaPorFechaYNroDocumento(fechaDeJornada,nroDocumento);

        }else if(fechaDeJornada==null&&nroDocumento!=null){
               return jornadaService.obtenerJornadaPorDocumento(nroDocumento);
        }
        return jornadaService.obtenerJornada();
    }



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
