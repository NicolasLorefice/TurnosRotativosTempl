package com.neoris.turnosrotativos.controllers;
import com.neoris.turnosrotativos.entities.EmpleadoModel;
import com.neoris.turnosrotativos.services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/empleado")
public class EmpleadoController {
    @Autowired
    EmpleadoService empleadoService;

    //obtener una lista de empleados
    @GetMapping()
    public ArrayList<EmpleadoModel> obtenerEmpleados(){
        return empleadoService.obtenerEmpleados();
    }
    //guardar empleados
    @PostMapping
    public ResponseEntity<Object> guardarEmpleado(@Valid @RequestBody EmpleadoModel empleado){
        return this.empleadoService.guardarEmpleados(empleado);
    }
    //Filtrar empleados por id
    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> obtenerEmpleadoPorId(@PathVariable("id")int id){
        return (this.empleadoService.obtenerPorId(id));
    }
    //borrar empleados por id
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> eliminarPorId(@PathVariable("id")int id) {
        return this.empleadoService.eliminarEmpleadoPorId(id);

    }
    //modificar empleados por id
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizarEmpleado(@Valid @PathVariable("id") Integer id,@Valid @RequestBody EmpleadoModel empleadoActualizado) {
       return this.empleadoService.actualizarEmpleado(id, empleadoActualizado);
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
