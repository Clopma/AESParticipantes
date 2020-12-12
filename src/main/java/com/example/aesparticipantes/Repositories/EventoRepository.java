package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Evento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends CrudRepository<Evento, Long> {



    @Query("from Evento e join Categoria c on e.categoria = c where e.competicion = ?1 order by c.orden")
    List<Evento> getEventosDeCompeticionPorOrdenDeCategoria(Competicion nombreCompeticion);

    Evento findByCategoriaAndCompeticion(Categoria nombreCategoria, Competicion nombreCompeticion);

}
