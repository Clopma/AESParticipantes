package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Keys.KeyJornada;
import lombok.*;

import javax.persistence.*;
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
    private Evento evento;

    @Id
    private int numeroJornada;

    @OneToMany(mappedBy = "jornada")
    private List<Tiempo> tiempos;

    private Date fechaInicio;

    private Date fechaFin;


}
