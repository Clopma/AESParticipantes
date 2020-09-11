package com.example.miwebbase.repositories;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Tiempo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends CrudRepository<Categoria, Long> {
    @Query("select nombre from Categoria")
    public List<String> getAllNames();
}
