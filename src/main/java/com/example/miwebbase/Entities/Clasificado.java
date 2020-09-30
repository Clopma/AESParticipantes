package com.example.miwebbase.Entities;

import com.example.miwebbase.Entities.Keys.KeyClasificado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "Clasificados")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(KeyClasificado.class)
public class Clasificado {

    @Id
    @ManyToOne
    private Categoria categoria;

    @Id
    private String ronda;

    @Id
    @ManyToOne
    private Participante participante;

    @Id
    private int posicion;

    private boolean victoria;

    public enum NombreRonda {
        SEMIFINAL,
        FINAL
    }
}
