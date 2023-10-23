package com.neoris.turnosrotativos.services;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.neoris.turnosrotativos.entities.EmpleadoModel;
import com.neoris.turnosrotativos.repositories.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;



import java.util.regex.Pattern;

@Service
public class EmpleadoService {

    @Autowired
    EmpleadoRepository empleadoRepository;

    //metodo para mostrar los empleados en la tabla
    public ArrayList<EmpleadoModel> obtenerEmpleados() {
        return (ArrayList<EmpleadoModel>) empleadoRepository.findAll();
    }

    //metodo para guardar empleados
    public ResponseEntity<Object> guardarEmpleados(EmpleadoModel empleadoModel) {
        LocalDateTime fechaCreacion = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        empleadoModel.setFechaCreacion(fechaCreacion.format(formatter));
        return validarIngresoEmpleado(empleadoModel);


    }

    //metodo para obtener los empleados por nro de id
    public ResponseEntity<Object> obtenerPorId(int id) {
        if(empleadoRepository.findById(id).isPresent()){
        return  new ResponseEntity<Object>(empleadoRepository.findById(id),HttpStatus.OK);}
        return new ResponseEntity<Object>("El id no se encuentra en la base de datos: "+id, HttpStatus.BAD_REQUEST);
    }

    public Optional<EmpleadoModel> obtenerPorId(Integer id) {
            return  empleadoRepository.findById(id);
    }


    //eliminar empleado por id
    public ResponseEntity<Object> eliminarEmpleadoPorId(int id) {
        try {
            empleadoRepository.deleteById(id);
            return new ResponseEntity<Object>("Se elimino corectamente al empleqado id: "+id,HttpStatus.NO_CONTENT);
        } catch (Exception err) {
            return new ResponseEntity<Object>("No se encontro al empleado con id: "+id,HttpStatus.NOT_FOUND);
        }
    }

    //metodo para modificar empleados por id
    //en caso de no encontrar el objeto devuelve null
    public ResponseEntity<Object> actualizarEmpleado(Integer id,EmpleadoModel empleadoActualizado) {
       return validarAcualizacionEmpleado(empleadoActualizado,id);
    }

    //Metodo para validar los atributos de la entidad empleado
    private ResponseEntity<Object> validarIngresoEmpleado(EmpleadoModel empleado) {
        //Valido la edad del empleado a ingresar
        if (esEdadValida(empleado.getFechaNacimiento())) {
            return new ResponseEntity<Object>("El empleado no puede ser menor de edad", HttpStatus.BAD_REQUEST);
        }
        //Valido el que el nombre no tenga caracteres especiales
        if (esFormatoNombreValido(empleado.getNombre())) {
            return new ResponseEntity<Object>("El nombre no puede tener caracteres especiales", HttpStatus.BAD_REQUEST);
            //Valido que el apellido no tenga caracteres especiales
        }
        if (esFormatoApellidoValido(empleado.getApellido())) {
            return new ResponseEntity<Object>("El apellido no puede tener caracteres especiales", HttpStatus.BAD_REQUEST);
            //Valido que la fecha de ingreso no sea posterior a la fecha del dia
        }
        if (esFechaIngresoValida(empleado.getFechaIngreso())) {
            return new ResponseEntity<Object>("La fecha de ingreso no puede ser posterior al dia de la fecha", HttpStatus.BAD_REQUEST);
            //Valido que la fecha de nacimiento no sea posterior al dia de la fecha
        }
        if (esFechaNacimientoValida(empleado.getFechaNacimiento())) {
            return new ResponseEntity<Object>("La fecha de nacimiento no puede ser posterior al dia de la fecha", HttpStatus.BAD_REQUEST);
            //Valido el formato del email
        }
        if (esFormatoEmailValido(empleado.getEmail())) {
            return new ResponseEntity<Object>("El email no es valido", HttpStatus.BAD_REQUEST);
            //Valido que el email no este en la base de dato
        }
        if (emailDuplicado(empleado.getEmail())) {
            return new ResponseEntity<Object>("El email ya esta en uso", HttpStatus.CONFLICT);
            //Valido que el dni no este en la base de datos
        }
        if (nroDocumentoDuplicado(empleado.getNroDocumento())) {
            return new ResponseEntity<Object>("El documento ya esta en uso", HttpStatus.CONFLICT);
        }
        //Guarda el empleado en la base de datos y lo muestra en pantalla
        return new ResponseEntity<Object>(empleadoRepository.save(empleado), HttpStatus.CREATED);


    }






    //Metodo para validar los datos de las actualizaciones a los empleados ya creados

