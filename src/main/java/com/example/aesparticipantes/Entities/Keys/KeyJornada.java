package com.example.aesparticipantes.Entities.Keys;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class KeyJornada implements Serializable {

    private String competicion;
    private int numeroJornada;
}
