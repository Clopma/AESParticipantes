package com.example.miwebbase.Entities;


import com.example.miwebbase.Models.PuntuacionRanking;
import com.example.miwebbase.Utils.RubikUtils;
import com.example.miwebbase.repositories.TiempoRepository;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.security.KeyPair;
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


    public int getPosicionDeParticipante(String nombreParticipante, Categoria categoria, TiempoRepository tiempoRepository){

        List<PuntuacionRanking> puntuaciones = tiempoRepository.getParticipantesPuntosTotalesCategoria(categoria);


        PuntuacionRanking puntuacionParticipante = puntuaciones.stream()
                .filter(p -> p.getNombre().equals(nombreParticipante))
                .findFirst()
                .get();

        List<PuntuacionRanking> puntuacionesEmpatadas = puntuaciones.stream()
                .filter(p -> p.getPuntos_totales() == puntuacionParticipante.getPuntos_totales()).collect(Collectors.toList());

        if(puntuacionesEmpatadas.size() == 1){
            return puntuaciones.indexOf(puntuacionParticipante) + 1;
        }

        Map<String, List<Integer>> puntuacionesPersonasEmpatadas = new HashMap<>();

        for (PuntuacionRanking puntuacion : puntuacionesEmpatadas){
            puntuacionesPersonasEmpatadas.put(puntuacion.getNombre(), tiempoRepository.getPosicionesParticipanteEnCategoria(categoria, puntuacion.getNombre()));
        }

        List<PuntuacionRanking> puntuacionesDesempatadas = new ArrayList<>();

        //Mientras el nombre que buscamos no esté ordenado
        while (!puntuacionesDesempatadas.stream().anyMatch(p -> p.getNombre().equals(nombreParticipante))) {

            Map<String, List<Integer>>  puntuacionesPorOrdenar = new HashMap<>(puntuacionesPersonasEmpatadas);
            //Intenta encontrar un mejor absoluto con las mejores puntuaciones máximas
            while (puntuacionesPersonasEmpatadas.size() > 1) {

                int mejorPuntuacionJornada = puntuacionesPorOrdenar.values().stream().mapToInt(ts -> ts.get(0)).max().getAsInt();

                Map<String, List<Integer>> aux = puntuacionesPorOrdenar.entrySet().stream().filter(p -> p.getValue().get(0) == mejorPuntuacionJornada)
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

                aux.forEach((k, v) -> v.remove(0));

                aux = aux.entrySet().stream().filter(p -> p.getValue().size() > 0).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

                aux = aux.entrySet().stream().filter(p -> p.getValue().size() > 0)
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

                if (aux.size() == 0) {
                    break;  //Nos quedamos con todos los empatados
                }

                puntuacionesPorOrdenar = aux;

            }

            //Si hay un ganador absoluto con alguna de las puntuaciones máximas
            if (puntuacionesEmpatadas.size() == 1){
                puntuacionesDesempatadas.add(puntuacionesEmpatadas.get(0)); //Siempre va a a ser el que se busca
                puntuacionesEmpatadas.remove(puntuacionesEmpatadas.get(0));
            } else {
            //Si todas las puntuaciones de todas las jornadas coinciden
               List<Tiempo> tiemposEmpatados = tiempoRepository.getTiemposDeVariosParticipantes(categoria, puntuacionesPersonasEmpatadas.keySet());
               Tiempo mejorMedia = tiemposEmpatados.stream().reduce((acc, val) -> {
                  val.setMedia(RubikUtils.getTiemposCalculados(Arrays.asList(val.getTiempo1(), val.getTiempo2(), val.getTiempo3(), val.getTiempo4(), val.getTiempo5()), categoria)[1]);
                  return  acc.getMedia() > val.getMedia() ? acc : acc;
               }).get();

               PuntuacionRanking mejorPuntuacion = puntuacionesEmpatadas.stream().filter(p -> p.getNombre().equals(mejorMedia.getParticipante().getNombre())).findFirst().get();
                puntuacionesDesempatadas.add(mejorPuntuacion);
                puntuacionesEmpatadas.remove(mejorPuntuacion);

            }
        }

        List<String> nombresEmpatados = puntuacionesEmpatadas.stream().map(p -> p.getNombre()).collect(Collectors.toList());
        int posicionParticipanteSuperior = puntuaciones.indexOf(puntuaciones.stream()
                .filter(p -> nombresEmpatados.contains(p.getNombre()))
                .findFirst()
                .get()) -1;

        int posicionParticipante = puntuacionesEmpatadas.indexOf(puntuaciones.stream()
                .filter(p -> nombreParticipante.equals(p.getNombre()))
                .findFirst()
                .get()) + 1;


        return posicionParticipanteSuperior + posicionParticipante + 1;
    }




}
