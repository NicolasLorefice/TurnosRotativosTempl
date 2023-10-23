package com.neoris.turnosrotativos.services;
import com.neoris.turnosrotativos.entities.JornadaModel;
import com.neoris.turnosrotativos.entities.dtos.JornadaDTO;
import com.neoris.turnosrotativos.repositories.JornadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JornadaService {
    @Autowired
    EmpleadoService empleadoService;
    @Autowired
    ConceptoService conceptoService;
    @Autowired
    JornadaRepository jornadaRepository;

    public JornadaModel guardarJornada(JornadaModel jornada) {
        return jornadaRepository.save(jornada);
    }
    public List<JornadaModel> obtenerJornada() {
        return (List<JornadaModel>) jornadaRepository.findAll();
    }
    //Metodo para obtener jornadas por documento
    public List<JornadaModel> obtenerJornadaPorDocumento(Integer nroDocumento){
        return (List<JornadaModel>) jornadaRepository.findByNroDocumento(nroDocumento);
    }
    //Metodo para obtener jornadas por fehca

    //Metodo para obtener jornadas por fecha y numero de documento
    public List<JornadaModel> obtenerJornadaPorFechaYNroDocumento(String fecha,Integer nroDocumento){
        return (List<JornadaModel>) jornadaRepository.findByNroDocumentoAndFechaDeJornada(nroDocumento, fecha);
    }
    //Metodo para crear la jornada
    public ResponseEntity<Object> crearJornada(@Valid JornadaDTO jornadaDTO) {

        if(!(empleadoService.obtenerPorId(jornadaDTO.getNroIdEmpleado()).isPresent())) {
            return new ResponseEntity<>("El id no corresponde a un empleado",HttpStatus.NOT_FOUND);
        }
        if(!(conceptoService.obtenerConceptoPorID(jornadaDTO.getNroConcepto()).isPresent())){
            return new ResponseEntity<>("El concepto no corresponde a uno valido",HttpStatus.NOT_FOUND);
        }
        if(!(hsValidas(jornadaDTO).getStatusCode().equals(HttpStatus.OK))){
            return (hsValidas(jornadaDTO));
        }
        if(!(validarSuperpocicionDeTurnos(jornadaDTO).getStatusCode().equals(HttpStatus.OK))){
            return (validarSuperpocicionDeTurnos(jornadaDTO));
        }
        if(!(validarJornadaMultiple(jornadaDTO)).getStatusCode().equals(HttpStatus.OK)){
            return (validarJornadaMultiple(jornadaDTO));
        }
        if(!(validarHorasPorJornada(jornadaDTO)).getStatusCode().equals(HttpStatus.OK)){
           return (validarHorasPorJornada(jornadaDTO));
        }
        if(!(validarMaximosTurnosSemanales(jornadaDTO)).getStatusCode().equals(HttpStatus.OK)){
            return (validarMaximosTurnosSemanales(jornadaDTO));
        }
        JornadaModel jornada = new JornadaModel();
        jornada.setNombreApellido(empleadoService.getNombreApellido(jornadaDTO.getNroIdEmpleado()));
        jornada.setNroConcepto(jornadaDTO.getNroConcepto());
        jornada.setNroDocumento(empleadoService.getNroDocumento(jornadaDTO.getNroIdEmpleado()));
        jornada.setHsTrabajadas(jornadaDTO.getHsTrabajadas());
        jornada.setFechaDeJornada(jornadaDTO.getFechaDeJornada());
        jornadaRepository.save(jornada);
        return new ResponseEntity<>(jornada, HttpStatus.CREATED);
    }



    //Metodo para validar el concepto
    private boolean esNroConceptoValido(JornadaDTO jornadaDTO) {
        return conceptoService.obtenerConceptoPorID(jornadaDTO.getNroConcepto()).isPresent();
    }
    //Metodo para validar las horas ingresadas
    private ResponseEntity<Object> hsValidas(JornadaDTO jornadaDTO) {
        //Valido que si es un dia libre no pueda tener horas asignadas
        if((jornadaDTO.getHsTrabajadas()!=null) &&
            conceptoService.obtenerConceptoPorID(jornadaDTO.getNroConcepto()).get().getNombre().equals("Dia Libre")){
            return new ResponseEntity<>("El concepto ingresado no requiere el ingreso de hs trabajadas "
                    ,HttpStatus.BAD_REQUEST);
        }//Valido que si es turno norma o extra este tenga horas asignadas
        else if((conceptoService.obtenerConceptoPorID(jornadaDTO.getNroConcepto()).get().getNombre().equals("Turno Normal"))
                ||(conceptoService.obtenerConceptoPorID(jornadaDTO.getNroConcepto()).get().getNombre().equals("Turno Extra"))){

                if (jornadaDTO.getHsTrabajadas()==null){
                     return new ResponseEntity<>("Horas trabajadas es obligatorio para el concepto ingresado",
                            HttpStatus.BAD_REQUEST);
                             }
                //Valido que las horas ingresadas en los conceptos turno normal y turno extra esten dentro del rango de lo requerido
                    if (jornadaDTO.getHsTrabajadas() > conceptoService.obtenerHsMaximasPorId(jornadaDTO.getNroConcepto())
                            || jornadaDTO.getHsTrabajadas() < conceptoService.obtenerHsMinimasPorId(jornadaDTO.getNroConcepto())) {
                            return new ResponseEntity<>("El rango de horas que se puede cargar para este concepto es de "
                                    +conceptoService.obtenerHsMinimasPorId(jornadaDTO.getNroConcepto()) +
                                " - "+ conceptoService.obtenerHsMaximasPorId(jornadaDTO.getNroConcepto()) +
                                ". Donde "+conceptoService.obtenerHsMinimasPorId(jornadaDTO.getNroConcepto()) +" = horas mínimas y "
                                    + conceptoService.obtenerHsMaximasPorId(jornadaDTO.getNroConcepto())+" = horas máximas."
                                , HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }    //Validacion de turno turnos y horas
    //Validacion de la superpocicion de los dias libres
    public ResponseEntity<Object> validarSuperpocicionDeTurnos(JornadaDTO jornadaDTO) {
        int contadorJornada=0;
        // Buscar jornadas con el mismo empleado y la misma fecha
        List<JornadaModel> jornadas = jornadaRepository.findByNroDocumentoAndFechaDeJornada(
                empleadoService.getNroDocumento(jornadaDTO.getNroIdEmpleado()),
                jornadaDTO.getFechaDeJornada());

        for (JornadaModel jornada : jornadas) {
            if(jornada.getNroConcepto()==3){
                contadorJornada++;
            }
        }
        if(contadorJornada>0){
            return new ResponseEntity<>("El empleado ingresado cuenta con un día libre en esa fecha."
                    ,HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
    //valida el ingreso de dos jornadas distintas el mismo dia
    public ResponseEntity<Object>  validarJornadaMultiple(JornadaDTO jornadaDTO){
        List<JornadaModel> jornadas = jornadaRepository.findByNroDocumentoAndFechaDeJornada(
                empleadoService.getNroDocumento(jornadaDTO.getNroIdEmpleado()),
                 jornadaDTO.getFechaDeJornada());
        Integer jornadaNormal=0;
        Integer jornadaExtra=0;
        for(JornadaModel jornada : jornadas){
            if(jornada.getNroConcepto()==1){
                jornadaNormal++;
            }
            if(jornada.getNroConcepto()==2){
                jornadaExtra++;
            }
        }
        if(jornadaDTO.getNroConcepto()==1 && jornadaNormal==1){
            return new ResponseEntity<>("El empleado ya tiene registrada una jornada con ese concepto en esa fecha",
                    HttpStatus.BAD_REQUEST);
        }
        if(jornadaDTO.getNroConcepto()==2 && jornadaExtra==1){
          return  new ResponseEntity<>("El empleado ya tiene registrada una jornada con ese concepto en esa fecha",
                    HttpStatus.BAD_REQUEST);
        }
        if(jornadaDTO.getNroConcepto()==3 && (jornadaNormal==1 || jornadaExtra==1)){
            return new ResponseEntity<>("El empleado no puede cargar Dia Libre si cargo un turno previamente para la fecha ingresada.",HttpStatus.BAD_REQUEST);
        }
        if(!(validarHorasPorSemana(jornadaDTO)).equals(HttpStatus.OK)){
            return (validarHorasPorSemana(jornadaDTO));
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
    //Valida segun las reglas de negocio las horas que el empleado puede trabajar por jornada
    private ResponseEntity<Object> validarHorasPorJornada(JornadaDTO jornadaDTO){
        List<JornadaModel> jornadas = jornadaRepository.findByNroDocumentoAndFechaDeJornada(
                empleadoService.getNroDocumento(jornadaDTO.getNroIdEmpleado()),
                jornadaDTO.getFechaDeJornada());
        Integer horasMaximasPorJornada=12;
        Integer horasActualesDeJornada=0;
        for(JornadaModel jornada : jornadas){
            if(jornada.getNroConcepto()!=3) {
                horasActualesDeJornada += jornada.getHsTrabajadas();

                if((horasActualesDeJornada+jornadaDTO.getHsTrabajadas())>horasMaximasPorJornada){
                    return new ResponseEntity<>("El empleado no puede cargar más de 12 horas trabajadas en un día."
                            ,HttpStatus.BAD_REQUEST);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    //Valida que la sumatoria de las horas por dia no supere un maximo semanal
    private ResponseEntity<Object> validarHorasPorSemana(JornadaDTO jornadaDTO){
        List<JornadaModel> jornadas = jornadaRepository.findByNroDocumento
                (empleadoService.getNroDocumento(jornadaDTO.getNroIdEmpleado()));
        Integer hsMaximasPorSemana = 48;
        Integer hsActualesDeSemana=0;
        if(jornadaDTO.getNroConcepto()!=3){
        for(JornadaModel jornada : jornadas){
            if(jornadaDTO.getNroConcepto()!=3 && jornada.getHsTrabajadas()!=null) {
                hsActualesDeSemana = (hsActualesDeSemana + jornada.getHsTrabajadas());
                if (hsActualesDeSemana + jornadaDTO.getHsTrabajadas() > hsMaximasPorSemana) {
                    return new ResponseEntity<>("El empleado ingresado supera las 48 horas semanales.", HttpStatus.BAD_REQUEST);
                }
            }
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
    //Valida la cantidad maxima de tipos de turnos que puede tomar un empleado por semana
    private ResponseEntity<Object> validarMaximosTurnosSemanales(JornadaDTO jornadaDTO){
        List<JornadaModel> jornadas = jornadaRepository.findByNroDocumento
                                    (empleadoService.getNroDocumento(jornadaDTO.getNroIdEmpleado()));
        Integer maximoTurnosExtra=3;
        Integer maximoTurnosNormales=5;
        Integer maximoDiasLibres=2;
        Integer auxMaximoTurnosExtra=0;
        Integer auxMaximoTurnosNormales=0;
        Integer auxMaximoDiasLibres=0;
        for(JornadaModel jornada : jornadas){
            if(jornada.getNroConcepto()==1){
                auxMaximoTurnosNormales++;
            }
            if(jornada.getNroConcepto()==2){
                auxMaximoTurnosExtra++;
            }
            if(jornada.getNroConcepto()==3){
                auxMaximoDiasLibres++;
            }
        }
        // Verificar si se pueden agregar más días de la misma categoría
        if (jornadaDTO.getNroConcepto() == 1 && auxMaximoTurnosNormales >= maximoTurnosNormales) {
            return new ResponseEntity<>("El empleado no puede cargar más de 5 Turnos Normales en la semana", HttpStatus.BAD_REQUEST);
        } else if (jornadaDTO.getNroConcepto() == 2 && auxMaximoTurnosExtra >= maximoTurnosExtra) {
            return new ResponseEntity<>("El empleado no puede cargar más de 3 Turnos Extra en la semana", HttpStatus.BAD_REQUEST);
        } else if (jornadaDTO.getNroConcepto() == 3 && auxMaximoDiasLibres >= maximoDiasLibres) {
            return new ResponseEntity<>("El empleado no puede cargar más de 2 Días Libres en la semana", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    public List<JornadaModel> obtenerJornadaPorFecha(String fechaDeJornada) {
        return jornadaRepository.findByFechaDeJornada(fechaDeJornada);
    }
}






