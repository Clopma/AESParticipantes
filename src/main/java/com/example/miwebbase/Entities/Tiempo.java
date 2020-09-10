package com.example.miwebbase.Entities;

import lombok.*;
import org.hibernate.annotations.GenerationTime;

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

    public static final String PARTICIPANTE = "participante";
    @Id
    @ManyToOne()
    private Participante participante;

    public static final String POSICION = "posicion";
    @Column(name = POSICION)
    @NotNull
    private int posicion;

    public static final String PUNTOS_TIEMPO = "puntosTiempo";
    @Column(name = PUNTOS_TIEMPO)
    @NotNull
    private int puntosTiempo;

    public static final String PUNTOS_BONUS = "puntosBonus";
    @Column(name = PUNTOS_BONUS)
    @NotNull
    private int puntosBonus;

    @Transient
    private int puntosTotales;
    public int getPuntosTotales(){
        return puntosTiempo + puntosBonus;
    }

    public static final String TIEMPO1 = "tiempo1";
    @Column(name = TIEMPO1)
    @NotNull
    private double tiempo1;

    public static final String TIEMPO2 = "tiempo2";
    @Column(name = TIEMPO2)
    @NotNull
    private double tiempo2;

    public static final String TIEMPO3 = "tiempo3";
    @Column(name = TIEMPO3)
    @NotNull
    private double tiempo3;

    public static final String TIEMPO4 = "tiempo4";
    @Column(name = TIEMPO4)
    @NotNull
    private double tiempo4;

    public static final String TIEMPO5 = "tiempo5";
    @Column(name = TIEMPO5)
    @NotNull
    private double tiempo5;

    public static final String SOLUCION = "solucion";
    @Column(name = SOLUCION)
    private String solucion;

    public static final String EXPLICACION = "explicacion";
    @Column(name = EXPLICACION, length = 1500)
    private String explicacion;

    @Override
    public int compareTo(Tiempo t){

        if (categoria.getPosicion() < t.getCategoria().getPosicion()) return -1;
        if (categoria.getPosicion() == t.getCategoria().getPosicion()) return 0;
        return 1;


    }

}
