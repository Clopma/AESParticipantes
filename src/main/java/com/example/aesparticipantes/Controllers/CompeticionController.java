package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Repositories.CompeticionRepository;
import com.example.aesparticipantes.Repositories.ParticipanteRepository;
import com.example.aesparticipantes.Seguridad.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.Optional;

@Controller
public class CompeticionController {

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    ParticipanteController participanteController;

    @Autowired
    ParticipanteRepository participanteRepository;

    @Autowired
    CompeticionController self;


    @GetMapping("/competicion/{nombreCompeticion}")
    public String showForm(Model model, @PathVariable("nombreCompeticion") String nombreCompeticion, Principal principal) {
        Optional<Competicion> competicion = competicionRepository.findByNombre(nombreCompeticion); //TODO: Maybe cachear

        if(!competicion.isPresent()){
            model.addAttribute("mensaje", "No hay ninguna competición llamada "+ nombreCompeticion +".");
            return "error/404";
        }

        if (principal instanceof UserData) { //TODO: Repetido en inscripción y participar: refactor
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            Participante yo = participanteRepository.findByNombre(nombreParticipanteGuardado).get();
            model.addAttribute("participanteLogeado", yo);
        }

        model.addAttribute("jornadaActiva", competicion.get().getJornadaActiva());
        model.addAttribute("competicion", competicion.get());
        model.addAttribute("inscripciones", competicion.get().getCategoriasInscritasPorParticipanteMap());
        model.addAttribute("temporada", competicion.get().getTemporada());
        return "competicion";
    }




}
