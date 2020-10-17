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
public class RankingCategoriaParticipante implements Cloneable {


    String nombre;
    Integer puntuacion_total;
    int posicion;
    List<PuntuacionIndividual> puntuacionesIndividuales;
    boolean clasificado;



    public RankingCategoriaParticipante(String nombre, Long puntuacion_total) {
        this.nombre = nombre;
        this.puntuacion_total = Math.toIntExact(puntuacion_total);
    }

    public RankingCategoriaParticipante clone(){

        return new RankingCategoriaParticipante(this.nombre, this.puntuacion_total, this.posicion, puntuacionesIndividuales == null ? null : new ArrayList<>(puntuacionesIndividuales), this.clasificado);

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
        for (int i = 1; i <= AESUtils.JORNADAS_CAMPEONATO; i++){
            final int ii = i;
            puntuacionesOrdenadas.add(puntuacionesIndividuales.stream().filter(p -> p.jornada == ii).findFirst()
                    .orElseGet(() -> new PuntuacionIndividual(this.nombre, ii, 0, false)));

        }

        this.puntuacionesIndividuales = puntuacionesOrdenadas;

    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof RankingCategoriaParticipante)) {
            return false;
        }

        RankingCategoriaParticipante r = (RankingCategoriaParticipante) o;

        return r.getNombre().equals(this.getNombre());
    }


}