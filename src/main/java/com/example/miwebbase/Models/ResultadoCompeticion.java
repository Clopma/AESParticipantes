package com.example.miwebbase.Models;

import com.example.miwebbase.Controllers.RankingGeneralController;
import com.example.miwebbase.Entities.Competicion;
import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Utils.AESUtils;
import com.example.miwebbase.repositories.ClasificadoRepository;
import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.miwebbase.Utils.AESUtils.formatTime;

@Setter
@Getter
public class ResultadoCompeticion {

    String nombreCompeticion;
    List<Categoria> categoriasParticipadas;

    public void generarYAnadirCategoria(List<Tiempo> tiemposJornadasCategoria, String nombreParticipante, RankingGeneralController rankingGeneralController, ClasificadoRepository clasificadoRepository) {

        com.example.miwebbase.Entities.Categoria categoria = tiemposJornadasCategoria.get(0).getCategoria();
        com.example.miwebbase.Entities.Competicion competicion = tiemposJornadasCategoria.get(0).getCompeticion();

        ResultadoCompeticion.Categoria resultadoCategoria = rankingGeneralController.getRankingParticipante(categoria, competicion, nombreParticipante);

        resultadoCategoria.setNombreCategoria(categoria.getNombre());
        resultadoCategoria.setNumTiempos(categoria.getNumTiempos());
        resultadoCategoria.setTamano(50.0/Math.pow(resultadoCategoria.getPosicion(), 0.3) + 10);


        resultadoCategoria.setMedalla(AESUtils.getPosicionFinal(clasificadoRepository.getRondasParticipante(nombreCompeticion, nombreParticipante, categoria.getNombre())));

        List<Tiempo> jornadasParticipadasEnOrden = tiemposJornadasCategoria.stream().sorted(Comparator.comparingInt(Tiempo::getJornada)).collect(Collectors.toList());

        jornadasParticipadasEnOrden.forEach(resultadoCategoria::generarYAnadirJornada);

        getCategoriasParticipadas().add(resultadoCategoria);
    }


    @AllArgsConstructor
    @Builder
    @Setter
    @Getter
    public static class Categoria {

        String nombreParticipante;
        @Setter(AccessLevel.NONE)
        private Integer puntuacion_total;

        String nombreCategoria;
        int numTiempos;
        @Setter(AccessLevel.NONE)
        List<Jornada> jornadas;
        int posicion;
        boolean clasificado;
        String medalla;
        double tamano;

        public Categoria(String nombreParticipante, Long puntuacion_total) {
            this.nombreParticipante = nombreParticipante;
            this.puntuacion_total = Math.toIntExact(puntuacion_total);
        }

        public Categoria(com.example.miwebbase.Entities.Categoria categoria){
            this.nombreCategoria = categoria.getNombre();
            this.numTiempos = categoria.getNumTiempos();
            this.posicion = categoria.getOrden();
            this.jornadas = new ArrayList<>();
            this.nombreCategoria = categoria.getNombre();
            this.numTiempos = categoria.getNumTiempos();
        }


        public Categoria clone(){

            return new Categoria(
                    this.nombreParticipante,
                    this.puntuacion_total,
                    this.nombreCategoria,
                    this.numTiempos,
                    jornadas == null ? null : new ArrayList<>(jornadas),
                    this.posicion,
                    this.clasificado,
                    this.medalla,
                    this.tamano);

        }

        public void resetJornadas(){
            jornadas = new ArrayList<>();
        }

        public void setPuntuacionesJornadas(List<Jornada> jornadas, Competicion competicion){

            List<Jornada> puntuacionesOrdenadas = new ArrayList<>();
            for (int i = 1; i <= competicion.getNumJornadas(); i++){
                final int ii = i;
                puntuacionesOrdenadas.add(jornadas.stream().filter(p -> p.numJornada == ii).findFirst()
                        .orElseGet(() -> Jornada.builder().numJornada(ii).participado(false).build()));
            }
            this.jornadas = jornadas;

        }

