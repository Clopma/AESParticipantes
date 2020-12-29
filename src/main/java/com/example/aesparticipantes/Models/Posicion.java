package com.example.aesparticipantes.Models;


import com.example.aesparticipantes.Entities.Evento;
import com.example.aesparticipantes.Entities.Jornada;
import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Entities.Tiempo;
import com.example.aesparticipantes.Repositories.ClasificadoRepository;
import com.example.aesparticipantes.Utils.AESUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class Posicion implements Comparable {


    Evento evento;
    List<Tiempo> tiempos;
    int posicionGeneral;
    boolean clasificado;
    String medalla;

    public void setMedalla(ClasificadoRepository clasificadoRepository){
        medalla = AESUtils.getPosicionFinal(clasificadoRepository.getRondasParticipante(getParticipante(), getEvento()));
    }

    public double getTamano(){
        return  50.0/Math.pow(posicionGeneral, 0.3) + 10;
    }

    public Integer getPuntuacionTotal(){
        if(tiempos == null) {return 0;}
        return tiempos.stream().filter(t -> t.getJornada().getFechaFin().before(new Date())).mapToInt(Tiempo::getPuntosTotales).reduce(Integer::sum).orElse(0);
    }

    public Participante getParticipante(){
        if(tiempos == null || tiempos.size() < 1){
            return Participante.builder().nombre("ZZZmisterio").build(); //Cuando el participante no tiene tiempos, a la hora de ordenar en el ranking, por orden alfabético que sea el últimos.
        } else {
            return  tiempos.get(0).getParticipante();
        }
    }


    public Posicion clone(){
        return new Posicion(
                evento,
                new ArrayList<>(tiempos),
                this.posicionGeneral,
                this.clasificado,
                this.medalla);

    }

    public Optional<String> getPuntuacionJornada(int numeroJornada) {
        Optional<Tiempo> tiempo = tiempos.stream().filter(t -> t.getJornada().getNumeroJornada() == numeroJornada).findFirst();
        return tiempo.map(value -> value.getPuntosTotales() + "");
    }

    public boolean primeraJornadaIsActiva(){
        Optional<Jornada> jornadaActiva = getEvento().getCompeticion().getJornadaActiva();
        return jornadaActiva.isPresent() && jornadaActiva.get().getNumeroJornada() == 1;
    }

    @Override
    public int compareTo(Object o) {
        return  Integer.compare(this.getEvento().getCategoria().getOrden(), ((Posicion) o).getEvento().getCategoria().getOrden());
    }
}