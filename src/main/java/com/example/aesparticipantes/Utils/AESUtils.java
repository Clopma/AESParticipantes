package com.example.aesparticipantes.Utils;

import com.example.aesparticipantes.Entities.Clasificado;
import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Models.Posicion;
import com.example.aesparticipantes.repositories.ClasificadoRepository;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AESUtils {


    public static final String COOKIE_TOKEN_TEMPORAL = "TOTE";
    public static final String COOKIE_NOMBRE_PARTICIPANTE = "NOPA";
    public static final String COOKIE_WCA_ID = "WCID";
    public static final String COOKIE_NOMBRE_WCA = "WCNO";
    public static final String COOKIE_TIPO_USUARIO = "TIUS";



    public static final String MENSAJE_ERROR = "Ha habido un error con la autenticación WCA, por favor, contacta un administrador y enséñale este mensaje: ";



    public enum TiposUsuarios {
        NV, // NO VINCULADO
        NC, // NO CONFIRMADO
        V; // VINCULADO
    }

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

    public static String getPosicionFinal(List<Clasificado.NombreRonda> clasificaciones) {

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

        return null;
    }

    public static TiposUsuarios getTipoUsuario(Participante participante) {

        if (participante == null) {
            return TiposUsuarios.NV;
        } else {
           return participante.isConfirmado() ? TiposUsuarios.V : TiposUsuarios.NC;
        }
    }

    public static String encodeURL(String str) {

        try {
            return URLEncoder.encode(str, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            //Nunca ocurrirá
            e.printStackTrace();
            return "Imposible";
        }
    }

    public static void setMedallas(List<Posicion> posiciones, ClasificadoRepository clasificadoRepository) {
        posiciones.stream().filter(Posicion::isClasificado).collect(Collectors.toList()).forEach(p -> p.setMedalla(clasificadoRepository));
    }



}
