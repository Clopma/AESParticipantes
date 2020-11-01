package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Evento;
import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Entities.Tiempo;
import com.example.aesparticipantes.Models.Posicion;
import com.example.aesparticipantes.Repositories.*;
import com.example.aesparticipantes.Seguridad.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ParticipanteController {

    @Autowired
    RankingGeneralController rankingGeneralController;

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    ClasificadoRepository clasificadoRepository;

    @Autowired
    ParticipanteRepository participanteRepository;

    @Autowired
    DescalificacionRepository descalificacionRepository;

    @Autowired
    InscripcionRepository inscripcionRepository;

    @Autowired
    ParticipanteController self;

    @Autowired
    TiempoRepository tiempoRepository;

    @RequestMapping("/participante/{nombreParticipante}")
    public String inicio(Model model, @PathVariable("nombreParticipante") String nombreParticipante, Principal principal) {

        if(principal instanceof UserData){
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            Participante yo = participanteRepository.findByNombre(nombreParticipanteGuardado);
            if(yo != null){
                if(nombreParticipante.equals(yo.getNombre())){
                    model.addAttribute("competicionesFuturas", self.getCompeticionesFuturas());
                    model.addAttribute("soyYo", true);
                }
            }
        }
        Participante participante = self.getParticipante(nombreParticipante);
        Map<Competicion, List<Posicion>> resultados = getResultadosParticipante(participante);

        model.addAttribute("resultados", resultados);
        model.addAttribute("participante", participante);


        return "participante";
    }




    @RequestMapping("/participante/{nombreParticipante}/{nombreCompeticion}")
    public String inicio(Model model, @PathVariable("nombreParticipante") String nombreParticipante, @PathVariable("nombreCompeticion") String nombreCompeticion) {


        List<Posicion> resultado = self.getPosicionesEnCompeticion(competicionRepository.findByNombre(nombreCompeticion), self.getParticipante(nombreParticipante), descalificacionRepository);
        model.addAttribute("participante", nombreParticipante);
        model.addAttribute("competicion", nombreCompeticion);
        model.addAttribute("resultado", resultado);

        return "participanteEnCompeticion";
    }

    @Cacheable(value = "participantes") // Tiempos EAGER...
    public Participante getParticipante(String nombreparticipante){
        return participanteRepository.findByNombre(nombreparticipante);
    }


    // Recoje los datos cacheados por getPosicionesEnCompeticion TODO: Pensarse si cachear dos veces aunque ya se cachee getPosicionEnParticipante
    public Map<Competicion, List<Posicion>> getResultadosParticipante(Participante participante) {

        Map<Competicion, List<Posicion>> resultados = new HashMap<>();

        Set<Competicion> competicionesParticipadas = participante.getTiempos().stream().collect(Collectors.groupingBy(t -> t.getEvento().getCompeticion())).keySet();
        competicionesParticipadas.forEach(competicion -> resultados.put(competicion, self.getPosicionesEnCompeticion(competicion, participante, descalificacionRepository)));
        return resultados;

    }

    @Cacheable(value = "posicionesParticipanteEnCompeticion", key = "#competicion.nombre + '-' + #participante.nombre")
    public List<Posicion> getPosicionesEnCompeticion(Competicion competicion, Participante participante, DescalificacionRepository descalificacionRepository) {

        List<Posicion> posiciones = new ArrayList<>();

        List<Evento> eventosParticipado = inscripcionRepository.findAllByParticipante(participante).stream().map(i -> i.getEvento()).collect(Collectors.toList());

        eventosParticipado.forEach(e -> posiciones.add(rankingGeneralController.getRankingGlobal(e, descalificacionRepository, clasificadoRepository).stream()
                .filter(p -> p.getParticipante().equals(participante)).findFirst().get()));

        posiciones.forEach(p -> {
            p.getTiempos().forEach(Tiempo::calcularDatos); //Solo se necesita para el enpoint de competición, pero mejor tener los datos ya cacheados
        });
        Collections.sort(posiciones);

        return posiciones;
    }

    @Cacheable(value = "competicionesFuturas")
    public List<Competicion> getCompeticionesFuturas(){
        return competicionRepository.findCompeticionesFuturas();
    }
}