        // categoria.html
        public String getPuntuacionJornada(int i) {

            if (jornadas.size()-1 >= i && jornadas.get(i).isParticipado()){
                return jornadas.get(i).getPuntos()+"";
            } else {
                return "-";
            }
        }

        public void generarYAnadirJornada(Tiempo tiempo){

            ResultadoCompeticion.Categoria.Jornada jornadaObj;
            Optional<Jornada> jornadaYaCreada = jornadas.stream().filter(j -> j.numJornada == tiempo.getJornada() && tiempo.getParticipante().getNombre().equals(nombreParticipante)).findFirst();

            if(jornadaYaCreada.isPresent()){
                jornadaObj = jornadaYaCreada.get();
            } else {
                jornadaObj = new ResultadoCompeticion.Categoria.Jornada();
                this.getJornadas().add(jornadaObj);
            }

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

            double[] singleMediaYpeor = AESUtils.getTiemposCalculados(tiempos, numTiempos);

            jornadaObj.setTiempo1(tiempo.getTiempo1() == 0 ? AESUtils.DNF : formatTime(tiempo.getTiempo1()));

            if (this.getNumTiempos() > 1) {

                jornadaObj.setTiempo2(tiempo.getTiempo2() == 0 ? AESUtils.DNF : formatTime(tiempo.getTiempo2()));
                jornadaObj.setTiempo3(tiempo.getTiempo3() == 0 ? AESUtils.DNF : formatTime(tiempo.getTiempo3()));
            }

            if (this.getNumTiempos() > 3) {

                jornadaObj.setTiempo4(tiempo.getTiempo4() == 0 ? AESUtils.DNF : formatTime(tiempo.getTiempo4()));
                jornadaObj.setTiempo5(tiempo.getTiempo5() == 0 ? AESUtils.DNF : formatTime(tiempo.getTiempo5()));
            }

            jornadaObj.setSingle(singleMediaYpeor[0] == 0 ? AESUtils.DNF : formatTime(singleMediaYpeor[0]));
            jornadaObj.setMedia(singleMediaYpeor[1] == 0 ? AESUtils.DNF : formatTime(singleMediaYpeor[1]));

            //Si la media no es un DNF
            if(singleMediaYpeor[1] > 0 && numTiempos == 5){

                if(tiempo.getTiempo1() == singleMediaYpeor[0]){ jornadaObj.setTiempoDescartadoAbajo(1);}
                else if(tiempo.getTiempo2() == singleMediaYpeor[0]){ jornadaObj.setTiempoDescartadoAbajo(2); }
                else if(tiempo.getTiempo3() == singleMediaYpeor[0]){ jornadaObj.setTiempoDescartadoAbajo(3); }
                else if(tiempo.getTiempo4() == singleMediaYpeor[0]){ jornadaObj.setTiempoDescartadoAbajo(4); }
                else if(tiempo.getTiempo5() == singleMediaYpeor[0]){ jornadaObj.setTiempoDescartadoAbajo(5); }

                if(tiempo.getTiempo1() == singleMediaYpeor[2]){ jornadaObj.setTiempoDescartadoArriba(1);}
                else if(tiempo.getTiempo2() == singleMediaYpeor[2]){ jornadaObj.setTiempoDescartadoArriba(2); }
                else if(tiempo.getTiempo3() == singleMediaYpeor[2]){ jornadaObj.setTiempoDescartadoArriba(3); }
                else if(tiempo.getTiempo4() == singleMediaYpeor[2]){ jornadaObj.setTiempoDescartadoArriba(4); }
                else if(tiempo.getTiempo5() == singleMediaYpeor[2]){ jornadaObj.setTiempoDescartadoArriba(5); }

            }


            jornadaObj.setPuntos(tiempo.getPuntosTotales());
        }

        @Setter
        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Jornada {

            int numJornada;
            int posicion;
            int tiempoDescartadoArriba;
            int tiempoDescartadoAbajo;
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

            boolean participado;

            public Jornada(String nombreParticipante, int numJornada, int puntos, boolean participado){
                this.nombreParticipante = nombreParticipante;
                this.numJornada = numJornada;
                this.puntos = puntos;
                this.participado = participado;
            }

        }

    }




}



