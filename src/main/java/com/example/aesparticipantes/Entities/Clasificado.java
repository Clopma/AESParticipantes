package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Keys.KeyClasificado;
import lombok.*;

import javax.persistence.*;

@Table(name = "Clasificados")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(KeyClasificado.class)
public class Clasificado {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Evento evento;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Participante participante;

    @Id
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
