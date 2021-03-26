package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Models.InscripcionModel;
import com.example.aesparticipantes.Repositories.TiempoRepository;
import com.example.aesparticipantes.Seguridad.WCAGetResponse;
import com.example.aesparticipantes.Utils.AESUtils;
import lombok.*;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Table(name = "Participantes")
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Configurable
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

    private boolean confirmado;
    private boolean baneado;

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

    public Map<Temporada, List<Categoria>> getTemporadas(TiempoRepository tiempoRepository){
        //Lo siento, Carlos del futuro, por haber hecho esto en una linea.
        Map<Temporada, List<Categoria>> mapa = new HashMap<>();

        tiempoRepository.getTiemposParticipante(this) //Se obtienen tiempos del participante y todas las entidades que se van a usar (Nota 002)
                .stream().collect(Collectors.groupingBy(t -> t.getJornada().getCompeticion())).entrySet()// obtenemos un Map<Competicion, List<Tiempo>>
                .stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(Tiempo::getCategoria).collect(Collectors.toList()))) //Map<Competicion, List<Categoria>>
                .forEach((c, lcat) -> {if(mapa.get(c.getTemporada()) == null) { mapa.put(c.getTemporada(), lcat);} else { mapa.get(c.getTemporada()).addAll(lcat);}}); // Las metemos en un map

        mapa.remove(null); //Eliminamos la temporada null que contiene los datos de los tiempos en competiciones que no tenían temporada
        mapa.keySet().stream().forEach(k -> mapa.put(k, mapa.get(k).stream().distinct().collect(Collectors.toList()))); //Eliminamos duplicadas
        return mapa;
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
