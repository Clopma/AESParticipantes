package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Participante;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends CrudRepository<Categoria, Long> {


    Optional<Categoria> findByNombre(String nombre);


    @Query("select distinct t.categoria from Tiempo t join Jornada j on t.jornada = j join Competicion c on j.competicion = c where c = ?1 and t.participante = ?2")
    List<Categoria> getCategoriasParticipadas(Competicion competicion, Participante participante);


    List<Categoria> findAllByOrderByOrden();
}
