package com.example.aesparticipantes.Models;

import com.example.aesparticipantes.Entities.Participante;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ParticipanteModel {

    public String nombre;

    /* Divisiones */
    public boolean asciende;
    public boolean desciende;

    public ParticipanteModel(Participante p){
        this.nombre = p.getNombre();
    }

    //Por comodidad en las divisiones, el clon de un participante no tiene ascensos ni descensos
    public ParticipanteModel clone(){
        return new ParticipanteModel(nombre, asciende, desciende);

    }

}