    private ResponseEntity<Object> validarAcualizacionEmpleado(EmpleadoModel empleado,Integer id) {
        //Valido la edad del empleado a ingresar
        if (esEdadValida(empleado.getFechaNacimiento())) {
            return new ResponseEntity<Object>("El empleado no puede ser menor de edad", HttpStatus.BAD_REQUEST);
        }
        //Valido el que el nombre no tenga caracteres especiales
        if (esFormatoNombreValido(empleado.getNombre())) {
            return new ResponseEntity<Object>("El nombre no puede tener caracteres especiales", HttpStatus.BAD_REQUEST);
            //Valido que el apellido no tenga caracteres especiales
        }
        if (esFormatoApellidoValido(empleado.getApellido())) {
            return new ResponseEntity<Object>("El apellido no puede tener caracteres especiales", HttpStatus.BAD_REQUEST);
            //Valido que la fecha de ingreso no sea posterior a la fecha del dia
        }
        if (esFechaIngresoValida(empleado.getFechaIngreso())) {
            return new ResponseEntity<Object>("La fecha de ingreso no puede ser posterior al dia de la fecha", HttpStatus.BAD_REQUEST);
            //Valido que la fecha de nacimiento no sea posterior al dia de la fecha
        }
        if (esFechaNacimientoValida(empleado.getFechaNacimiento())) {
            return new ResponseEntity<Object>("La fecha de nacimiento no puede ser posterior al dia de la fecha", HttpStatus.BAD_REQUEST);
            //Valido el formato del email
        }
        if (esFormatoEmailValido(empleado.getEmail())) {
            return new ResponseEntity<Object>("El email no es valido", HttpStatus.BAD_REQUEST);

        }
            //valida la existencia deñ id el empleado a modificar
        if (!encontrarEmpleadoYModificar(empleado, id)) {
            return new ResponseEntity<Object>("El empleado no se encuentra en la base de datos", HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<Object>("El empleado de id: " + id + " fue modificado con exito", HttpStatus.OK);

    }






    //Metodo para validar la edad
    private boolean esEdadValida(String fechaNacimiento) {
        // Convierte la fecha de nacimiento en un objeto LocalDate
        LocalDate fechaNacimientoDate = LocalDate.parse(fechaNacimiento);
        // Obtiene la fecha actual
        LocalDate fechaActual = LocalDate.now();
        // Calcula el período entre la fecha de nacimiento y la fecha actual
        Period periodo = Period.between(fechaNacimientoDate, fechaActual);
        // Obtiene la cantidad de años del período
        int edad = periodo.getYears();
        return !(edad >= 18);
    }

    //Metodo para validar la fecha de ingreso
    private boolean esFechaIngresoValida(String fechaIngreso) {
        // Convierte la fecha de ingreso en un objeto LocalDate
        LocalDate fechaIngresoDate = LocalDate.parse(fechaIngreso);
        // Obtiene la fecha actual
        LocalDate fechaActual = LocalDate.now();
        // Compara la fecha de ingreso con la fecha actual
        // Si la fecha de ingreso es posterior, retorna true; de lo contrario, retorna false.
        return fechaIngresoDate.isAfter(fechaActual);
    }

    private boolean esFechaNacimientoValida(String fechaNacimiento) {
        // Convierte la fecha de ingreso en un objeto LocalDate
        LocalDate fechaIngresoDate = LocalDate.parse(fechaNacimiento);
        // Obtiene la fecha actual
        LocalDate fechaActual = LocalDate.now();
        // Compara la fecha de ingreso con la fecha actual
        // Si la fecha de ingreso es posterior, retorna true; de lo contrario, retorna false.
        return fechaIngresoDate.isAfter(fechaActual);
    }

    //Metodo para validar el email
    private boolean esFormatoEmailValido(String email) {
        // Define una expresión regular para validar el formato de un email
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        // Compila la expresión regular en un patrón
        Pattern pattern = Pattern.compile(regex);
        // Compara el email con el patrón
        return !pattern.matcher(email).matches();
    }
    //Metodo para validar el nombre
    private boolean esFormatoNombreValido(String nombre) {
        //Define una exprecion regular para validar el formato de un nombre
        String regex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
        //Compila la exprecion regular en un patron
        Pattern pattern = Pattern.compile(regex);
        //Compara el nombre con el patron
        return !pattern.matcher(nombre).matches();
    }
    //Metodo para validar el apellido
    private boolean esFormatoApellidoValido(String apellido) {
        //Define una exprecion regular para validar el formato de un apellido
        String regex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
        //Compila la exprecion regular en un patron
        Pattern pattern = Pattern.compile(regex);
        //Compara el apellido con el patron
        return !pattern.matcher(apellido).matches();
    }


    //Metodo para encontrar emails duplicados
    //TODO modificar, si el email no coincide con el del empleado a modificar, compara el email en la base de datos para buscar coincidencias
    private boolean emailDuplicado(String email) {
        Optional<EmpleadoModel> usuarioExistente = empleadoRepository.findByEmail(email);
        return usuarioExistente.isPresent();
    }


    //Metodo para encontrar documentos duplicados
    //TODO modificar, si el dni no coincide con el del empeado a modificar, buscar coincidencias en la bd
    private boolean nroDocumentoDuplicado(Integer nroDocumento) {
        Optional<EmpleadoModel> usuarioExistente = empleadoRepository.findByNroDocumento(nroDocumento);
        return usuarioExistente.isPresent();
    }
    //Metodo para validar la existencia y modificar el empleado
    private boolean encontrarEmpleadoYModificar(EmpleadoModel empleadoActualizado, Integer id){
        EmpleadoModel empleadoExistente = empleadoRepository.findById(id).orElse(null);
        if (empleadoExistente != null) {
            // Actualiza los campos del empleado existente con los valores proporcionados
            empleadoExistente.setNroDocumento(empleadoActualizado.getNroDocumento());
            empleadoExistente.setNombre(empleadoActualizado.getNombre());
            empleadoExistente.setApellido(empleadoActualizado.getApellido());
            empleadoExistente.setEmail(empleadoActualizado.getEmail());
            empleadoExistente.setFechaNacimiento(empleadoActualizado.getFechaNacimiento());
            empleadoExistente.setFechaIngreso(empleadoActualizado.getFechaIngreso());
            // Guarda el empleado actualizado en la base de datos
            empleadoRepository.save(empleadoExistente);
            return true;
        }

        return false;
    }
    //Metodo que devuelva la concatenacion del nombre y el apellido
    public String getNombreApellido (Integer id){
        String nombre;
        String apellido;
        nombre = this.empleadoRepository.findById(id).get().getNombre();
        apellido = this.empleadoRepository.findById(id).get().getApellido();
        return nombre+" "+apellido;

    }


    public Integer getNroDocumento(Integer nroIdEmpleado) {
        return this.empleadoRepository.findById(nroIdEmpleado).get().getNroDocumento();
    }







}



