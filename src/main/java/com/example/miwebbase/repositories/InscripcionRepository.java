package com.example.miwebbase.repositories;

import com.example.miwebbase.Entities.Inscripcion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionRepository extends CrudRepository<Inscripcion, Long> {


}
