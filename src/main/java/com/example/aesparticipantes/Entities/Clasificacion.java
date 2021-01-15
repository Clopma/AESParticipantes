package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Keys.KeyClasificacion;
import lombok.*;

import javax.persistence.*;

@Table(name = "Clasificaciones")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@IdClass(KeyClasificacion.class)
@Setter
public class Clasificacion {

    @Id
    @ManyToOne
    Temporada temporada;

    @Id
    @ManyToOne
    Categoria categoria;

    int tamanoDivisiones;
}
