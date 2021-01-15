package com.example.aesparticipantes.Entities.Keys;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class KeyClasificacion implements Serializable {

    private String temporada;
    private String categoria;

}
