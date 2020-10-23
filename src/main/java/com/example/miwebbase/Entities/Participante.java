package com.example.miwebbase.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Table(name = "Participantes")
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Participante {


    @Id
    @Column(length = 75)
    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "participante")
    List<Tiempo> tiempos;

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Participante)) {
            return false;
        }

        Participante p = (Participante) o;

        return p.getNombre().equals(this.getNombre());
    }


}
