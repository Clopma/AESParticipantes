package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Keys.KeyDescalificacion;
import lombok.Getter;

import javax.persistence.*;

@Table(name = "Descalificaciones")
@IdClass(KeyDescalificacion.class)
@Entity
@Getter
public class Descalificacion {

    @Id
    @ManyToOne
    private Categoria categoria;

    @Id
    @ManyToOne
    private Participante participante;

    @Id
    @ManyToOne
    private Competicion competicion;


}
