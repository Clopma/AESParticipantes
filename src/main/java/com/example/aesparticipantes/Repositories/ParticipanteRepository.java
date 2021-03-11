package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Participante;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipanteRepository extends CrudRepository<Participante, Long> {

    @Query("select p.nombre from Participante p order by p.nombre")
    List<String> getNombres();

    Optional<Participante> findByWcaId(String wcaId);

    // inscripciones y jornadas son dos bags relacionadas por lo que no causarán producto cartesiano con los Sets
    // https://stackoverflow.com/questions/4334970/hibernate-throws-multiplebagfetchexception-cannot-simultaneously-fetch-multipl/51055523?stw=2#comment116263441_51055523
    @EntityGraph(attributePaths = {"inscripciones.evento.competicion.jornadas.tiempos.categoria",
                                    "inscripciones.evento.competicion.jornadas.tiempos.participante"}, type = EntityGraph.EntityGraphType.LOAD)
    // Faltan por fetchear clasificados y desclasificaciones pero me doy por satisfecho
    @Query("from Participante where nombre = ?1")
    Optional<Participante> findByNombreParaPerfil(String nombre);



    Optional<Participante> findByNombre(String nombre);

}
