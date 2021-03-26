package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ClasificadoRepository extends CrudRepository<Clasificado, Long> {


    @Query("from Clasificado c where c.evento = ?1 and c.ronda = ?2")
    List<Clasificado> getRonda(Evento evento, String ronda);

    @Query("from Clasificado c where c.clasificacion = ?1 and c.ronda = ?2")
    List<Clasificado> getRonda(Clasificacion clasificacion, String ronda);

    @Query("select cat, c.participante, c.ronda from Clasificado c join Categoria cat on c.evento.categoria = cat " +
            "where c.ronda like 'MEDALLA_%' and c.evento.competicion = ?1 order by cat.orden")
    List<Object[]> getMedallasCompeticion(String competicion);

    @Query("select cat, c.participante, c.ronda from Clasificado c join Categoria cat on c.clasificacion.categoria = cat " +
            "where c.ronda like 'MEDALLA_%' and c.clasificacion.temporada = ?1 order by cat.orden")
    List<Object[]> getMedallasTemporada(String temporada);

    @Query("select c.ronda from Clasificado c where c.participante = ?1 and c.evento = ?2 and c.ronda like 'MEDALLA_%'")
    List<Clasificado.NombreRonda> getRondasParticipante(Participante nombreParticipante, Evento evento);

    @Query("select c.ronda from Clasificado c where c.participante = ?1 and c.clasificacion = ?2 and c.ronda like 'MEDALLA_%'")
    List<Clasificado.NombreRonda> getRondasParticipante(Participante nombreParticipante, Clasificacion evento);


    List<Clasificado> findAllByEventoIn(Set<Evento> eventos);

    List<Clasificado> findAllByEvento(Evento eventos);

    @Query(value = "select * from Clasificados c where id  = ?1", nativeQuery = true)
    List<Clasificado> test(int a);


    @Builder
    @Getter
    class Medalla {
        Categoria categoria;
        Participante participante;
        String ronda;
    }
}
