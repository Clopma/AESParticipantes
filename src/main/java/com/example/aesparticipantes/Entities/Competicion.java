package com.example.aesparticipantes.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table(name = "Competiciones")
//@IdClass(KeyCompeticion.class)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Competicion {

    @Id
    @Column(length = 25)
    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competicion")
    private List<Evento> eventos;

    private Date inicio;

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
