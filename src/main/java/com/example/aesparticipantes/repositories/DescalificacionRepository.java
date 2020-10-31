package com.example.aesparticipantes.repositories;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Descalificacion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescalificacionRepository extends CrudRepository<Descalificacion, Long> {

    List<Descalificacion> findAllByCategoriaAndCompeticion(Categoria categoria, Competicion competicion);

}
