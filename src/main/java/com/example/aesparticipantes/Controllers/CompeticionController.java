package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Repositories.CompeticionRepository;
import com.example.aesparticipantes.Seguridad.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Controller
public class CompeticionController {

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    ParticipanteController participanteController;

    @Autowired
    CompeticionController self;


    @GetMapping("/competicion/{nombreCompeticion}")
    public String showForm(Model model, @PathVariable("nombreCompeticion") String nombreCompeticion, Principal principal) {
        Optional<Competicion> competicion = competicionRepository.findByNombre(nombreCompeticion); //TODO: Maybe cachear

        if(!competicion.isPresent()){
            return "error/404";
        }

        if (principal instanceof UserData) { //TODO: Repetido en inscripción y participar: refactor
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            Participante yo = participanteController.getParticipante(nombreParticipanteGuardado);
            model.addAttribute("participanteLogeado", yo);
        }

        model.addAttribute("jornadaActiva", competicion.get().getJornadaActiva());
        model.addAttribute("competicion", competicion.get());
        model.addAttribute("inscripciones", self.getCategoriasInscritasPorParticipante(competicion.get()));
        return "competicion";
    }

    @Cacheable("categoriasParticipantesMap")
    public TreeMap<Participante, Map<String, Categoria>> getCategoriasInscritasPorParticipante(Competicion competicion){
     return competicion.getCategoriasInscritasPorParticipanteMap();
    }

    @Cacheable(value = "competiciones")
    public Optional<Competicion> getCompeticion(String nombreCompeticion){
        return competicionRepository.findByNombre(nombreCompeticion);
    }

}
