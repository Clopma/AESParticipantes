package com.example.miwebbase.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = Participante.T_PARTICIPANTES)
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Participante {

    public static final String T_PARTICIPANTES = "Participantes";

    @Id
    private String nombre;



}
