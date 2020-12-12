package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.*;
import com.example.aesparticipantes.Repositories.CompeticionRepository;
import com.example.aesparticipantes.Repositories.InscripcionRepository;
import com.example.aesparticipantes.Repositories.ParticipanteRepository;
import com.example.aesparticipantes.Seguridad.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Optional;
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

    Logger logger = LoggerFactory.getLogger(InscripcionController.class);

    @ResponseBody
    @PostMapping("/inscripcion/{nombreCompeticion}")
    public ResponseEntity<String> inscribir(@PathVariable("nombreCompeticion") String nombreCompeticion, @RequestBody List<String> categorias, Principal principal) {


        if (principal instanceof UserData) {
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            Participante yo = participanteRepository.findByNombre(nombreParticipanteGuardado); //TODO: Es posible no cargar todos los tiempos de cada evento? En debug tarda, mala señal?
            Optional<Competicion> competicion = competicionRepository.findByNombre(nombreCompeticion);

            if(!competicion.isPresent()){
                logger.error("Inscripción falloda,  la competición no existe: " + nombreCompeticion);
                return new ResponseEntity<>("Esta competición no existe. Esto debe de ser un error.", HttpStatus.NOT_FOUND);
            }

            if(competicion.get().isEmpezada()){
                logger.error("Inscripción falloda,  competición ya comenzada: " + nombreCompeticion);
                return new ResponseEntity<>("No puedes inscribirte en una competición que ya ha comenzado, a no ser que hayas abierto esta página antes de que comenzara pero pulsado el botón después, " +
                        "ha ocurrido un error, por favor, comunícaselo al desarrollador.", HttpStatus.UNAUTHORIZED);
            }

            if (yo == null) {
                logger.error("Usuario no logueado intentando inscribirse a " +categorias.toString());
                return new ResponseEntity<>("Usuario incorrecto. Warning.", HttpStatus.UNAUTHORIZED);
            } else {

                List<Inscripcion> inscripcionesACampeonato = inscripcionRepository.getInscripcionesDeParticipanteEnCompeticion(yo.getNombre(), competicion.get().getNombre());

                List<Inscripcion> nuevasInscripciones = categorias.stream()
                        .map(c -> Inscripcion.builder()
                        .evento(Evento.builder()
                                .categoria(Categoria.builder().nombre(c).build())
                                .competicion(competicion.get()).build())
                        .participante(yo)
                        .fechaInscripcion(new Date())
                        .build()
                ).collect(Collectors.toList()); //TODO: Controlar que una categoría no exista

                inscripcionRepository.deleteAll(inscripcionesACampeonato);
                inscripcionRepository.saveAll(nuevasInscripciones);
                
                self.evictFuturasCompeticiones();
                self.evictParticipante(yo.getNombre());


                if(inscripcionesACampeonato.size() == 0){
                    if(nuevasInscripciones.size() == 0){
                        logger.warn("No te has inscrito a ninguna categoría: " + categorias.toString() + " " + yo.getNombre());
                        return new ResponseEntity<>("No te has inscrito a ninguna categoría, ¡anímate y selecciona al menos una! ", HttpStatus.OK);
                    } else {
                        logger.info("Participante inscrito." + categorias.toString() + " " + yo.getNombre());
                        return new ResponseEntity<>("Te has inscrito a la competición, ¡mucha suerte!", HttpStatus.OK);
                    }
                } else {
                    if(nuevasInscripciones.size() == 0){
                        logger.warn("Participante desinscrito." + categorias.toString() + " " + yo.getNombre());
                        return new ResponseEntity<>("Ya no estás inscrito a este campeonato. ¡Nos vemos a la próxima!", HttpStatus.OK);
                    } else {
                        logger.info("Un participante ha cambiado sus inscripciones: " + categorias.toString() + " " + yo.getNombre());
                        return new ResponseEntity<>("Los cambios se han guardado, ¡a por todas!", HttpStatus.OK);
                    }

                }


            }
        } else {
            logger.error("Usuario no logueado intentando inscribirse a " + categorias.toString());
            return new ResponseEntity<>("Este no es tu usuario o estás deslogeado. Warning.", HttpStatus.UNAUTHORIZED);
        }


    }

    @CacheEvict(value = "competicionesFuturas")
    public void evictFuturasCompeticiones(){}

    @CacheEvict(value = "participantes", key = "#nombreParticipante")
    public void evictParticipante(String nombreParticipante){}


}
