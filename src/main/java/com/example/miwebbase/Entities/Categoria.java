package com.example.miwebbase.Entities;


import com.example.miwebbase.Models.PuntuacionRanking;
import com.example.miwebbase.Utils.RubikUtils;
import com.example.miwebbase.repositories.TiempoRepository;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.*;
import java.util.stream.Collectors;

@Table(name = Categoria.T_CATEGORIAS)
@Entity
@Getter
public class Categoria {

    public static final String T_CATEGORIAS = "Categorias";

    @Id
    private String nombre;

    private int numTiempos;

    private int orden;


    public int getPosicionDeParticipante(String nombreParticipante, Categoria categoria, TiempoRepository tiempoRepository) {

        List<PuntuacionRanking> puntuaciones = tiempoRepository.getParticipantesPuntosTotalesCategoria(categoria);


        PuntuacionRanking puntuacionParticipante = puntuaciones.stream()
                .filter(p -> p.getNombre().equals(nombreParticipante))
                .findFirst()
                .get();

        List<PuntuacionRanking> puntuacionesEmpatadas = puntuaciones.stream()
                .filter(p -> p.getPuntos_totales() == puntuacionParticipante.getPuntos_totales()).collect(Collectors.toList());

        if (puntuacionesEmpatadas.size() == 1) {
            return puntuaciones.indexOf(puntuacionParticipante) + 1;
        }

        Map<String, List<Integer>> puntuacionesPersonasEmpatadasPorPuntuacionTotal = new HashMap<>();

        for (PuntuacionRanking puntuacion : puntuacionesEmpatadas) {
            puntuacionesPersonasEmpatadasPorPuntuacionTotal.put(puntuacion.getNombre(), tiempoRepository.getPosicionesParticipanteEnCategoria(categoria, puntuacion.getNombre()));
        }

        List<String> personasDesempatadasEnOrden = new ArrayList<>();

        //Mientras el nombre que buscamos no esté ordenado
        while (!personasDesempatadasEnOrden.stream().anyMatch(p -> p.equals(nombreParticipante))) {

            int mejorPuntuacionJornada = puntuacionesPersonasEmpatadasPorPuntuacionTotal.values().stream().mapToInt(ts -> ts.get(0)).max().getAsInt();

            // Desempatar por jornadas individuales
            Map<String, List<Integer>> puntuacionesPersonasEmpatadasPorPuntuacionesIndividuales = puntuacionesPersonasEmpatadasPorPuntuacionTotal.entrySet().stream().filter(p -> p.getValue().get(0) == mejorPuntuacionJornada)
                    .collect(Collectors.toMap(e -> e.getKey(), e -> new ArrayList<>(e.getValue())));

            if (puntuacionesPersonasEmpatadasPorPuntuacionesIndividuales.size() == 1) {

                String personaConMejorPuntuacionIndividual = puntuacionesPersonasEmpatadasPorPuntuacionesIndividuales.entrySet().iterator().next().getKey();
                personasDesempatadasEnOrden.add(personaConMejorPuntuacionIndividual);
                puntuacionesPersonasEmpatadasPorPuntuacionTotal = puntuacionesPersonasEmpatadasPorPuntuacionTotal.entrySet().stream()
                        .filter(p -> !p.getKey().equals(personaConMejorPuntuacionIndividual))
                        .collect(Collectors.toMap(e -> e.getKey(), e-> e.getValue()));

            } else {

                //Desempatar por media
                List<Tiempo> tiemposEmpatados = tiempoRepository.getTiemposDeVariosParticipantes(categoria, puntuacionesPersonasEmpatadasPorPuntuacionesIndividuales.keySet());
                tiemposEmpatados.stream().forEach(t -> t.setMedia(RubikUtils.getTiemposCalculados(Arrays.asList(t.getTiempo1(), t.getTiempo2(), t.getTiempo3(), t.getTiempo4(), t.getTiempo5()), categoria)[1]));

                Tiempo mejorMedia = tiemposEmpatados.stream().reduce((acc, val) -> (acc.getMedia() > 0) && (acc.getMedia() < val.getMedia()) ? acc : val).get();

                String personaConMejorMedia = puntuacionesPersonasEmpatadasPorPuntuacionesIndividuales.entrySet().stream().filter(p -> p.getKey().equals(mejorMedia.getParticipante().getNombre())).findFirst().get().getKey();
                personasDesempatadasEnOrden.add(personaConMejorMedia);
                puntuacionesPersonasEmpatadasPorPuntuacionTotal = puntuacionesPersonasEmpatadasPorPuntuacionTotal.entrySet().stream()
                        .filter(p -> !p.getKey().equals(personaConMejorMedia))
                        .collect(Collectors.toMap(e -> e.getKey(), e-> e.getValue()));

            }
        }

        List<String> nombresEmpatados = puntuacionesEmpatadas.stream().map(p -> p.getNombre()).collect(Collectors.toList());
        int posicionParticipanteSuperior = puntuaciones.indexOf(puntuaciones.stream()
                .filter(p -> nombresEmpatados.contains(p.getNombre()))
                .findFirst()
                .get());

        int posicionParticipante = personasDesempatadasEnOrden.indexOf(personasDesempatadasEnOrden.stream()
                .filter(p -> nombreParticipante.equals(p))
                .findFirst()
                .get()) + 1;


        return posicionParticipanteSuperior + posicionParticipante;
    }


}
