package com.example.aesparticipantes.Entities.Keys;

import com.example.aesparticipantes.Entities.Temporada;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class KeyFranja implements Serializable {

    private String nombre;
    private Temporada temporada;

}
