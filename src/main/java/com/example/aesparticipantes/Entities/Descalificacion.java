package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Keys.KeyDescalificacion;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "Descalificaciones")
@IdClass(KeyDescalificacion.class)
@Entity
@Getter
@Setter
public class Descalificacion {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Evento evento;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Participante participante;



}
