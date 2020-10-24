package com.example.miwebbase.Entities;

import com.example.miwebbase.Models.Posicion;
import com.example.miwebbase.repositories.DescalificacionRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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


    public Map<Competicion, List<Posicion>> getPosicionesEnCompeticiones(DescalificacionRepository descalificacionRepository) {
        Map<Competicion, List<Posicion>> resultados = new HashMap<>();

        Set<Competicion> competicionesParticipadas = this.getTiempos().stream().collect(Collectors.groupingBy(Tiempo::getCompeticion)).keySet();
        competicionesParticipadas.forEach(competicion -> resultados.put(competicion, getPosicionesEnCompeticion(competicion, descalificacionRepository)));
        return resultados;
    }


    public List<Posicion> getPosicionesEnCompeticion(Competicion competicion, DescalificacionRepository descalificacionRepository) {

        List<Posicion> posiciones = new ArrayList<>();

       Set<Categoria> categoriasParticipadas = this.getTiempos().stream().filter(t -> t.getCompeticion().equals(competicion))
                    .collect(Collectors.groupingBy(Tiempo::getCategoria)).keySet();

               categoriasParticipadas.forEach(c -> posiciones.add(c.getRanking(competicion, descalificacionRepository).stream()
                       .filter(p -> p.getParticipante().equals(this)).findFirst().get()));

        return posiciones;
    }



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
