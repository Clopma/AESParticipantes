package com.example.miwebbase.repositories;

import com.example.miwebbase.Entities.Tiempo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiempoRepository extends CrudRepository<Tiempo, Long> {


}
