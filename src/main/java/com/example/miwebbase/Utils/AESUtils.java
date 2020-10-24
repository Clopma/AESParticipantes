package com.example.miwebbase.Utils;

import com.example.miwebbase.Entities.Clasificado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AESUtils {

    public static String DNF = "DNF";

    //devuelve un double[]{single, media, peor}
    public static double[] getTiemposCalculados(List<Double> tiempos, int numTiempos) {

        tiempos = tiempos.stream().filter(t -> t > 0).collect(Collectors.toList());

        double mejorTiempo = tiempos.stream().mapToDouble(d -> d).min().orElse(0);

        Collections.sort(tiempos);
        double peorTiempo = 0;
        double media = 0;
        if (numTiempos == 5) {

            if (tiempos.size() == 4) {
                tiempos.remove(0);
                media = tiempos.stream().mapToDouble(d -> d).average().getAsDouble();
            } else if (tiempos.size() == 5) {
                tiempos.remove(0);
                peorTiempo = tiempos.get(4 - 1);
                tiempos.remove(4 - 1);
                media = tiempos.stream().mapToDouble(d -> d).average().getAsDouble();
            }
        } else if (numTiempos == 3) {
            if (tiempos.size() == 3) {
                media = tiempos.stream().mapToDouble(d -> d).average().getAsDouble();
                peorTiempo = tiempos.get(2);
            }
        }

        return new double[]{mejorTiempo, media, peorTiempo};
    }

    public static String formatTime(double time) {

        try {
            String doubleAsText = String.valueOf(round(time, 2));
            int decimal = Integer.parseInt(doubleAsText.split("\\.")[0]);
            int mins = decimal / 60;
            int secs = decimal % 60;
            String fractional = doubleAsText.split("\\.")[1];
            if (fractional.length() == 1) {
                fractional += "0";
            }
            if (mins > 0) {
                return String.format("%01d:%02d.", mins, secs) + fractional;
            } else {
                return String.format("%01d.", secs) + fractional;
            }
        } catch (Exception e) {
            return "CCA";
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String getPosicionFinal(List<Clasificado.NombreRonda> clasificaciones){

        if (clasificaciones.size() > 0) {
            List<Clasificado.NombreRonda> order = Arrays.asList(Clasificado.NombreRonda.MEDALLA_BRONCE, Clasificado.NombreRonda.MEDALLA_PLATA, Clasificado.NombreRonda.MEDALLA_ORO);

            Clasificado.NombreRonda max = clasificaciones.get(0);

            for (Clasificado.NombreRonda c : clasificaciones) {
                if (order.indexOf(max) < order.indexOf(c)) {
                    max = c;
                }
            }

            return max.name();

        }

        return "OTRO";
    }

}
