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
        Competicion competicion = competicionRepository.findByNombre(nombreCompeticion);

        if (principal instanceof UserData) { //TODO: Repetido en inscripción: refactor
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            Participante yo = participanteController.getParticipante(nombreParticipanteGuardado);
            model.addAttribute("participanteLogeado", yo);
        }

        model.addAttribute("competicion", competicion);
        model.addAttribute("inscripciones", self.getCategoriasInscritasPorParticipante(competicion));
        return "competicion";
    }

    @Cacheable("categoriasParticipantesMap")
    public TreeMap<Participante, Map<String, Categoria>> getCategoriasInscritasPorParticipante(Competicion competicion){
     return competicion.getCategoriasInscritasPorParticipanteMap();
    }

}
