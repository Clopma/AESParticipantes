package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Models.InscripcionModel;
import com.example.aesparticipantes.Seguridad.WCAGetResponse;
import com.example.aesparticipantes.Utils.AESUtils;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @OneToMany(mappedBy = "participante")
    List<Tiempo> tiempos;

    @org.hibernate.annotations.OrderBy(clause = "categoria asc")
    @OneToMany(mappedBy = "participante")
    Set<Inscripcion> inscripciones;


    public List<InscripcionModel> getInscripcionesParticipadasYNoParticipadasEnCompeticion(Competicion competicion, List<Inscripcion> inscripciones, List<Tiempo> tiemposParticipadosEnJornada) {

        return inscripciones.stream().map(i -> {

           Optional<Tiempo> tiempo = tiemposParticipadosEnJornada.stream().filter(t -> t.getCategoria().equals(i.getEvento().getCategoria())).findAny();

           return InscripcionModel.builder()
                   .nombreCategoria(i.getEvento().getCategoria().getNombre())
                   .comenzado(tiempo.isPresent())
                   .finalizado(tiempo.isPresent() && !tiempo.get().isEnCurso())
                   .build();
       })
               .collect(Collectors.toList());

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
