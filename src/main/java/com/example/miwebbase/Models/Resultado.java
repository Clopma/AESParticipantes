package com.example.miwebbase.Models;

import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Utils.AESUtils;
import com.example.miwebbase.repositories.TiempoRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.miwebbase.Utils.AESUtils.formatTime;

@Setter
@Getter
public class Resultado {

    List<Categoria> categoriasParticipadas;


    @Setter
    @Getter
    public static class Categoria {

        String nombreCategoria;
        int numTiempos;
        List<Jornada> jornadas;
        int posicion;
        double tamano;

        @Setter
        @Getter
        public static class Jornada {

            int numJornada;
            int posicion;
            String tiempo1;
            String tiempo2;
            String tiempo3;
            String tiempo4;
            String tiempo5;
            String single;
            String media;
            String solucion;
            String explicacion;
            int puntos;
            String nombreParticipante;


        }

        public Categoria(com.example.miwebbase.Entities.Categoria categoria){
            this.nombreCategoria = categoria.getNombre();
            this.numTiempos = categoria.getNumTiempos();
            this.posicion = categoria.getOrden();
            this.jornadas = new ArrayList<>();
        }

        public void generarYAnadirJornada(Tiempo tiempo, com.example.miwebbase.Entities.Categoria categoria){
            Resultado.Categoria.Jornada jornadaObj = new Resultado.Categoria.Jornada();
            jornadaObj.setNumJornada(tiempo.getJornada());
            jornadaObj.setPosicion(tiempo.getPosicion());
            jornadaObj.setSolucion(tiempo.getSolucion() == null ? "" : tiempo.getSolucion());
            jornadaObj.setExplicacion(tiempo.getExplicacion() == null ? "" : tiempo.getExplicacion());
            jornadaObj.setNombreParticipante(tiempo.getParticipante().getNombre());

            List<Double> tiempos = new ArrayList<>();
            tiempos.add(tiempo.getTiempo1());
            tiempos.add(tiempo.getTiempo2());
            tiempos.add(tiempo.getTiempo3());
            tiempos.add(tiempo.getTiempo4());
            tiempos.add(tiempo.getTiempo5());

            jornadaObj.setTiempo1(tiempo.getTiempo1() == 0 ? AESUtils.DNF : formatTime(tiempo.getTiempo1()));

            if (this.getNumTiempos() > 1) {

                jornadaObj.setTiempo2(tiempo.getTiempo2() == 0 ? AESUtils.DNF : formatTime(tiempo.getTiempo2()));
                jornadaObj.setTiempo3(tiempo.getTiempo3() == 0 ? AESUtils.DNF : formatTime(tiempo.getTiempo3()));
            }

            if (this.getNumTiempos() > 3) {

                jornadaObj.setTiempo4(tiempo.getTiempo4() == 0 ? AESUtils.DNF : formatTime(tiempo.getTiempo4()));
                jornadaObj.setTiempo5(tiempo.getTiempo5() == 0 ? AESUtils.DNF : formatTime(tiempo.getTiempo5()));
            }


            double[] singleYMedia = AESUtils.getTiemposCalculados(tiempos, categoria);
            jornadaObj.setSingle(singleYMedia[0] == 0 ? AESUtils.DNF : formatTime(singleYMedia[0]));
            jornadaObj.setMedia(singleYMedia[1] == 0 ? AESUtils.DNF : formatTime(singleYMedia[1]));

            jornadaObj.setPuntos(tiempo.getPuntosTotales());
            this.getJornadas().add(jornadaObj);
        }

    }

    public void generarYAnadirCategoria(List<Tiempo> tiemposJornadasCategoria, String nombreParticipante, TiempoRepository tiempoRepository) {

        com.example.miwebbase.Entities.Categoria categoria = tiemposJornadasCategoria.get(0).getCategoria();
        Resultado.Categoria categoriaObj = new Resultado.Categoria(categoria);
        categoriaObj.setNombreCategoria(categoria.getNombre());
        categoriaObj.setNumTiempos(categoria.getNumTiempos());
        categoriaObj.setPosicion(AESUtils.getPosicionDeParticipante(nombreParticipante, categoria, tiempoRepository.getParticipantesPuntosTotalesCategoria(categoria), tiempoRepository));
        categoriaObj.setTamano(50.0/Math.pow(categoriaObj.getPosicion(), 0.3) + 10);

        this.getCategoriasParticipadas().add(categoriaObj);

       List<Tiempo> jornadasParticipadasEnOrden = tiemposJornadasCategoria.stream().sorted(Comparator.comparingInt(Tiempo::getJornada)).collect(Collectors.toList());

        for (Tiempo tiempo : jornadasParticipadasEnOrden) {
            categoriaObj.generarYAnadirJornada(tiempo, categoria);
        }
    }





}



