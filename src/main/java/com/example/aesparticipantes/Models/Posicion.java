package com.example.aesparticipantes.Models;


import com.example.aesparticipantes.Entities.Evento;
import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Entities.Tiempo;
import com.example.aesparticipantes.Repositories.ClasificadoRepository;
import com.example.aesparticipantes.Utils.AESUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class Posicion implements Comparable {

    //TODO: qué me asegura que estén ordenados por jornada?
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
       return tiempos.stream().mapToInt(Tiempo::getPuntosTotales).reduce(Integer::sum).orElse(0);
    }

    public Participante getParticipante(){
        if(tiempos == null || tiempos.size() < 1){
            return Participante.builder().nombre("misterio").build();
        } else {
            return  tiempos.get(0).getParticipante();
        }
    }

    public Evento getEvento(){
        if(tiempos == null || tiempos.size() < 1){
            return Evento.builder().build();
        } else {
            return  tiempos.get(0).getEvento();
        }
    }


    public Posicion clone(){
        return new Posicion(
                new ArrayList<>(tiempos),
                this.posicionGeneral,
                this.clasificado,
                this.medalla);

    }

    public String getPuntuacionJornada(int i) {

        if (tiempos.size() - 1 >= i){
            return tiempos.get(i).getPuntosTotales()+"";
        } else {
            return "-";
        }
    }

    @Override
    public int compareTo(Object o) {
        return  Integer.compare(this.getEvento().getCategoria().getOrden(), ((Posicion) o).getEvento().getCategoria().getOrden());
    }
}