package com.example.miwebbase.Entities;


import com.example.miwebbase.Models.Posicion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Table(name = "Categorias")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {


    @Id
    @Column(length = 25)
    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoria")
    List<Tiempo> tiempos;

    @NotNull
    private int numTiempos;

    @NotNull
    private int orden;

    @NotNull
    private Integer cortePlayOffs;

    @Transient
    Map<Competicion, List<Posicion>> rankings;

    public void refrescarRanking(Competicion competicion, boolean forzar) {

        if(forzar || rankings.get(competicion) == null){
            List<Tiempo> tiemposRanking = tiempos.stream().filter(t -> t.getCompeticion().equals(competicion)).collect(Collectors.toList());

        }

    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Categoria)) {
            return false;
        }

        Categoria c = (Categoria) o;

        return c.getNombre().equals(this.getNombre());
    }


}


