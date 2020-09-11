package com.example.miwebbase.Utils;

import com.example.miwebbase.Entities.Categoria;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RubikUtils {

    public static String DNF = "DNF";

    public static double[] getTiemposCalculados(List<Double> tiempos, Categoria categoria) {

        tiempos = tiempos.stream().filter(t -> t > 0).collect(Collectors.toList());

        double mejorTiempo = tiempos.stream().filter(i -> i > 0).mapToDouble(d -> d).min().orElse(0);

        Collections.sort(tiempos);

        if (categoria.getNumTiempos() == 5) {

            if (tiempos.size() == 4) {
                tiempos.remove(0);
                tiempos.remove(0);
            } else if (tiempos.size() == 5) {
                tiempos.remove(0);
                tiempos.remove(4 - 1);
            }
        }

        // tiempos.size() siempre = 3
        return new double[] {mejorTiempo, tiempos.size() < 3 ? 0 : tiempos.stream().mapToDouble(d -> d).average().getAsDouble()};
    }




}
