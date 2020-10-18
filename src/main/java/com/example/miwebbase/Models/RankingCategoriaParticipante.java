package com.example.miwebbase.Models;

import com.example.miwebbase.Entities.Competicion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RankingCategoriaParticipante implements Cloneable {


    String nombreParticipante;
    Integer puntuacion_total;
    int posicion;
    @Setter(AccessLevel.NONE)
    List<PuntuacionIndividual> puntuacionesIndividuales;
    boolean clasificado;



    public RankingCategoriaParticipante(String nombreParticipante, Long puntuacion_total) {
        this.nombreParticipante = nombreParticipante;
        this.puntuacion_total = Math.toIntExact(puntuacion_total);
    }

    public RankingCategoriaParticipante clone(){

        return new RankingCategoriaParticipante(this.nombreParticipante, this.puntuacion_total, this.posicion, puntuacionesIndividuales == null ? null : new ArrayList<>(puntuacionesIndividuales), this.clasificado);

    }

    public String getPuntuacionIndividual(int i) {

        if (puntuacionesIndividuales.size()-1 >= i && puntuacionesIndividuales.get(i).isParticipado()){
            return puntuacionesIndividuales.get(i).getPuntuacion_jornada()+"";
        } else {
            return "-";
        }
    }

    public void setPuntuacionesIndividuales(List<PuntuacionIndividual> puntuacionesIndividuales, Competicion competicion){

        List<PuntuacionIndividual> puntuacionesOrdenadas = new ArrayList<>();
        for (int i = 1; i <= competicion.getNumJornadas(); i++){
            final int ii = i;
            puntuacionesOrdenadas.add(puntuacionesIndividuales.stream().filter(p -> p.jornada == ii).findFirst()
                    .orElseGet(() -> new PuntuacionIndividual(this.nombreParticipante, ii, 0, false)));

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

        return r.getNombreParticipante().equals(this.getNombreParticipante());
    }


}