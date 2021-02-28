package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.*;
import com.example.aesparticipantes.Models.Posicion;
import com.example.aesparticipantes.Repositories.*;
import com.example.aesparticipantes.Seguridad.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Transactional
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
    EventoRepository eventoRepository;

    @Autowired
    ParticipanteController self;

    @Autowired
    TiempoRepository tiempoRepository;

    @RequestMapping("/participante/{nombreParticipante}")
    public String perfilParticipante(Model model, @PathVariable("nombreParticipante") String nombreParticipante, Principal principal) {

        model.addAttribute("soyYo", false);

        if (principal instanceof UserData) { //TODO: Repetido en inscripción: refactor
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            Participante yo = participanteRepository.findByNombre(nombreParticipanteGuardado);
            if (yo != null && nombreParticipante.equals(yo.getNombre())) {
                model.addAttribute("soyYo", true);
            }
        }


        Optional<Participante> participante = participanteRepository.findByNombreParaPerfil(nombreParticipante);
        if(!participante.isPresent()){
            model.addAttribute("mensaje", "No hay ningún participante llamado "+ nombreParticipante +".");
            return "error/404";
        }

        List<Clasificado> clasificados = clasificadoRepository.findAllByEventoIn(participante.get().getInscripciones().stream().map(Inscripcion::getEvento).collect(Collectors.toSet()));
        List<Descalificacion> descalificados = descalificacionRepository.findAllByEventoIn(participante.get().getInscripciones().stream().map(Inscripcion::getEvento).collect(Collectors.toSet()));

        LinkedHashMap<Competicion, List<Posicion>> resultadosCompeticionesInscritas = getResultadosParticipante(participante.get(), clasificados, descalificados);
        List<Competicion> competicionesFuturas = competicionRepository.findCompeticionesFuturas();
        List<Competicion> competicionesPresentes = competicionRepository.findCompeticionesPresentesConInscripcionesAbiertas();


        eventoRepository.findAllByCompeticionIn(competicionesFuturas);// Previene n + 1 //TODO: cambiar por entity graph
        eventoRepository.findAllByCompeticionIn(competicionesPresentes);// Previene n + 1 //TODO: cambiar por entity graph


        model.addAttribute("competicionesFuturas", competicionesFuturas);
        model.addAttribute("competicionesPresentes", competicionesPresentes);
        model.addAttribute("resultados", resultadosCompeticionesInscritas);
        model.addAttribute("participante", participante.get());


        return "participante";
    }

    @RequestMapping("/participante/{nombreParticipante}/{nombreCompeticion}")
    public String participanteEnCompeticion(Model model, @PathVariable("nombreParticipante") String nombreParticipante, @PathVariable("nombreCompeticion") String nombreCompeticion, Principal principal) {

        model.addAttribute("soyYo", false);

        if (principal instanceof UserData) { //TODO: Repetido en inscripción: refactor
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            Participante yo = participanteRepository.findByNombre(nombreParticipanteGuardado);
            if (yo != null && nombreParticipante.equals(yo.getNombre())) {
                model.addAttribute("soyYo", true);
            }
        }

        Optional<Competicion> competicion = competicionRepository.findByNombre(nombreCompeticion);
        if(!competicion.isPresent()){
            model.addAttribute("mensaje", "No hay ninguna competición llamada "+ nombreCompeticion +".");
            return "error/404";
        }

        List<Posicion> resultado = self.getPosicionesEnCompeticion(competicion.get(), participanteRepository.findByNombre(nombreParticipante), descalificacionRepository.findAllByEventoIn(competicion.get().getEventos()), clasificadoRepository.findAllByEventoIn(competicion.get().getEventos()));
        model.addAttribute("participante", nombreParticipante);
        model.addAttribute("competicion", nombreCompeticion); //TODO: nombrecompeticion y cambiar en template
        model.addAttribute("resultado", resultado);

        return "participanteEnCompeticion";
    }


    public LinkedHashMap<Competicion, List<Posicion>> getResultadosParticipante(Participante participante, List<Clasificado> clasificados, List<Descalificacion> descalificados) {

        LinkedHashMap<Competicion, List<Posicion>> resultados = new LinkedHashMap<>();

        Set<Competicion> competicionesInscritas = participante.getInscripciones().stream().collect(Collectors.groupingBy(i -> i.getEvento().getCompeticion())).keySet();
        List<Competicion> competicionesInscritasOrdenadas = new ArrayList<>(competicionesInscritas);
        Collections.sort(competicionesInscritasOrdenadas);
        competicionesInscritasOrdenadas.forEach(competicion -> resultados.put(competicion, self.getPosicionesEnCompeticion(competicion, participante, descalificados, clasificados)));
        return resultados;

    }

    //@Cacheable(value = "posicionesParticipanteEnCompeticion", key = "#competicion.nombre + '-' + #participante.nombre")
    public List<Posicion> getPosicionesEnCompeticion(Competicion competicion, Participante participante, List<Descalificacion> descalificados, List<Clasificado> clasificados) {

        List<Posicion> posiciones = new ArrayList<>();

        List<Evento> eventosInscrito = participante.getInscripciones().stream().filter(i -> i.getEvento().getCompeticion().equals(competicion)).map(Inscripcion::getEvento).collect(Collectors.toList());

        eventosInscrito.forEach(e -> {

            Posicion posicion = rankingGeneralController.getRankingGlobal(e, descalificados, clasificados).stream()
                    .filter(p -> p.getParticipante().equals(participante)).findFirst()
                    .orElse(Posicion.builder().evento(e).posicionGeneral(0).build() // Aun no ha participado
                    );

            posiciones.add(posicion);

        });

        Collections.sort(posiciones);

        return posiciones;
    }

    @PostMapping("/enviarAjustes")
    public ResponseEntity<String> enviarAjustes(
            @RequestHeader("email") String email,
            @RequestHeader("anuncioNuevaCompeticion") Boolean anuncioNuevaCompeticion,
            @RequestHeader("recordatorioInscripcion") Boolean recordatorioInscripcion,
            @RequestHeader("recordatorioComienzo") Boolean recordatorioComienzo,
            @RequestHeader("recordatorioParticipar") Boolean recordatorioParticipar,
            @RequestHeader("recordatorioJornadas") Boolean recordatorioJornadas,
            Principal principal) {

        Participante participanteLogeado;
        if (principal instanceof UserData) {
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            participanteLogeado = participanteRepository.findByNombre(nombreParticipanteGuardado);
            if (participanteLogeado == null) {
                return new ResponseEntity<>("Usuario no encontrado", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>("Usuario no logeado", HttpStatus.UNAUTHORIZED);
        }

        participanteLogeado.setEmail(email);
        participanteLogeado.setAnuncioNuevaCompeticion(anuncioNuevaCompeticion);
        participanteLogeado.setRecordatorioInscripcion(recordatorioInscripcion);
        participanteLogeado.setRecordatorioComienzo(recordatorioComienzo);
        participanteLogeado.setRecordatorioParticipar(recordatorioParticipar);
        participanteLogeado.setRecordatorioJornadas(recordatorioJornadas);
        participanteRepository.save(participanteLogeado);
        //TODO: evictear participante
        return new ResponseEntity<>("Los tiempos se han guardado correctamente", HttpStatus.OK);
    }
}
