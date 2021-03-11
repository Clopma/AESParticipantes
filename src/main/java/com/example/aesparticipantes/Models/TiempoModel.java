package com.example.aesparticipantes.Models;

import com.example.aesparticipantes.Entities.Tiempo;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TiempoModel {
    int puntuacionTotal;
    int numJornada;
    boolean jornadaIsAcabada;
    double single;
    double media;
    String nombreParticipante;
    int tiempoDescartadoArriba;
    int tiempoDescartadoAbajo;
    private int posicion;
    private String singleStr;
    private String mediaStr;
    private String tiempo1Str;
    private String tiempo2Str;
    private String tiempo3Str;
    private String tiempo4Str;
    private String tiempo5Str;
    private String solucion;
    private String explicacion;

    public static TiempoModel tiempoToTiempoModel(Tiempo t) {
        return TiempoModel.builder()
                .numJornada(t.getJornada().getNumeroJornada())
                .puntuacionTotal(t.getPuntosTotales())
                .jornadaIsAcabada(t.getJornada().isAcabada())
                .single(t.getSingle())
                .media(t.getMedia())
                .nombreParticipante(t.getParticipante().getNombre())
                .tiempoDescartadoArriba(t.getTiempoDescartadoArriba())
                .tiempoDescartadoAbajo(t.getTiempoDescartadoAbajo())
                .posicion(t.getPosicion())
                .singleStr(t.getSingleStr())
                .mediaStr(t.getMediaStr())
                .tiempo1Str(t.getTiempo1Str())
                .tiempo2Str(t.getTiempo2Str())
                .tiempo3Str(t.getTiempo3Str())
                .tiempo4Str(t.getTiempo4Str())
                .tiempo5Str(t.getTiempo5Str())
                .solucion(t.getSolucion())
                .explicacion(t.getExplicacion())
                .build();
    }
}
