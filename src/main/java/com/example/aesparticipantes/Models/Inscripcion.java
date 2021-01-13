package com.example.aesparticipantes.Models;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Tiempo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class Inscripcion {

    Categoria categoria;
    boolean participado;
    Tiempo tiempo;

}
