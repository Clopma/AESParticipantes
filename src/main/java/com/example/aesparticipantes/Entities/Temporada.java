package com.example.aesparticipantes.Entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;


@Table(name = "Temporadas")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Temporada {

    @Id
    private String nombre;

    @org.hibernate.annotations.OrderBy(clause = "jornadas")
    @OneToMany(mappedBy = "temporada")
    Set<Competicion> competiciones;

    @OneToMany(mappedBy = "temporada")
    List<Clasificacion> clasificaciones;


}
