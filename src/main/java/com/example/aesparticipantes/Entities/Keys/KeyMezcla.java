package com.example.aesparticipantes.Entities.Keys;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class KeyMezcla implements Serializable{

    private String categoria;
    private KeyJornada jornada;
    private int numTiempo;

}
