package com.example.miwebbase.repositories;


import com.example.miwebbase.Entities.Competicion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompeticionRepository extends CrudRepository<Competicion, Long> {


    Competicion findByNombre(String nombre);



}
