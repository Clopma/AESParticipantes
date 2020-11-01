package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Evento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends CrudRepository<Evento, Long> {

    Evento findByCategoriaNombreAndCompeticionNombre(String nombreCategoria, String nombreCompeticion);

}
