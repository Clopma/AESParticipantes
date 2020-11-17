package com.example.aesparticipantes.Entities;


import lombok.*;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "Categorias")
@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Configurable
public class Categoria {


    @Id
    @Column(length = 25)
    private String nombre;

    @NotNull
    private int numTiempos;

    @NotNull
    private int orden;

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


