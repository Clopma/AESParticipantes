package com.example.aesparticipantes.repositories;

import com.example.aesparticipantes.Entities.Inscripcion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionRepository extends CrudRepository<Inscripcion, Long> {


}
