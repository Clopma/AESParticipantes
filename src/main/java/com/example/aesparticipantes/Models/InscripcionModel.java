package com.example.aesparticipantes.Models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class InscripcionModel {

    String nombreCategoria;
    boolean comenzado;
    boolean finalizado;

}
