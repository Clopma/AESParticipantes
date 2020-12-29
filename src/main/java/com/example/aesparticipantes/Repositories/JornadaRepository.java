package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Jornada;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface JornadaRepository extends CrudRepository<Jornada, Long> {


    @Query("from Jornada j where j.fechaFin > current_timestamp order by j.fechaFin")
    List<Jornada> getProximasJornadasEnFinalizar();

    Jornada findByCompeticionAndNumeroJornada(Competicion competicion, int numJornada);


}
