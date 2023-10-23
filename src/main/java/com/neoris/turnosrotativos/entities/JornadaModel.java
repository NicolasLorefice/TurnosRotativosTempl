package com.neoris.turnosrotativos.entities;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@Entity(name = "jornada")
public class JornadaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer nroConcepto;
    Integer nroDocumento;
    String nombreApellido;
    String fechaDeJornada;
    Integer hsTrabajadas;
}
