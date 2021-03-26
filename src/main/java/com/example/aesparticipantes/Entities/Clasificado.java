package com.example.aesparticipantes.Entities;

import lombok.*;

import javax.persistence.*;

@Table(name = "Clasificados", uniqueConstraints= {
        @UniqueConstraint(columnNames = {"participante_nombre", "evento_categoria_nombre", "evento_competicion_nombre", "ronda"}),
        @UniqueConstraint(columnNames = {"participante_nombre", "clasificacion_temporada_nombre", "clasificacion_categoria_nombre", "ronda"})
})
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Clasificado {

    @Id
    @Column(columnDefinition = "integer auto_increment")
    int id;

    @ManyToOne()
    private Evento evento;

    @ManyToOne()
    private Clasificacion clasificacion;

    @ManyToOne()
    private Participante participante;

    @Column(length = 25)
    private String ronda;

    @Transient
    private int posicion;

    @Transient
    private boolean victoria;

    @Transient
    private String medalla;

    @Column
    private String puntuacion;

    public enum NombreRonda {
        CUARTO,
        SEMIFINAL,
        FINAL,
        GANADOR,
        MEDALLA_ORO,
        MEDALLA_PLATA,
        MEDALLA_BRONCE
    }
}
