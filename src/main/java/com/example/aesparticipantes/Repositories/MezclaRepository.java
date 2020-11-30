package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Jornada;
import com.example.aesparticipantes.Entities.Mezcla;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MezclaRepository extends CrudRepository<Mezcla, Long> {

    List<Mezcla> findAllByJornadaAndCategoria(Jornada jornada, Categoria categoria);

}
