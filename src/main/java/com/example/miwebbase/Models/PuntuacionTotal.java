package com.example.miwebbase.Models;

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
    List<PuntuacionIndividual> puntuaciones_indivduales;

    public PuntuacionTotal(String nombre, Long puntuacion_total) {
        this.nombre = nombre;
        this.puntuacion_total = Math.toIntExact(puntuacion_total);
    }

    public PuntuacionTotal clone(){

        return new PuntuacionTotal(this.nombre, this.puntuacion_total, this.posicion, puntuaciones_indivduales == null ? null : new ArrayList<>(puntuaciones_indivduales));

    }

    public String getPuntuacionIndividual(int i) {

       return puntuaciones_indivduales.size()-1 >= i ? puntuaciones_indivduales.get(i).getPuntuacion_jornada()+"" : "";
    }



}