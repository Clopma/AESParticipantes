package com.example.miwebbase.Models;

import com.example.miwebbase.Utils.AESUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PuntuacionTotal implements Cloneable {
    String nombre;
    Integer puntuacion_total;
    int posicion;
    List<PuntuacionIndividual> puntuacionesIndividuales;



    public PuntuacionTotal(String nombre, Long puntuacion_total) {
        this.nombre = nombre;
        this.puntuacion_total = Math.toIntExact(puntuacion_total);
    }

    public PuntuacionTotal clone(){

        return new PuntuacionTotal(this.nombre, this.puntuacion_total, this.posicion, puntuacionesIndividuales == null ? null : new ArrayList<>(puntuacionesIndividuales));

    }

    public String getPuntuacionIndividual(int i) {

        if (puntuacionesIndividuales.size()-1 >= i && puntuacionesIndividuales.get(i).isParticipado()){

            return puntuacionesIndividuales.get(i).getPuntuacion_jornada()+"";
        } else {
            return "-";
        }
    }

    public void setPuntuacionesIndividuales(List<PuntuacionIndividual> puntuacionesIndividuales){

        List<PuntuacionIndividual> puntuacionesOrdenadas = new ArrayList<>();
        for (int i = 1; i < AESUtils.JORNADAS_CAMPEONATO; i++){
            final int ii = i;
            puntuacionesOrdenadas.add(puntuacionesIndividuales.stream().filter(p -> p.jornada == ii).findFirst()
                    .orElseGet(() -> new PuntuacionIndividual(this.nombre, ii, 0, false)));

        }

        this.puntuacionesIndividuales = puntuacionesOrdenadas;

    }


}