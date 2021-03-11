package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Evento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends CrudRepository<Evento, Long> {

    Evento findByCategoriaAndCompeticion(Categoria nombreCategoria, Competicion nombreCompeticion);

    @EntityGraph(attributePaths = "inscripciones")
    List<Evento> findAllByCompeticionIn(List<Competicion> competiciones);

}
