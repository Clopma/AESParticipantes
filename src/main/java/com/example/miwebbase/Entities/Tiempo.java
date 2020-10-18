package com.example.miwebbase.Entities;

import com.example.miwebbase.Entities.Keys.KeyTiempo;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "Tiempos")
@Entity
@IdClass(KeyTiempo.class)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tiempo {

    @Id
    @ManyToOne
    private Competicion competicion;

    @Id
    @ManyToOne
    private Categoria categoria;

    @Id
    private int jornada;

    @Id
    @ManyToOne
    private Participante participante;

    @NotNull
    private int posicion;

    @NotNull
    private int puntosTiempo;

    @NotNull
    private int puntosBonus;

    @Transient
    private int puntosTotales;

    public int getPuntosTotales(){
        return puntosTiempo + puntosBonus;
    }

    @NotNull
    private double tiempo1;

    @NotNull
    private double tiempo2;

    @NotNull
    private double tiempo3;

    @NotNull
    private double tiempo4;

    @NotNull
    private double tiempo5;

    @Transient
    private double media;

    private String solucion;

    @Column(length = 2000)
    private String explicacion;

}
