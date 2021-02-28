package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Franja;
import com.example.aesparticipantes.Entities.Temporada;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FranjaRepository extends CrudRepository<Franja, Long> {

    List<Franja> findAllByTemporada(Temporada temporada);


}
