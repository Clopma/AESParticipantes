package com.example.aesparticipantes.Entities.Keys;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SorteoCompeticionKey implements Serializable {

    private String competicion;
    private String sorteo;

}
