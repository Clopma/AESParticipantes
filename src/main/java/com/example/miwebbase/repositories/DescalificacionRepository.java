package com.example.miwebbase.repositories;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Descalificacion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescalificacionRepository extends CrudRepository<Descalificacion, Long> {


    List<Descalificacion> findAllByCategoria(Categoria categoria);

}
