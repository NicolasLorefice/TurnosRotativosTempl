package com.neoris.turnosrotativos.entities;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@Entity(name = "Empleados")
public class EmpleadoModel {
    //PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message="El documento es un campo obligatorio")
    private Integer nroDocumento;

    @NotBlank(message= "El nombre es un campo obligatorio")
    private String nombre;

    @NotBlank(message="El apellido es un campo obligatorio")
    private String apellido;

    @NotBlank(message="El email es un campo obligatorio")
    private String email;

    @NotBlank(message = "La fecha de nacimiento es un campo obligatorio")
    private String fechaNacimiento;

    @NotBlank(message = "La fecha de ingreso es un campo obligatorio")
    private String fechaIngreso;
    
    String fechaCreacion;

}
