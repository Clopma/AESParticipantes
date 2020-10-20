package com.example.miwebbase.Entities;

import com.example.miwebbase.Entities.Keys.KeyInscripcion;
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
    private Categoria categoria;

    @Id
    @ManyToOne
    private Participante participante;

    @Id
    @ManyToOne
    private Competicion competicion;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date fechaInscripcion;


}
