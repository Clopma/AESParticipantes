package com.example.aesparticipantes.Entities.Keys;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class KeyInscripcion implements Serializable {

    private KeyEvento evento;
    private String participante;

}
