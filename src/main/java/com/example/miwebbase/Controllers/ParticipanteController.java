package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Models.ResultadoCompeticion;
import com.example.miwebbase.repositories.CompeticionRepository;
import com.example.miwebbase.repositories.TiempoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ParticipanteController {

    @Autowired
    private TiempoRepository tiempoRepository;

    @Autowired
    private RankingGeneralController rankingGeneralController;

    @Autowired
    CompeticionRepository competicionRepository;


    @Autowired
    ParticipanteController self;


    @RequestMapping("/participante/{nombreParticipante}")
    public String inicio(Model model, @PathVariable("nombreParticipante") String nombreParticipante) {


         List<ResultadoCompeticion> resultados = new ArrayList<>();

        tiempoRepository.findCompeticionesParticipadasPor(nombreParticipante).forEach(c -> {
             ResultadoCompeticion resultadoCompeticion = self.getResultadoParticipante(nombreParticipante, c.getNombre());
             resultadoCompeticion.setNombreCompeticion(c.getNombre());
             resultados.add(resultadoCompeticion);
         });

        model.addAttribute("resultados", resultados);

        return "participante";
    }


    @RequestMapping("/participante/{nombreParticipante}/{nombreCompeticion}")
    public String inicio(Model model, @PathVariable("nombreParticipante") String nombreParticipante, @PathVariable("nombreCompeticion") String nombreCompeticion) throws Exception {

        model.addAttribute("participante", nombreParticipante);
        model.addAttribute("competicion", nombreCompeticion);

        model.addAttribute("resultado", self.getResultadoParticipante(nombreParticipante, nombreCompeticion));

        return "participanteEnCompeticion";
    }

    @Cacheable(value = "participantes")
    public ResultadoCompeticion getResultadoParticipante(String nombreParticipante, String nombreCompeticion) {

        List<Tiempo> tiemposParticipante = tiempoRepository.getTiemposOfParticipante(nombreParticipante, nombreCompeticion);

        Map<Integer, List<Tiempo>> categoriasInformadas = tiemposParticipante.stream().collect(Collectors.groupingBy(t -> t.getCategoria().getOrden()));

        ResultadoCompeticion resultadoCompeticion = new ResultadoCompeticion();
        resultadoCompeticion.setCategoriasParticipadas(new ArrayList<>());

        for (Map.Entry<Integer, List<Tiempo>> categoriaEntry : categoriasInformadas.entrySet()) {
            resultadoCompeticion.generarYAnadirCategoria(categoriaEntry.getValue(), rankingGeneralController, nombreParticipante);
        }

        return resultadoCompeticion;
    }


}
