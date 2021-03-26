package com.example.aesparticipantes.Models;

import com.example.aesparticipantes.Controllers.DivisionController;
import com.example.aesparticipantes.Entities.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class TimelinePointDivisiones {

    Temporada temporada;
    Jornada jornada;

    public Optional<TimelinePointDivisiones> getAnterior(){
        Optional<Jornada> jornadaAnterior = jornada.getCompeticion().getJornada(jornada.getNumeroJornada() -1);
        if(jornadaAnterior.isPresent()){
            return Optional.of(new TimelinePointDivisiones(temporada, jornadaAnterior.get()));
        } else {
            //Estamos en una primera jornada, hay que irse a la competición anterior
            int indexCompeticion = new ArrayList(temporada.getCompeticiones()).indexOf(jornada.getCompeticion());
            if(indexCompeticion == 0){
                //Estamos en la primera jornada de la primera competición
                return Optional.empty();
            } else {
                //Se saca de la última jornada de la competición anteror.
                Competicion competicionAnterior = new ArrayList<>(temporada.getCompeticiones()).get(indexCompeticion -1);
                return Optional.of(new TimelinePointDivisiones(temporada, competicionAnterior.getUltimaJornada()));
            }
        }
    }

    public Optional<TimelinePointDivisiones> getSiguiente(){
        Optional<Jornada> jornadaSiguiente = jornada.getCompeticion().getJornada(jornada.getNumeroJornada() + 1);
        if(jornadaSiguiente.isPresent()){
            return Optional.of(new TimelinePointDivisiones(temporada, jornadaSiguiente.get()));
        } else {
            //Estamos en la última jornada, hay que irse a la competición siguiente
            int indexCompeticion = new ArrayList(temporada.getCompeticiones()).indexOf(jornada.getCompeticion());
            if(indexCompeticion == temporada.getCompeticiones().size() - 1){
                //Estamos en la última jornada de la última competición
                return Optional.empty();
            } else {
                //Se saca de la primera jornada de la competición siguiente.
                Competicion competicionAnterior = new ArrayList<>(temporada.getCompeticiones()).get(indexCompeticion + 1);
                return Optional.of(new TimelinePointDivisiones(temporada, competicionAnterior.getPrimeraJornada()));
            }
        }
    }

    public static TimelinePointDivisiones getPrimero(Temporada temporada){
        Competicion competicion = new ArrayList<>(temporada.getCompeticiones()).get(0);
        return new TimelinePointDivisiones(temporada, competicion.getPrimeraJornada());
    }

    public static TimelinePointDivisiones getUltimaAcabada(Temporada temporada){
        List<TimelinePointDivisiones> acabadas = getTimelineCompleta(temporada).stream().filter(t -> t.getJornada().isAcabada()).collect(Collectors.toList());
        return acabadas.stream().filter(t -> t.getJornada().isActiva()).findAny()
                .orElse(acabadas.stream().reduce((acc, val) -> acc.getJornada().getFechaInicio().after(val.getJornada().getFechaInicio()) ? acc : val)
                        .orElseThrow(() -> new RuntimeException("Error: No se ha encontrado el último timelinePoint de la temporada " + temporada.getNombre() + ". Esto no debería ocurrir.")));
    }

    public static List<TimelinePointDivisiones> getTimelineCompleta(Temporada temporada) {
        List<TimelinePointDivisiones> timelineCompleta = new ArrayList<>();

        Optional<TimelinePointDivisiones> i = Optional.of(getPrimero(temporada));
        timelineCompleta.add(i.get());
        while(true){
            i = i.get().getSiguiente();
            if(!i.isPresent() /*|| !i.get().getJornada().isAcabada() */){break;}
            timelineCompleta.add(i.get());
        }
        return timelineCompleta;
    }

    public int[] getDivisionYPosicionParticipante(Participante participante, Categoria categoria, DivisionController divisionController) {

        DivisionesEnJornada fase1 = divisionController.getVistaDivision(this, Evento.builder().categoria(categoria).competicion(jornada.getCompeticion()).build())[1];

        for(int i = 0; i < fase1.getDivisiones().size(); i++){
            DivisionJornada divisionJornada = fase1.getDivisiones().get(i);
            for(int j = 0; j < divisionJornada.getParticipantes().size(); j++){
               if(divisionJornada.getParticipantes().get(j).getNombre().equals(participante.getNombre())){
                  return new int[]{i +1, j+1};
               }
            }
        }

        //No lo ha encontrado en la división a pesar de que tiene un tiempo en ella (ya que si no, no hay que llamar a este método),
        //con lo cual quiere decir que ese tiempo era de la última jornada, y no está incluído en la división
        return new int[]{0, 0};
    }
}
