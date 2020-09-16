package com.example.miwebbase.repositories;

import com.example.miwebbase.Entities.Participante;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipanteRepository extends CrudRepository<Participante, Long> {

    @Query("select nombre from Participante")
    List<String> getAllNames();


}
