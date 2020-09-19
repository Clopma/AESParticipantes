package com.example.miwebbase.repositories;

import com.example.miwebbase.Entities.Categoria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends CrudRepository<Categoria, Long> {


    Categoria findByNombre(String nombre);

    List<Categoria> findAllByOrderByOrden();
}
