package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Categoria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends CrudRepository<Categoria, Long> {


    Categoria findByNombre(String nombre);

    List<Categoria> findAllByOrderByOrden();
}
