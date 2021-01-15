package com.example.aesparticipantes.Models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DivisionesEnJornada {

    List<DivisionJornada> divisiones = new ArrayList<>();

    public DivisionesEnJornada clone(){
       DivisionesEnJornada copia = new DivisionesEnJornada();
       copia.setDivisiones(new ArrayList(divisiones.stream().map(DivisionJornada::clone).collect(Collectors.toList())));
       return copia;
    }


}
