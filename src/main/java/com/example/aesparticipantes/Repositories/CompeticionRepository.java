package com.example.aesparticipantes.Repositories;


import com.example.aesparticipantes.Entities.Competicion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompeticionRepository extends CrudRepository<Competicion, Long> {


    Competicion findByNombre(String nombre);

    @Query("from Competicion c where c.inicio > current_timestamp")
    List<Competicion> findCompeticionesFuturas();



}
