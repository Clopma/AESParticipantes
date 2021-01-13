package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Seguridad.WCAGetResponse;
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

    @Column(unique = true)
    private String email;

    @Column
    private Boolean anuncioNuevaCompeticion;

    @Column
    private Boolean recordatorioComienzo;

    @Column
    private Boolean recordatorioJornadas;

    @Column
    private Boolean recordatorioInscripcion;

    @Column
    private Boolean recordatorioParticipar;

    private String linkPerfilWCA;
    private String nombreWCA;
    private String gender;
    private String pais;
    private Date fechaCreacionWCA;
    private Date fechaActualicazionWCA;
    private String urlImagenPerfil;
    private String urlImagenPerfilIcono;

    @Column(columnDefinition = "boolean default false") //TODO: necesario?
    private boolean confirmado;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "participante") //TODO: TERRIBLE N+1
    @Fetch(value = FetchMode.SUBSELECT)
    List<Tiempo> tiempos;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "participante")
    List<Inscripcion> inscripciones;


    public List<com.example.aesparticipantes.Models.Inscripcion> getInscripcionesParticipadasYNoParticipadasEnCompeticion(Competicion competicion, List<Categoria> categoriasParticipadas) {

        //TODO: Un poco raro este Model, ahora podemos usar un inscripción y un tiempo
       List<com.example.aesparticipantes.Models.Inscripcion> inscripciones = getInscripciones().stream().filter(i -> i.getEvento().getCompeticion().equals(competicion))
                .map(i -> com.example.aesparticipantes.Models.Inscripcion.builder().categoria(i.getEvento().getCategoria()).build()).collect(Collectors.toList());

        inscripciones.forEach(i -> {
            i.setParticipado(categoriasParticipadas.contains(i.getCategoria()));
        });

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


    public void setWCAData(WCAGetResponse perfilWCA) {
        setWcaId(perfilWCA.getMe().getWca_id());
        setNombreWCA(perfilWCA.getMe().getName());
        setFechaActualicazionWCA(perfilWCA.getMe().getUpdated_at());
        setFechaCreacionWCA(perfilWCA.getMe().getCreated_at());
        setGender(perfilWCA.getMe().getGender());
        setLinkPerfilWCA(perfilWCA.getMe().getUrl());
        setPais(perfilWCA.getMe().getCountry_iso2());
        setUrlImagenPerfil(perfilWCA.getMe().getAvatar().getUrl());
        setUrlImagenPerfilIcono(perfilWCA.getMe().getAvatar().getThumb_url());
    }
}
