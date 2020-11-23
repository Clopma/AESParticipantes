package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Jornada;
import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Entities.Tiempo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TiempoRepository extends CrudRepository<Tiempo, Long> {


    @Query("from Tiempo t where t.jornada = ?1 and t.participante = ?2")
    List<Tiempo> getTiemposEnJornada(Jornada jornada, Participante participante);

}
