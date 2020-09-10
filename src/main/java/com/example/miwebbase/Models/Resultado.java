package com.example.miwebbase.Models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Resultado {

    List<Categoria> categoriasParticipadas;

    @Setter
    @Getter
    public static class Categoria {

        String nombre;
        int numTiempos;
        List<Jornada> jornadasParticipadas;

        @Setter
        @Getter
        public static class Jornada {

            int numJornada;
            int posicion;
            String tiempo1;
            String tiempo2;
            String tiempo3;
            String tiempo4;
            String tiempo5;
            String single;
            String media;
            String solucion;
            String explicacion;
            int puntos;


        }
    }
}



