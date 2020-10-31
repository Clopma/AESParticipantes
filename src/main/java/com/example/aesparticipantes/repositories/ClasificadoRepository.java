package com.example.aesparticipantes.repositories;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Clasificado;
import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Participante;
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

    @Query("select c.categoria, c.participante, c.ronda from Clasificado c where c.ronda like 'MEDALLA_%' and c.competicion.nombre = ?1 order by c.categoria.orden")
    List<Object[]> getMedallas(String nombreCompeticion);

    @Query("select c.ronda from Clasificado c where c.competicion = ?1 and c.participante = ?2 and c.categoria = ?3 and c.ronda like 'MEDALLA_%'")
    List<Clasificado.NombreRonda> getRondasParticipante(Competicion nombreCompeticion, Participante nombreParticipante, Categoria nombreCategoria);


    @Builder
    @Getter
    class Medalla {
        Categoria categoria;
        Participante participante;
        String ronda;
    }
}
