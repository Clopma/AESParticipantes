package com.example.miwebbase.Entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "Categorias")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {


    @Id
    private String nombre;

    @NotNull
    private int numTiempos;

    @NotNull
    private int orden;

    @NotNull
    private Integer cortePlayOffs;

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


