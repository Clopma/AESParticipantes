package com.example.miwebbase.repositories;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Models.PuntuacionIndividual;
import com.example.miwebbase.Models.PuntuacionTotal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TiempoRepository extends CrudRepository<Tiempo, Long> {

    @Query("from Tiempo where participante.nombre = ?1")
    List<Tiempo> getTiemposOfParticipante(String nombreParticipante);

    @Query("select new com.example.miwebbase.Models.PuntuacionTotal(" +
            "participante.nombre, SUM(puntosBonus + puntosTiempo)) from Tiempo " +
            "where categoria = ?1 " +
            "group by participante.nombre " +
            "order by SUM(puntosBonus + puntosTiempo) desc")
    List<PuntuacionTotal> getParticipantesPuntosTotalesCategoria(Categoria categoria);


    @Query("select new com.example.miwebbase.Models.PuntuacionIndividual(" +
            "participante.nombre, jornada, puntosBonus + puntosTiempo, true) from Tiempo " +
            "where categoria = ?1 " +
            "order by jornada")
    List<PuntuacionIndividual> getParticipantesPuntosIndividualesCategoria(Categoria categoria);

    List<Tiempo> findAllByCategoriaAndJornadaOrderByPosicion(Categoria categoria, int numeroJornada);


    @Query("select new com.example.miwebbase.Models.PuntuacionIndividual(" +
            "participante.nombre, jornada, puntosTiempo + puntosBonus, true) from Tiempo " +
            "where categoria = ?1 " +
            "AND participante.nombre = ?2 " +
            "order by jornada")
    List<PuntuacionIndividual> getParticipantePuntosIndividualesCategoria(Categoria categoria, String nombreParticipante);


    @Query( "from Tiempo " +
            "where categoria = ?1 " +
            "AND participante.nombre IN ?2 ")
    List<Tiempo> getTiemposDeVariosParticipantes(Categoria categoria, Set<String> participantes);
}
