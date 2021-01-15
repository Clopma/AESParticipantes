package com.example.aesparticipantes.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class DivisionJornada {

    boolean nueva = false;
    List<ParticipanteModel> participantes = new ArrayList<>();

    public DivisionJornada(boolean nueva){
        this.nueva = nueva;
    }
    public DivisionJornada(List<ParticipanteModel> participantes, boolean nueva) {
        setParticipantes(participantes);
        this.nueva = nueva;
    }

    public DivisionJornada clone(){
        return new DivisionJornada(new ArrayList(participantes.stream().map(ParticipanteModel::clone).collect(Collectors.toList())), nueva);

    }


}
