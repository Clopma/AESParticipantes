package com.example.miwebbase.repositories;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Clasificado;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClasificadoRepository extends CrudRepository<Clasificado, Long> {


    @Query("from Clasificado c where c.categoria = ?1 and c.ronda = ?2 order by c.posicion")
    List<Clasificado> getRonda(Categoria categoria, String ronda);

}
