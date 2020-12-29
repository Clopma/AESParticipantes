package com.example.aesparticipantes.Entities;


import com.example.aesparticipantes.Entities.Keys.KeyMezcla;
import lombok.*;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "Mezclas")
@Entity
@Setter
@Getter
@IdClass(KeyMezcla.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Configurable
public class Mezcla {

    @Id
    @ManyToOne
    private Categoria categoria;

    @Id
    @ManyToOne
    private Jornada jornada;

    @Id
    private int numTiempo;

    @NotNull
    @Column(length = 1000)
    private String texto;

    @NotNull
//    @Column(unique = true) //TODO: Testing las repite :/
    private String imagenUrl;

}


