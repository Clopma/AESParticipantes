package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Temporada;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemporadaRepository extends CrudRepository<Temporada, Long> {


    @EntityGraph(attributePaths = "competiciones.jornadas")
    Optional<Temporada> findByNombre(String nombre);

}
