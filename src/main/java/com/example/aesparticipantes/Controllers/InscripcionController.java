package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.*;
import com.example.aesparticipantes.Repositories.CompeticionRepository;
import com.example.aesparticipantes.Repositories.InscripcionRepository;
import com.example.aesparticipantes.Repositories.ParticipanteRepository;
import com.example.aesparticipantes.Seguridad.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class InscripcionController {

    @Autowired
    ParticipanteRepository participanteRepository;

    @Autowired
    InscripcionRepository inscripcionRepository;

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    InscripcionController self;

    @ResponseBody
    @PostMapping("/inscripcion/{nombreParticipante}/{nombreCompeticion}")
    public ResponseEntity<String> inscribir(@PathVariable("nombreParticipante") String nombreParticipante, @PathVariable("nombreCompeticion") String nombreCompeticion, @RequestBody List<String> categorias, Principal principal) {


        if (principal instanceof UserData) {
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            Participante yo = participanteRepository.findByNombre(nombreParticipanteGuardado);//TODO: Es posible no cargar todos los tiempos de cada evento? En debug tarda, mala señal?
            Competicion competicion = competicionRepository.findByNombre(nombreCompeticion);
            if(competicion.getInicio().before(new Date())){
                return new ResponseEntity<>("No puedes inscribirte en una competición que ya ha comenzado, de hecho, " +
                        "que puedas leer esto es un error, por favor, comunícaselo al desarrollador.", HttpStatus.UNAUTHORIZED);
            }

            if (yo == null || !nombreParticipante.equals(yo.getNombre())) {
                return new ResponseEntity<>("Usuario o competición incorrectos. Warning.", HttpStatus.UNAUTHORIZED); //TODO: Log warning
            } else {

                List<Inscripcion> inscripcionesACampeonato = inscripcionRepository.getInscripcionesDeParticipanteEnCompeticion(yo.getNombre(), competicion.getNombre());

                List<Inscripcion> nuevasInscripciones = categorias.stream()
                        .map(c -> Inscripcion.builder()
                        .evento(Evento.builder()
                                .categoria(Categoria.builder().nombre(c).build())
                                .competicion(competicion).build())
                        .participante(yo)
                        .fechaInscripcion(new Date())
                        .build()
                ).collect(Collectors.toList());

                inscripcionRepository.deleteAll(inscripcionesACampeonato);
                inscripcionRepository.saveAll(nuevasInscripciones);
                self.evictFuturasCompeticiones();


                if(inscripcionesACampeonato.size() == 0){
                    if(nuevasInscripciones.size() == 0){
                        return new ResponseEntity<>("No te has inscrito a ninguna categoría, ¡anímate y selecciona al menos una! ", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Te has inscrito a la competición, ¡mucha suerte!", HttpStatus.OK);
                    }
                } else {
                    if(nuevasInscripciones.size() == 0){
                        return new ResponseEntity<>("Ya no estás inscrito a este campeonato. ¡Nos vemos a la próxima!", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Los cambios se han guardado, ¡a por todas!", HttpStatus.OK);
                    }

                }


            }
        } else {
            return new ResponseEntity<>("Este no es tu usuario o estás deslogeado. Warning.", HttpStatus.UNAUTHORIZED); //TODO: Log warning
        }


    }

    @CacheEvict(value = "competicionesFuturas")
    public void evictFuturasCompeticiones(){}


}
