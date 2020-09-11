package com.example.miwebbase.Models;

import lombok.Getter;

@Getter
public class PuntuacionRanking {
    String nombre;
    int puntos_totales;

    public PuntuacionRanking(String nombre, Long puntos_totales) {
        this.nombre = nombre;
        this.puntos_totales  = Math.toIntExact(puntos_totales);;
    }

}