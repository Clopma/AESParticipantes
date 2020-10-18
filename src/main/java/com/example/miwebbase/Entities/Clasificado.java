package com.example.miwebbase.Entities;

import com.example.miwebbase.Entities.Keys.KeyClasificado;
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
    @ManyToOne
    private Competicion competicion;

    @Id
    @ManyToOne
    private Categoria categoria;

    @Id
    @Column(length = 25)
    private String ronda;

    @Id
    @ManyToOne
    private Participante participante;

    @Transient
    private int posicion;

    @Transient
    private boolean victoria;

    @Column
    private String puntuacion;

    public enum NombreRonda {
        CUARTO,
        SEMIFINAL,
        FINAL,
        GANADOR
    }
}
