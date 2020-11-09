package com.example.aesparticipantes.Entities;

import lombok.*;

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
@Setter
public class Competicion {

    @Id
    @Column(length = 25)
    private String nombre;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "competicion")
    private List<Evento> eventos;

    private Date inicio;

    private int numJornadas;

    public boolean isParticipanteInscrito(Participante p){
        return eventos.stream().anyMatch(i -> i.isParticipanteInscrito(p));
    }

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
