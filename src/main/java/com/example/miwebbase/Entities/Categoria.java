package com.example.miwebbase.Entities;


import com.example.miwebbase.Models.Posicion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Table(name = "Categorias")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {


    @Id
    @Column(length = 25)
    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoria")
    List<Tiempo> tiempos;

    @NotNull
    private int numTiempos;

    @NotNull
    private int orden;

    @NotNull
    private Integer cortePlayOffs;

    @Transient
    Map<Competicion, List<Posicion>> rankings;

    public void refrescarRanking(Competicion competicion, boolean forzar) {

        if(forzar || rankings.get(competicion) == null){
            List<Tiempo> tiemposRanking = tiempos.stream().filter(t -> t.getCompeticion().equals(competicion)).collect(Collectors.toList());

        }

    private static Integer getPosicionDeParticipante(Participante participante, List<Posicion> puntuaciones) {

        Posicion puntuacionTotalParticipantes = puntuaciones.stream()
                .filter(p -> p.getParticipante().equals(participante))
                .findFirst()
                .get().clone();

        List<Posicion> puntuacionesEmpatadasOriginalmentePorPuntuacionTotal = puntuaciones.stream().map(Posicion::clone)
                    .filter(p -> p.getPuntuacionTotal().intValue() == puntuacionTotalParticipantes.getPuntuacionTotal().intValue()).collect(Collectors.toList());

        if (puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.size() == 1) {
            return puntuaciones.indexOf(puntuaciones.stream().filter(p -> p.getParticipante().equals(participante)).findFirst().get()) + 1;
        }

        // TODO: Lo mismo que arriba
        // puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.forEach(p -> p.setPuntuacionesJornadas(tiempoRepository.getParticipantePuntosIndividualesCategoria(categoria, p.getNombreParticipante(), competicion), competicion));

        List<Posicion> puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.stream().map(Posicion::clone).collect(Collectors.toList());

        //TODO: porqué no posiciones?
        List<String> personasDesempatadasEnOrden = new ArrayList<>();

        //Mientras el nombre que buscamos no esté ordenado
        while (personasDesempatadasEnOrden.stream().noneMatch(p -> p.equals(participante.getNombre()))) {

            List<Posicion> puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas =
                    puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream().map(Posicion::clone).collect(Collectors.toList());

            puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.forEach(p -> p.getTiempos().sort(Comparator.comparingInt(t -> t.getPuntosTotales()).reversed()));

            List<Posicion> puntuacionesEmpatadasPorPuntuacionesIndividuales;

            while (true) {

                int mejorPuntuacionJornadaN = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.stream()
                        .mapToInt(ps -> ps.getTiempos().size() > 0 ? ps.getTiempos().get(0).getPuntosTotales() : 0)
                        .max().getAsInt();

                List<Posicion> aux = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.stream().map(Posicion::clone)
                        .filter(p -> p.getTiempos().size() > 0 && p.getTiempos().get(0).getPuntosTotales() == mejorPuntuacionJornadaN)
                        .collect(Collectors.toList());

                if (aux.size() > 1) {

                    puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.forEach(p -> p.getTiempos().remove(0));

                } else if (aux.size() == 1) {

                    puntuacionesEmpatadasPorPuntuacionesIndividuales = aux;
                    break;

                } else /*if (aux.size() == 0)*/ {

                    puntuacionesEmpatadasPorPuntuacionesIndividuales = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas;
                    break;

                }

            }

            if (puntuacionesEmpatadasPorPuntuacionesIndividuales.size() == 1) {

                String personaConMejorPuntuacionIndividual = puntuacionesEmpatadasPorPuntuacionesIndividuales.get(0).getParticipante().getNombre();
                personasDesempatadasEnOrden.add(personaConMejorPuntuacionIndividual);
                puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream()
                        .filter(p -> !p.getParticipante().getNombre().equals(personaConMejorPuntuacionIndividual))
                        .collect(Collectors.toList());

            } else {

                //Desempatar por media
                List<Tiempo> tiemposEmpatados = tiempoRepository.getTiemposDeVariosParticipantes(categoria, puntuacionesEmpatadasPorPuntuacionesIndividuales.stream().map(Posicion::getNombreParticipante).collect(Collectors.toSet()), competicion);
                tiemposEmpatados.forEach(t -> t.setMedia(AESUtils.getTiemposCalculados(Arrays.asList(t.getTiempo1(), t.getTiempo2(), t.getTiempo3(), t.getTiempo4(), t.getTiempo5()), categoria.getNumTiempos())[1]));

                Tiempo mejorMedia = tiemposEmpatados.stream().reduce((acc, val) -> (acc.getMedia() > 0) && (acc.getMedia() < val.getMedia()) ? acc : val).get();

                String personaConMejorMedia = puntuacionesEmpatadasPorPuntuacionesIndividuales.stream().filter(p -> p.getNombreParticipante().equals(mejorMedia.getParticipante().getNombre())).findFirst().get().getNombreParticipante();
                personasDesempatadasEnOrden.add(personaConMejorMedia);
                puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream()
                        .filter(p -> !p.getNombreParticipante().equals(personaConMejorMedia))
                        .collect(Collectors.toList());

            }
        }

        List<String> nombresEmpatados = puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.stream().map(Posicion::getNombreParticipante).collect(Collectors.toList());
        int posicionParticipanteSuperior = puntuaciones.indexOf(puntuaciones.stream()
                .filter(p -> nombresEmpatados.contains(p.getNombreParticipante()))
                .findFirst()
                .get());

        int posicionParticipante = personasDesempatadasEnOrden.indexOf(personasDesempatadasEnOrden.stream()
                .filter(nombreParticipante::equals)
                .findFirst()
                .get()) + 1;

        return posicionParticipanteSuperior + posicionParticipante;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Categoria)) {
            return false;
        }

        Categoria c = (Categoria) o;

        return c.getNombre().equals(this.getNombre());
    }


}


