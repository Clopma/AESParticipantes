package com.example.aesparticipantes.Models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PosicionTemporada {
    String categoria;
    int division;
    int posicionEnDivision;
}
