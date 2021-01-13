package com.example.aesparticipantes.Repositories;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Inscripcion;
import com.example.aesparticipantes.Entities.Participante;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends CrudRepository<Inscripcion, Long> {

    @Query("from Inscripcion i where i.participante.nombre = ?1 and i.evento.competicion = ?2")
    List<Inscripcion> getInscripcionesDeParticipanteEnCompeticion(String nombreParticipante, String nombreCompeticion);

    Optional<Inscripcion> findByEvento_CompeticionAndEvento_CategoriaAndParticipante(Competicion competicion, Categoria categoria, Participante participante);



}
