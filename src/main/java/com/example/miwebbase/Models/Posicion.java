package com.example.miwebbase.Models;


import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Competicion;
import com.example.miwebbase.Entities.Participante;
import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Utils.AESUtils;
import com.example.miwebbase.repositories.ClasificadoRepository;
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
public class Posicion {

    //TODO: qué me asegura que estén ordenados por jornada?
    List<Tiempo> tiempos;
    int posicionGeneral;
    boolean clasificado;
    String medalla;

    public void setMedalla(ClasificadoRepository clasificadoRepository){
        medalla = AESUtils.getPosicionFinal(clasificadoRepository.getRondasParticipante(getCompeticion(), getParticipante(), getCategoria()));
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

    public Categoria getCategoria(){
        if(tiempos == null || tiempos.size() < 1){
            return Categoria.builder().nombre("misterio").build();
        } else {
            return  tiempos.get(0).getCategoria();
        }
    }

    public Competicion getCompeticion(){
        if(tiempos == null || tiempos.size() < 1){
            return Competicion.builder().nombre("misterio").build();
        } else {
            return tiempos.get(0).getCompeticion();
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

}