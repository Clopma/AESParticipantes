package com.example.aesparticipantes.Entities.Keys;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class KeyTiempo implements Serializable {

    private KeyJornada jornada;
    private String participante;
    private String categoria;

}
