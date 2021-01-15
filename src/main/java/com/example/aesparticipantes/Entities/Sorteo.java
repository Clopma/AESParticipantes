package com.example.aesparticipantes.Entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "Sorteos")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Sorteo {

    @Id
    private String id;

    private String descripcion;

    private String imagen;

    private String link;


}
