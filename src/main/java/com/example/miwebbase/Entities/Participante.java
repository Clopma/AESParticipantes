package com.example.miwebbase.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = Participante.T_PARTICIPANTES)
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Participante {

    public static final String T_PARTICIPANTES = "Participantes";

    @Id
    private String nombre;

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
