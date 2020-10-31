package com.example.aesparticipantes.repositories;

import com.example.aesparticipantes.Entities.Tiempo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiempoRepository extends CrudRepository<Tiempo, Long> {


}
