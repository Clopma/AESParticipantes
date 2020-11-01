package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Inscripcion;
import com.example.aesparticipantes.Entities.Participante;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends CrudRepository<Inscripcion, Long> {


    List<Inscripcion> findAllByParticipante(Participante participante);
}
