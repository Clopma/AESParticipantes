package com.example.aesparticipantes.Models;


import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Jornada;
import com.example.aesparticipantes.Entities.Tiempo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class Posicion implements Comparable {


    String nombreCategoria;
    String nombreCompeticion;
    Integer puntuacionTotal;
    List<TiempoModel> tiempos;
    String nombreParticipante;
    int posicionGeneral;
    boolean clasificado;
    boolean primeraJornadaIsActiva;
    String medalla;
    int orden;
    int numTiemposCategoria;

    public static class PosicionBuilder {

        public PosicionBuilder categoria(Categoria categoria) {
            this.nombreCategoria = categoria.getNombre();
            this.orden = categoria.getOrden();
            this.numTiemposCategoria = categoria.getNumTiempos();
            return this;
        }

        private PosicionBuilder nombreCategoria(String nombreCategoria) {
            // NO USAR: debería usarse el genérico para categoria
            return this;
        }

        private PosicionBuilder numTiemposCategoria(String numTiempos) {
            // NO USAR: debería usarse el genérico para categoria
            return this;
        }

        public PosicionBuilder puntuacionTotalyParticipante(List<Tiempo> tiempos) {
            if (tiempos == null || tiempos.isEmpty()) {
                this.puntuacionTotal =  0;
                this.nombreParticipante = "ZZZmisterio";
            } else {
                this.puntuacionTotal = tiempos.stream().filter(t -> t.getJornada().getFechaFin().before(new Date())).mapToInt(Tiempo::getPuntosTotales).reduce(Integer::sum).orElse(0);
                this.nombreParticipante = tiempos.get(0).getParticipante().getNombre();
                this.tiempos = tiempos.stream().map(TiempoModel::tiempoToTiempoModel).collect(Collectors.toList());
            }
            return this;
        }

        public PosicionBuilder primeraJornadaIsActiva(Competicion competicion) {
            Optional<Jornada> jornadaActiva = competicion.getJornadaActiva();
            this.primeraJornadaIsActiva = jornadaActiva.isPresent() && jornadaActiva.get().getNumeroJornada() == 1;
            return this;
        }

    }


    public double getTamano(){
        return  50.0/Math.pow(posicionGeneral, 0.3) + 10;
    }


    public Optional<String> getPuntuacionJornada(int numeroJornada) {
        Optional<TiempoModel> tiempo = tiempos.stream().filter(t -> t.getNumJornada() == numeroJornada).findFirst();
        return tiempo.map(value -> value.getPuntuacionTotal() + "");
    }


    @Override
    public int compareTo(Object o) {
        return  Integer.compare(this.orden, ((Posicion) o).orden);
    }


    public Posicion clone(){
        return new Posicion(
                nombreCategoria,
                nombreCompeticion,
                puntuacionTotal,
                new ArrayList<>(tiempos),
                nombreParticipante,
                posicionGeneral,
                clasificado,
                primeraJornadaIsActiva,
                medalla,
                orden,
                numTiemposCategoria);

    }
}