package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Participante;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipanteRepository extends RefreshableRepository<Participante, Long> { //TODO Necesario?????????????

    @Query("select p.nombre from Participante p order by p.nombre")
    List<String> getNombres();

    Participante findByWcaId(String wcaId);

    Participante findByNombre(String nombre);



}
