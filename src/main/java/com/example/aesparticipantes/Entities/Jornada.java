package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Keys.KeyJornada;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@Table(name = "Jornadas")
@IdClass(KeyJornada.class)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Jornada {

    @Id
    @ManyToOne
    private Competicion competicion;

    @Id
    private int numeroJornada;

    //TODO: Evita "failed to lazily initialize a collection of role: Jornada.tiempos, could not initialize proxy - no Session
    //Es terrible por Dios
    // Ocurre al acceder al ranking de una jornada y luego ir al ranking general de su categoría, si cargas directamente el ranking general no ocurre
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "jornada")
    private List<Tiempo> tiempos;

    @NotNull
    private Date fechaInicio;

    @NotNull
    private Date fechaFin;

    public boolean isAcabada(){
        return fechaFin.before(new Date());
    }



}
