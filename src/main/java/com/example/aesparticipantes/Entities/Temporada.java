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
    Set<Competicion> competiciones; //TODO: list?

    @OneToMany(mappedBy = "temporada")
    List<Clasificacion> clasificaciones;




    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Temporada)) {
            return false;
        }

        Temporada t = (Temporada) o;

        return t.getNombre().equals(this.getNombre());
    }

}
