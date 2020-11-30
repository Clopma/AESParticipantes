package com.example.aesparticipantes.Entities.Keys;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class KeyClasificado implements Serializable {

    private KeyEvento evento;
    private String participante;
    private String ronda;



}
