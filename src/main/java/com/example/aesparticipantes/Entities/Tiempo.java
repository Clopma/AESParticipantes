package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Keys.KeyTiempo;
import com.example.aesparticipantes.Utils.AESUtils;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;

import static com.example.aesparticipantes.Utils.AESUtils.formatTime;

@Table(name = "Tiempos")
@Entity
@IdClass(KeyTiempo.class)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tiempo implements Comparable {

    @Id
    @ManyToOne
    private Jornada jornada;

    @Id
    @ManyToOne
    private Categoria categoria;

    @Id
    @ManyToOne
//    @Cascade({org.hibernate.annotations.CascadeType.PERSIST})
    private Participante participante;

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

    //TODO @NotNull
    private Date comienzo;

    //Si es null, ha comenzado pero no se ha enviado el tiempo final
    private Date envio;

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

    @Transient
    private int posicion;

    @Transient
    private int puntosTiempo;

    @Transient
    private int puntosBonus;

    @Transient
    private boolean oculto = false; //TODO: qué pasa si no lo inicicializo? no es por defecto false?

    public int getPuntosTotales() {
            return getJornada().isAcabada() ? puntosTiempo + puntosBonus : 0;
    }



    public void calcularDatos() {
        
        double[] singleMediaYpeor = AESUtils.getTiemposCalculados(Arrays.asList(tiempo1, tiempo2, tiempo3, tiempo4, tiempo5), categoria.getNumTiempos());

        single = singleMediaYpeor[0];
        media = singleMediaYpeor[1];

        tiempo1Str = tiempo1 == 0 ? AESUtils.DNF : formatTime(this.tiempo1);

        if (categoria.getNumTiempos() > 1) {

            tiempo2Str = tiempo2 == 0 ? AESUtils.DNF : formatTime(tiempo2);
            tiempo3Str = tiempo3 == 0 ? AESUtils.DNF : formatTime(tiempo3);
        }

        if (categoria.getNumTiempos() > 3) {

            tiempo4Str = tiempo4 == 0 ? AESUtils.DNF : formatTime(tiempo4);
            tiempo5Str = tiempo5 == 0 ? AESUtils.DNF : formatTime(tiempo5);
        }

        singleStr = single == 0 ? AESUtils.DNF : formatTime(single);
        mediaStr = media == 0 ? AESUtils.DNF : formatTime(media);

        //Si la media no es un DNF
        if (media > 0 && categoria.getNumTiempos() == 5) {

            if (tiempo1 == single) {
               tiempoDescartadoAbajo = 1;
            } else if (tiempo2 == single) {
                tiempoDescartadoAbajo = 2;
            } else if (tiempo3 == single) {
                tiempoDescartadoAbajo = 3;
            } else if (tiempo4 == single) {
                tiempoDescartadoAbajo = 4;
            } else if (tiempo5 == single) {
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

    @Override
    public int compareTo(Object o) {

        Tiempo t = (Tiempo) o;
        if (this.getMedia() == 0){
           if(t.getMedia() == 0) return 0;
           return 1;
        } else if (t.getMedia() == 0){
            return -1;
        }
        return Double.compare(this.getMedia(), t.getMedia());
    }
}
