package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Evento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends CrudRepository<Evento, Long> {



    @Query("from Evento e where e.categoria.nombre = ?1 and e.competicion.nombre = ?2")
    Evento getEventoPorCategoriaYNombre(String nombreCategoria, String nombreCompeticion);

}
