package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Evento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JornadaRepository extends CrudRepository<Evento, Long> {

}
