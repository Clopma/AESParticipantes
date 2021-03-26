package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Clasificacion;
import com.example.aesparticipantes.Entities.Temporada;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClasificacionRepository extends CrudRepository<Clasificacion, Long> {


    Optional<Clasificacion> findByCategoriaAndTemporada(Categoria categoria, Temporada temporada);

}
