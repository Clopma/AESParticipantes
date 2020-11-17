package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Clasificado;
import com.example.aesparticipantes.Entities.Evento;
import com.example.aesparticipantes.Entities.Participante;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClasificadoRepository extends CrudRepository<Clasificado, Long> {


    @Query("from Clasificado c where c.evento = ?1 and c.ronda = ?2")
    List<Clasificado> getRonda(Evento evento, String ronda);

    @Query("select cat, c.participante, c.ronda from Clasificado c join Categoria cat on c.evento.categoria = cat " +
            "where c.ronda like 'MEDALLA_%' and c.evento.competicion = ?1 order by cat.orden")
    List<Object[]> getMedallas(String competicion);

    @Query("select c.ronda from Clasificado c where c.participante = ?1 and c.evento = ?2 and c.ronda like 'MEDALLA_%'")
    List<Clasificado.NombreRonda> getRondasParticipante(Participante nombreParticipante, Evento evento);


    @Builder
    @Getter
    class Medalla {
        Categoria categoria;
        Participante participante;
        String ronda;
    }
}
