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

    @OneToMany(mappedBy = "jornada")
    private List<Tiempo> tiempos;

    @NotNull
    private Date fechaInicio;

    @NotNull
    private Date fechaFin;


}
