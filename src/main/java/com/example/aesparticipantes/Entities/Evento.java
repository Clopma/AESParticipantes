package com.example.aesparticipantes.Entities;


import com.example.aesparticipantes.Entities.Keys.KeyEvento;
import com.example.aesparticipantes.Models.Posicion;
import com.example.aesparticipantes.Repositories.ClasificadoRepository;
import com.example.aesparticipantes.Repositories.DescalificacionRepository;
import com.example.aesparticipantes.Utils.AESUtils;
import lombok.*;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Table(name = "Eventos")
@Entity
@IdClass(KeyEvento.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Configurable
public class Evento {

    @Id
    @ManyToOne
    private Categoria categoria;

    @Id
    @ManyToOne
    private Competicion competicion;

    @OneToMany(mappedBy = "evento")
    private List<Inscripcion> inscripciones;

    @NotNull
    private Integer cortePlayOffs;

    public boolean isParticipanteInscrito(Participante p){
       return inscripciones.stream().anyMatch(i -> i.getParticipante().equals(p));
    }

    public List<Tiempo> getTiempos(){
        List<Tiempo> tiempos = new ArrayList<>();
        competicion.getJornadas().forEach(j -> tiempos.addAll(j.getTiempos().stream().filter(t -> t.getCategoria().equals(categoria)).collect(Collectors.toList()))); // TODO: Feísimo: Hay alguna forma de mapear tiempos en esta entidad???
        return tiempos;
    }

    //Ser llamado solo desde cacheable
    public List<Tiempo> getRankingJornada(int numJornada){

        List<Tiempo> tiemposEnJornada = getTiempos().stream().filter(t -> t.getJornada().getNumeroJornada() == numJornada).collect(Collectors.toList()); //TODO: Quizás una query
        AESUtils.setPosicionesEnTiempos(tiemposEnJornada);
        tiemposEnJornada.sort(Comparator.comparingInt(Tiempo::getPosicion));

        return tiemposEnJornada;

    }

    //Ser llamado solo desde cacheable
    public List<Posicion> getRankingGlobal(DescalificacionRepository descalificacionRepository, ClasificadoRepository clasificadoRepository) {



            List<Tiempo> tiemposRanking = getTiempos();
//            tiemposRanking.stream().filter(t -> t.getJornada().getEvento().getCompeticion().equals(competicion)).collect(Collectors.toList()); TODO: Ya son de la compecición, cierto?

            List<Posicion> puntuacionesTotales = tiemposRanking.stream().collect(Collectors.groupingBy(Tiempo::getParticipante))
                            .values().stream().map(tiempos -> Posicion.builder()
                            .evento(this)
                            .tiempos(tiempos)
                            .build()
                    ).collect(Collectors.toList());

            List<Posicion> puntuacionesTotalesAux = new ArrayList<>(puntuacionesTotales);


            // Ordena por puntuación y si no, por puntuación desempatada
            Comparator<Posicion> comparadorPuntosYPosiciones =  ((Comparator<Posicion>)(p1, p2) -> p2.getPuntuacionTotal().compareTo(p1.getPuntuacionTotal()))
                    .thenComparing(p -> getPosicionDeParticipante(p.getParticipante(), puntuacionesTotalesAux));

            puntuacionesTotales.sort(comparadorPuntosYPosiciones);

            // Setea todas las posiciones tal y como se han ordenado
            AtomicInteger posicion = new AtomicInteger(1);
            puntuacionesTotales.forEach(p -> p.setPosicionGeneral(posicion.getAndIncrement()));

            List<Descalificacion> descalificados = descalificacionRepository.findAllByEvento(this);


            List<Posicion> noDescalificados = puntuacionesTotales.stream().filter(
                    p -> descalificados.stream().noneMatch(d -> p.getParticipante().equals(d.getParticipante())))
                    .collect(Collectors.toList());

            if (noDescalificados.size() > 0) {
                noDescalificados.subList(0, Math.min(this.getCortePlayOffs(), noDescalificados.size())) //TODO: TEST un poco mas
                        .forEach(p -> p.setClasificado(true));
            }

                AESUtils.setMedallas(puntuacionesTotales, clasificadoRepository);

             return puntuacionesTotales;

    }

    private Integer getPosicionDeParticipante(Participante participante, List<Posicion> puntuaciones) {

        int puntuacionADesempatar = puntuaciones.stream()
                .filter(p -> p.getParticipante().equals(participante))
                .findFirst()
                .get().getPuntuacionTotal();

        List<Posicion> puntuacionesEmpatadasOriginalmentePorPuntuacionTotal = puntuaciones.stream().map(Posicion::clone)
                    .filter(p -> p.getPuntuacionTotal() == puntuacionADesempatar).collect(Collectors.toList());

        if (puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.size() == 1) {
            return puntuaciones.indexOf(puntuaciones.stream().filter(p -> p.getParticipante().equals(participante)).findFirst().get()) + 1;
        }

        List<Posicion> puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.stream().map(Posicion::clone).collect(Collectors.toList());

        List<Posicion> personasDesempatadasEnOrden = new ArrayList<>();

        int i = 0; //Por seguridad
        //Mientras el nombre que buscamos no esté ordenado
        while (personasDesempatadasEnOrden.stream().noneMatch(p -> p.getParticipante().equals(participante)) && i < 1000) {

            i++; //TODO: logs
            List<Posicion> puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas =
                    puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream().map(Posicion::clone).collect(Collectors.toList());

            puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.forEach(p -> p.getTiempos().sort(Comparator.comparingInt(Tiempo::getPuntosTotales).reversed()));

            List<Posicion> puntuacionesEmpatadasPorPuntuacionesIndividuales = new ArrayList<>();
            List<Posicion> aux2 = new ArrayList<>();
            int j = 0; //por seguridad
            while (j < 1000) {
                j++; //TODO: logs

                int mejorPuntuacionJornadaN = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.stream()
                        .mapToInt(ps -> ps.getTiempos().size() > 0 ? ps.getTiempos().get(0).getPuntosTotales() : 0)
                        .max().getAsInt();

                List<Posicion> aux = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.stream().map(Posicion::clone)
                        .filter(p -> p.getTiempos().size() > 0 && p.getTiempos().get(0).getPuntosTotales() == mejorPuntuacionJornadaN)
                        .collect(Collectors.toList());

                if (aux.size() > 1) {

                    aux2 = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.stream().map(Posicion::clone).collect(Collectors.toList()); //Me la guardo para que quede al menos un último Tiempo que identifique el Participante
                    puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.forEach(p -> { if(p.getTiempos().size() > 0) { p.getTiempos().remove(0); } });

                } else if (aux.size() == 1) {

                    puntuacionesEmpatadasPorPuntuacionesIndividuales = aux;
                    break;

                } else /*if (aux.size() == 0)*/ {

                    puntuacionesEmpatadasPorPuntuacionesIndividuales = aux2;
                    break;

                }

            }

            if (puntuacionesEmpatadasPorPuntuacionesIndividuales.size() == 1) {

                Posicion personaConMejorPuntuacionIndividual = puntuacionesEmpatadasPorPuntuacionesIndividuales.get(0);
                personasDesempatadasEnOrden.add(personaConMejorPuntuacionIndividual);
                puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream()
                        .filter(p -> !p.getParticipante().equals(personaConMejorPuntuacionIndividual.getParticipante()))
                        .collect(Collectors.toList());

            } else {

                //Desempatar por media
                List<Tiempo> tiemposEmpatados = new ArrayList<>();
                puntuacionesEmpatadasPorPuntuacionesIndividuales.forEach(p -> tiemposEmpatados.addAll(p.getTiempos()));

                //tiemposEmpatados.forEach(Tiempo::calcularDatos); TODO: Ya deberían estar calculados en este punto, no? Puedo eliminar?

                Optional<Tiempo> mejorMedia = tiemposEmpatados.stream().reduce((acc, val) -> (acc.getMedia() > 0) && (acc.getMedia() < val.getMedia()) ? acc : val);

                Posicion personaConMejorMedia;

                if(mejorMedia.isPresent()) {
                    personaConMejorMedia = puntuacionesEmpatadasPorPuntuacionesIndividuales.stream().filter(p -> p.getParticipante().equals(mejorMedia.get().getParticipante())).findFirst().get();
                } else {
                    personaConMejorMedia = puntuacionesEmpatadasPorPuntuacionesIndividuales.get(0); // En este punto ya orden alfabético o lo que Dios quiera
                }

                personasDesempatadasEnOrden.add(personaConMejorMedia);
                puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream()
                        .filter(p -> !p.getParticipante().equals(personaConMejorMedia.getParticipante()))
                        .collect(Collectors.toList());
            }
        }

        List<String> nombresEmpatados = puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.stream().map(p -> p.getParticipante().getNombre()).collect(Collectors.toList());
        int posicionParticipanteSuperior = puntuaciones.indexOf(puntuaciones.stream()
                .filter(p -> nombresEmpatados.contains(p.getParticipante().getNombre()))
                .findFirst()
                .get());

        int posicionParticipante = personasDesempatadasEnOrden.indexOf(personasDesempatadasEnOrden.stream()
                .filter(p -> p.getParticipante().equals(participante))
                .findFirst()
                .get()) + 1;

        return posicionParticipanteSuperior + posicionParticipante;
    }

    public String getId(){
        return getEventoId(getCompeticion(), getCategoria());
    }

    public static String getEventoId(Competicion competicion, Categoria categoria) {
        return competicion.getNombre() + "-" + categoria.getNombre();
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Evento)) {
            return false;
        }

        Evento e = (Evento) o;

        return e.getCategoria().equals(this.getCategoria())
                && e.getCompeticion().equals(this.getCompeticion());
    }


}


