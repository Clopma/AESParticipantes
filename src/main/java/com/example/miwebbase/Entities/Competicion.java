package com.example.miwebbase.Entities;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Table(name = "Competiciones")
@Entity
@Getter
public class Competicion {

    @Id
    @Column(length = 25)
    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competicion")
    private List<Tiempo> tiempos;

    private int numJornadas;

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Competicion)) {
            return false;
        }

        Competicion c = (Competicion) o;

        return c.getNombre().equals(this.getNombre());
    }
}
