package com.example.aesparticipantes.Entities;


import com.example.aesparticipantes.Entities.Keys.KeyFranja;
import lombok.*;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "Franjas")
@IdClass(KeyFranja.class)
@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Configurable
public class Franja {

    @Id
    @NotNull
    @Column(length = 100)
    private String nombre;

    @Id
    @NotNull
    @ManyToOne
    private Temporada temporada;

    @NotNull
    private String dia;

    @NotNull
    private String horaInicio;

    @NotNull
    private String horaFin;

    @NotNull
    private String tipo;

}


