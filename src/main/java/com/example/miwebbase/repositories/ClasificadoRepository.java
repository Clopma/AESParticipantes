package com.example.miwebbase.repositories;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Clasificado;
import com.example.miwebbase.Entities.Participante;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClasificadoRepository extends CrudRepository<Clasificado, Long> {


    @Query("from Clasificado c where c.categoria = ?1 and c.ronda = ?2")
    List<Clasificado> getRonda(Categoria categoria, String ronda);

    @Query("select c.categoria, c.participante, c.ronda from Clasificado c where c.ronda like 'MEDALLA_%' order by c.categoria.orden")
    List<Object[]> getMedallas();

    @Builder
    @Getter
    class Medalla {
        Categoria categoria;
        Participante participante;
        String ronda;
    }
}
