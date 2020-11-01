package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Descalificacion;
import com.example.aesparticipantes.Entities.Evento;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescalificacionRepository extends CrudRepository<Descalificacion, Long> {

    List<Descalificacion> findAllByEvento(Evento evento);

}
