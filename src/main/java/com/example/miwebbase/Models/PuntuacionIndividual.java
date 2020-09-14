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



}