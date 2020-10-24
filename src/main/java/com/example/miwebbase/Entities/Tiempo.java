package com.example.miwebbase.Entities;

import com.example.miwebbase.Entities.Keys.KeyTiempo;
import com.example.miwebbase.Utils.AESUtils;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

import static com.example.miwebbase.Utils.AESUtils.formatTime;

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
    private int jornada;

    @Id
    @ManyToOne
    private Competicion competicion;

    @Id
    @ManyToOne
    private Categoria categoria;

    @Id
    @ManyToOne
    private Participante participante;

    @NotNull
    private int posicion;

    @NotNull
    private int puntosTiempo;

    @NotNull
    private int puntosBonus;

    public int getPuntosTotales() {
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

    private String solucion;

    @Column(length = 2000)
    private String explicacion;

    @Transient
    private double media;

    @Transient double single;

    @Transient
    private String mediaStr;

    @Transient
    String singleStr;

    @Transient
    int tiempoDescartadoArriba;

    @Transient
    int tiempoDescartadoAbajo;

    @Transient
    private String tiempo1Str;

    @Transient
    private String tiempo2Str;

    @Transient
    private String tiempo3Str;

    @Transient
    private String tiempo4Str;

    @Transient
    private String tiempo5Str;


    public void calcularDatos() {
        
        double[] singleMediaYpeor = AESUtils.getTiemposCalculados(Arrays.asList(tiempo1, tiempo2, tiempo3, tiempo4, tiempo5), categoria.getNumTiempos());

        tiempo1Str = tiempo1 == 0 ? AESUtils.DNF : formatTime(this.tiempo1);

        if (categoria.getNumTiempos() > 1) {

            tiempo2Str = tiempo2 == 0 ? AESUtils.DNF : formatTime(tiempo2);
            tiempo3Str = tiempo3 == 0 ? AESUtils.DNF : formatTime(tiempo3);
        }

        if (categoria.getNumTiempos() > 3) {

            tiempo4Str = tiempo4 == 0 ? AESUtils.DNF : formatTime(tiempo4);
            tiempo5Str = tiempo5 == 0 ? AESUtils.DNF : formatTime(tiempo5);
        }

        singleStr = singleMediaYpeor[0] == 0 ? AESUtils.DNF : formatTime(singleMediaYpeor[0]);
        mediaStr = singleMediaYpeor[1] == 0 ? AESUtils.DNF : formatTime(singleMediaYpeor[1]);

        //Si la media no es un DNF
        if (singleMediaYpeor[1] > 0 && categoria.getNumTiempos() == 5) {

            if (tiempo1 == singleMediaYpeor[0]) {
               tiempoDescartadoAbajo = 1;
            } else if (tiempo2 == singleMediaYpeor[0]) {
                tiempoDescartadoAbajo = 2;
            } else if (tiempo3 == singleMediaYpeor[0]) {
                tiempoDescartadoAbajo = 3;
            } else if (tiempo4 == singleMediaYpeor[0]) {
                tiempoDescartadoAbajo = 4;
            } else if (tiempo5 == singleMediaYpeor[0]) {
                tiempoDescartadoAbajo = 5;
            }

            if (tiempo1 == singleMediaYpeor[2]) {
                tiempoDescartadoArriba = 1;
            } else if (tiempo2 == singleMediaYpeor[2]) {
                tiempoDescartadoArriba = 2;
            } else if (tiempo3 == singleMediaYpeor[2]) {
                tiempoDescartadoArriba = 3;
            } else if (tiempo4 == singleMediaYpeor[2]) {
                tiempoDescartadoArriba = 4;
            } else if (tiempo5 == singleMediaYpeor[2]) {
                tiempoDescartadoArriba = 5;
            }

        }

    }
}
