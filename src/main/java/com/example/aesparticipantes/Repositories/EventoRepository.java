package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Evento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends CrudRepository<Evento, Long> {

    Optional<Evento> findByCategoriaAndCompeticion(Categoria categoria, Competicion competicion);

    @EntityGraph(attributePaths = "inscripciones")
    List<Evento> findAllByCompeticionIn(List<Competicion> competiciones);

}
