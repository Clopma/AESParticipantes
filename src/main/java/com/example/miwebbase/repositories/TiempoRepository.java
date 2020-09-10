package com.example.miwebbase.repositories;

import com.example.miwebbase.Entities.Tiempo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TiempoRepository extends CrudRepository<Tiempo, Long> {

    @Query("from Tiempo where participante.nombre = ?1")
    List<Tiempo> getTiemposOfParticipante(String nombreParticipante);

}
