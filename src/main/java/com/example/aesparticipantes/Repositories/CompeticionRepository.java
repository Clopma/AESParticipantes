package com.example.aesparticipantes.Repositories;


import com.example.aesparticipantes.Entities.Competicion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompeticionRepository extends CrudRepository<Competicion, Long> {


    Optional<Competicion> findByNombre(String nombre);

    @Query("from Competicion c join Jornada j on c = j.competicion group by c having min(j.fechaInicio) > current_timestamp")
    List<Competicion> findCompeticionesFuturas();

    @Query("from Competicion c join Jornada j on c = j.competicion group by c having min(j.fechaInicio) < current_timestamp and c.limiteInscripciones > current_timestamp")
    List<Competicion> findCompeticionesPresentesConInscripcionesAbiertas();

}
