package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Keys.KeyInscripcion;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Table(name = "Inscripciones")
@IdClass(KeyInscripcion.class)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Inscripcion {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Evento evento;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Participante participante;

    //TODO ¿Se puede linkar mediante jpa de alguna forma Tiempo aquí?

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date fechaInscripcion;



}
