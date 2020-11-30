package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Utils.AESUtils;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Table(name = "Participantes")
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Participante implements Comparable {


    @Id
    @Column(length = 75)
    private String nombre;

    @Column(unique = true)
    private String wcaId;

    private String linkPerfilWCA;
    private String nombreWCA;
    private String gender;
    private String pais;
    private Date fechaCreacionWCA;
    private Date fechaActualicazionWCA;
    private String urlImagenPerfil;
    private String urlImagenPerfilIcono;

    @Column(columnDefinition = "boolean default false")
    private boolean confirmado;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "participante")
    @Fetch(value = FetchMode.SUBSELECT)
    List<Tiempo> tiempos;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "participante")
    List<Inscripcion> inscripciones;


    public List<com.example.aesparticipantes.Models.Inscripcion> getInscripcionesParticipadasYNoParticipadasEnCompeticion(Competicion competicion, List<Categoria> categoriasParticipadas) {
       List<com.example.aesparticipantes.Models.Inscripcion> inscripciones = getInscripciones().stream().filter(i -> i.getEvento().getCompeticion().equals(competicion))
                .map(i -> com.example.aesparticipantes.Models.Inscripcion.builder().categoria(i.getEvento().getCategoria()).build()).collect(Collectors.toList());

        inscripciones.forEach(p -> p.setParticipado(categoriasParticipadas.contains(p.getCategoria())));

        return inscripciones;

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

    @Override
    public int compareTo(Object o) {
        return  AESUtils.COLLATOR.compare(this.getNombre(), ((Participante) o).getNombre());
    }


}
