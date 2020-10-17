package com.example.miwebbase.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PuntuacionIndividual implements Cloneable {
    String nombre;
    int jornada;
    int puntuacion_jornada;
    boolean participado;



    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof PuntuacionIndividual)) {
            return false;
        }

        PuntuacionIndividual p = (PuntuacionIndividual) o;

        return p.getNombre().equals(this.getNombre());
    }



}