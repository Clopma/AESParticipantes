package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Keys.KeyInscripcion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
public class Inscripcion {

    @Id
    @ManyToOne
    private Evento evento;

    @Id
    @ManyToOne
    private Participante participante;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date fechaInscripcion;


}
