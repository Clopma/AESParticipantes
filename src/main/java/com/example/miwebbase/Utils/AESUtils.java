package com.example.miwebbase.Utils;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Models.PuntuacionTotal;
import com.example.miwebbase.repositories.TiempoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AESUtils {

    public static String DNF = "DNF";
    public static final int JORNADAS_CAMPEONATO = 5;

    //devuelve un double[]{single, media}
    public static double[] getTiemposCalculados(List<Double> tiempos, Categoria categoria) {

        tiempos = tiempos.stream().filter(t -> t > 0).collect(Collectors.toList());

        double mejorTiempo = tiempos.stream().mapToDouble(d -> d).min().orElse(0);

        Collections.sort(tiempos);

        double media = 0;
        if (categoria.getNumTiempos() == 5) {

            if (tiempos.size() == 4) {
                tiempos.remove(0);
                media = tiempos.stream().mapToDouble(d -> d).average().getAsDouble();
            } else if (tiempos.size() == 5) {
                tiempos.remove(0);
                tiempos.remove(4 - 1);
                media = tiempos.stream().mapToDouble(d -> d).average().getAsDouble();
            }
        } else if (categoria.getNumTiempos() == 3){
            if (tiempos.size() == 3){
                media = tiempos.stream().mapToDouble(d -> d).average().getAsDouble();
            }
        }

        return new double[]{mejorTiempo, media};
    }


    public static Integer getPosicionDeParticipante(String nombreParticipante, Categoria categoria, List<PuntuacionTotal> puntuaciones, TiempoRepository tiempoRepository) {

        PuntuacionTotal puntuacionTotalParticipantes = puntuaciones.stream()
                .filter(p -> p.getNombre().equals(nombreParticipante))
                .findFirst()
                .get().clone();

        List<PuntuacionTotal> puntuacionesEmpatadasOriginalmentePorPuntuacionTotal = puntuaciones.stream().map(PuntuacionTotal::clone)
                .filter(p -> p.getPuntuacion_total() == puntuacionTotalParticipantes.getPuntuacion_total()).collect(Collectors.toList());

        if (puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.size() == 1) {
            return puntuaciones.indexOf(puntuaciones.stream().filter(p -> p.getNombre().equals(nombreParticipante)).findFirst().get()) + 1;
        }

        puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.forEach(p -> p.setPuntuacionesIndividuales(tiempoRepository.getParticipantePuntosIndividualesCategoria(categoria, p.getNombre())));

        List<PuntuacionTotal> puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.stream().map(PuntuacionTotal::clone).collect(Collectors.toList());

        List<String> personasDesempatadasEnOrden = new ArrayList<>();

        //Mientras el nombre que buscamos no esté ordenado
        while (!personasDesempatadasEnOrden.stream().anyMatch(p -> p.equals(nombreParticipante))) {

            int mejorPuntuacionJornada = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream().mapToInt(ps -> ps.getPuntuacionesIndividuales().
                    stream().reduce((acc, val) -> acc.getPuntuacion_jornada() > val.getPuntuacion_jornada() ? acc : val).get().getPuntuacion_jornada()).max().getAsInt();

            // Desempatar por jornadas individuales
            // Copia de la lista siempre para jugar con los arrays y no afectar a los de la original
            List<PuntuacionTotal> puntuacionesEmpatadasPorPuntuacionesIndividuales = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream().map(PuntuacionTotal::clone)
                    .filter(p -> p.getPuntuacionesIndividuales().stream()
                            .reduce((acc, val) -> acc.getPuntuacion_jornada() > val.getPuntuacion_jornada() ? acc : val)
                            .get().getPuntuacion_jornada() == mejorPuntuacionJornada)
                    .collect(Collectors.toList());

            if (puntuacionesEmpatadasPorPuntuacionesIndividuales.size() == 1) {

                String personaConMejorPuntuacionIndividual = puntuacionesEmpatadasPorPuntuacionesIndividuales.get(0).getNombre();
                personasDesempatadasEnOrden.add(personaConMejorPuntuacionIndividual);
                puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream()
                        .filter(p -> !p.getNombre().equals(personaConMejorPuntuacionIndividual))
                        .collect(Collectors.toList());

            } else {

                //Desempatar por media
                List<Tiempo> tiemposEmpatados = tiempoRepository.getTiemposDeVariosParticipantes(categoria, puntuacionesEmpatadasPorPuntuacionesIndividuales.stream().map(p -> p.getNombre()).collect(Collectors.toSet()));
                tiemposEmpatados.stream().forEach(t -> t.setMedia(getTiemposCalculados(Arrays.asList(t.getTiempo1(), t.getTiempo2(), t.getTiempo3(), t.getTiempo4(), t.getTiempo5()), categoria)[1]));

                Tiempo mejorMedia = tiemposEmpatados.stream().reduce((acc, val) -> (acc.getMedia() > 0) && (acc.getMedia() < val.getMedia()) ? acc : val).get();

                String personaConMejorMedia = puntuacionesEmpatadasPorPuntuacionesIndividuales.stream().filter(p -> p.getNombre().equals(mejorMedia.getParticipante().getNombre())).findFirst().get().getNombre();
                personasDesempatadasEnOrden.add(personaConMejorMedia);
                puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream()
                        .filter(p -> !p.getNombre().equals(personaConMejorMedia))
                        .collect(Collectors.toList());

            }
        }

        List<String> nombresEmpatados = puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.stream().map(PuntuacionTotal::getNombre).collect(Collectors.toList());
        int posicionParticipanteSuperior = puntuaciones.indexOf(puntuaciones.stream()
                .filter(p -> nombresEmpatados.contains(p.getNombre()))
                .findFirst()
                .get());

        int posicionParticipante = personasDesempatadasEnOrden.indexOf(personasDesempatadasEnOrden.stream()
                .filter(nombreParticipante::equals)
                .findFirst()
                .get()) + 1;

        return posicionParticipanteSuperior + posicionParticipante;
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

}
