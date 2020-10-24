package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Competicion;
import com.example.miwebbase.Entities.Participante;
import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Models.Posicion;
import com.example.miwebbase.repositories.ClasificadoRepository;
import com.example.miwebbase.repositories.CompeticionRepository;
import com.example.miwebbase.repositories.DescalificacionRepository;
import com.example.miwebbase.repositories.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
public class ParticipanteController {

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    ClasificadoRepository clasificadoRepository;

    @Autowired
    ParticipanteRepository participanteRepository;

    @Autowired
    DescalificacionRepository descalificacionRepository;

    @Autowired
    ParticipanteController self;


    @RequestMapping("/participante/{nombreParticipante}")
    public String inicio(Model model, @PathVariable("nombreParticipante") String nombreParticipante) {


        Participante participante =  participanteRepository.getByNombre(nombreParticipante);
        Map<Competicion, List<Posicion>> resultados = participante.getPosicionesEnCompeticiones(descalificacionRepository);
        resultados.values().forEach(pl -> pl.forEach(p -> p.setMedalla(clasificadoRepository)));

        model.addAttribute("resultados", resultados);

        return "participante";
    }


    @RequestMapping("/participante/{nombreParticipante}/{nombreCompeticion}")
    public String inicio(Model model, @PathVariable("nombreParticipante") String nombreParticipante, @PathVariable("nombreCompeticion") String nombreCompeticion) {


        List<Posicion> resultado = participanteRepository.getByNombre(nombreParticipante).getPosicionesEnCompeticion(competicionRepository.findByNombre(nombreCompeticion), descalificacionRepository);
        model.addAttribute("participante", nombreParticipante);
        model.addAttribute("competicion", nombreCompeticion);
        model.addAttribute("resultado", resultado);
        resultado.forEach(p -> {
            p.setMedalla(clasificadoRepository);
            p.getTiempos().forEach(Tiempo::calcularDatos);
        });



        return "participanteEnCompeticion";
    }



}
