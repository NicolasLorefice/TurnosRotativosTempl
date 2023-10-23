package com.neoris.turnosrotativos.entities.dtos;

import lombok.Getter;
import lombok.Setter;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class JornadaDTO {

    private Integer id;
    @NotNull(message = "El capo Numero de id de empleado es obligatorio")
    private Integer nroIdEmpleado;
    @NotNull(message = "El campo Numero de concepto es obligatorio")
    private Integer nroConcepto;
    private Integer nroDocumento;
    private String nombreApellido;
    @NotBlank(message = "El campo Fecja de jornada es obligatorio")
    private String fechaDeJornada;
    private Integer hsTrabajadas;

    // Constructor por si es necesario
    public JornadaDTO(Integer id, Integer nroIdEmpleado, Integer nroConcepto, Integer nroDocumento, String nombreApellido, String fechaDeJornada, String concepto, Integer hsTrabajadas) {
        this.id = id;
        this.nroIdEmpleado = nroIdEmpleado;
        this.nroConcepto = nroConcepto;
        this.nroDocumento = nroDocumento;
        this.nombreApellido = nombreApellido;
        this.fechaDeJornada = fechaDeJornada;
        this.hsTrabajadas = hsTrabajadas;
    }
}
