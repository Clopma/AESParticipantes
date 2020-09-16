package com.example.miwebbase.Entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = Tiempo.T_USUARIOS)
@Entity
@IdClass(KeyTiempo.class)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tiempo implements Comparable<Tiempo> {

    public static final String T_USUARIOS = "Tiempos";

    public static final String CATEGORIA = "categoria";
    @Id
    @ManyToOne
    private Categoria categoria;

    @Id
    private int jornada;

    @Id
    @ManyToOne()
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

    @Override
    public int compareTo(Tiempo t){

        if (categoria.getOrden() < t.getCategoria().getOrden()) return -1;
        if (categoria.getOrden() == t.getCategoria().getOrden()) return 0;
        return 1;

    }

}
