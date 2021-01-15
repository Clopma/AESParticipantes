package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.*;
import com.example.aesparticipantes.Repositories.*;
import com.example.aesparticipantes.Seguridad.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ParticiparController {

    private static final String FRASE_HACKER = "Esto no debería ocurrir a no ser que seas un hacker.";
    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    ParticipanteController participanteController;

    @Autowired
    ParticipanteRepository participanteRepository;

    @Autowired
    InscripcionRepository inscripcionRepository;

    @Autowired
    TiempoRepository tiempoRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    MezclaRepository mezclaRepository;

    @Autowired
    CacheManager cacheManager;

    @GetMapping("/participar/{nombreCompeticion}")
    public String elegirCategoria(@PathVariable("nombreCompeticion") String nombreCompeticion, Model model, Principal principal) {

        Participante participanteLogeado;
        if (principal instanceof UserData) {
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
             participanteLogeado = participanteRepository.findByNombre(nombreParticipanteGuardado);
            if (participanteLogeado == null){
                return noLogeado(model);
            }
        } else {
            return noLogeado(model);
        }

        Optional<Competicion> competicion = competicionRepository.findByNombre(nombreCompeticion);
        if(!competicion.isPresent()){
            model.addAttribute("mensaje", "No hay ninguna competición llamada "+ nombreCompeticion +".");
            return "error/404";
        }

        Optional<Jornada> jornadaActiva = competicion.get().getJornadaActiva();
        if (!jornadaActiva.isPresent()){
            model.addAttribute("mensaje", "No hay ninguna jornada activa en este campeonato. Si crees que esto es un error, contacta a un administrador.");
            return "mensaje";
        }


        List<Categoria> categoriasParticipadas = tiempoRepository.getTiemposEnJornada(jornadaActiva.get(), participanteLogeado).stream().filter(t -> !t.isEnCurso())
                .map(Tiempo::getCategoria).collect(Collectors.toList());

        List<com.example.aesparticipantes.Models.Inscripcion> categoriasParticipadasYSoloInscritas = participanteLogeado.getInscripcionesParticipadasYNoParticipadasEnCompeticion(competicion.get(), categoriasParticipadas);

        model.addAttribute("inscripciones", categoriasParticipadasYSoloInscritas);

        return "elegirCategoria";
    }

    private String noLogeado(Model model) {
        model.addAttribute("mensaje", "Necesitas tener la sesión iniciada para participar.");
        return "mensaje";
    }

    @GetMapping("/participar/{nombreCompeticion}/{nombreCategoria}")
    public String participar(@PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("nombreCompeticion") String nombreCompeticion, Model model, Principal principal) {


        //TODO: Código duplicado, meter en métodos de validación: con un throws custom exception con un campo para la template y otro para el mensaje
            Participante participanteLogeado;
            if (principal instanceof UserData) {
                String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
                participanteLogeado = participanteRepository.findByNombre(nombreParticipanteGuardado);
                if (participanteLogeado == null) {
                    return noLogeado(model);
                }
            } else {
                return noLogeado(model);
            }

            Optional<Competicion> competicion = competicionRepository.findByNombre(nombreCompeticion);

            if(!competicion.isPresent()){
                model.addAttribute("mensaje", "No hay ninguna competición llamada "+ nombreCompeticion +".");
                return "error/404";
            }
            Optional<Jornada> jornadaActiva = competicion.get().getJornadaActiva();
            if (!jornadaActiva.isPresent()) {
                model.addAttribute("mensaje", "No hay ninguna jornada activa en este campeonato. Si crees que esto es un error, contacta a un administrador.");
                return "mensaje";
            }
         // FIN TODO

        Optional<Categoria> categoria = categoriaRepository.findByNombre(nombreCategoria);

        if(!categoria.isPresent()){
            return "error/404";
        }

        Optional<Inscripcion> posibleInscripcion = inscripcionRepository.findByEvento_CompeticionAndEvento_CategoriaAndParticipante(competicion.get(), categoria.get(), participanteLogeado);
        Optional<Tiempo> posibleTiempo = tiempoRepository.getByParticipanteAndCategoriaAndJornada(participanteLogeado, categoria.get(), jornadaActiva.get());

        if (!posibleInscripcion.isPresent()){
            model.addAttribute("mensaje", "No estás inscrito en esa categoría. Por favor, no intentes hacer trampas. Si crees que esto es un error, contacta a un administrador.");
            return "mensaje";
        }

        if (posibleTiempo.isPresent() && !posibleTiempo.get().isEnCurso()){
            model.addAttribute("mensaje", "Ya has participado en esta categoría en esta jornada. Si crees que esto es un error, contacta a un administrador.");
            return "mensaje";
        }


        if (!posibleTiempo.isPresent()) {
            tiempoRepository.save(Tiempo.builder()
                    .jornada(jornadaActiva.get())
                    .participante(participanteLogeado)
                    .categoria(categoria.get())
                    .comienzo(new Date())
                    //TODO: https://trello.com/c/zx5rFvfH
                    .tiempo1(0)
                    .tiempo2(0)
                    .tiempo3(0)
                    .tiempo4(0)
                    .tiempo5(0)
                    .build());
        }

        List<Mezcla> mezclas = mezclaRepository.findAllByJornadaAndCategoria(jornadaActiva.get(), categoria.get());

        posibleTiempo.ifPresent(tiempo -> model.addAttribute("segundosRestantes", tiempo.segundosRestantes()));
        model.addAttribute("categoria", categoria.get());
        model.addAttribute("competicion", competicion.get());
        model.addAttribute("mezclas", mezclas);
        model.addAttribute("participante", participanteLogeado);

        return "participar";
    }

    @PostMapping("/participar/{nombreCompeticion}/{nombreCategoria}")
    // Cache evict from code
    public ResponseEntity<String> enviarTiempos(@PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("nombreCompeticion") String nombreCompeticion,
                                                @RequestHeader(value = "tiempo1", required = false) Double tiempo1op,
                                                @RequestHeader(value = "tiempo2", required = false) Double tiempo2op,
                                                @RequestHeader(value = "tiempo3", required = false) Double tiempo3op,
                                                @RequestHeader(value = "tiempo4", required = false) Double tiempo4op,
                                                @RequestHeader(value = "tiempo5", required = false) Double tiempo5op,
                                                @RequestHeader(value = "solucion", required = false) String solucion,
                                                @RequestHeader(value = "explicacion", required = false) String explicacion,
                                                Model model, Principal principal) {


        double tiempo1 = tiempo1op != null ? tiempo1op : 0;
        double tiempo2 = tiempo2op != null ? tiempo2op : 0;
        double tiempo3 = tiempo3op != null ? tiempo3op : 0;
        double tiempo4 = tiempo4op != null ? tiempo4op : 0;
        double tiempo5 = tiempo5op != null ? tiempo5op : 0;


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

        Optional<Competicion> competicion = competicionRepository.findByNombre(nombreCompeticion);
        if (!competicion.isPresent()) {
            return  new ResponseEntity<>("Competición no encontrada. " + FRASE_HACKER, HttpStatus.FORBIDDEN);
        }

        Optional<Jornada> jornadaActiva = competicion.get().getJornadaActiva();
        if (!jornadaActiva.isPresent()) {
            return new ResponseEntity<>("Lo sentimos, pero la jornada ha terminado mientras participabas. No hay que dejar las cosas para el último momento...", HttpStatus.FORBIDDEN);
        }

        Optional<Categoria> categoria = categoriaRepository.findByNombre(nombreCategoria); //TODO: Cachear (hacer esta tarea después de entender el cacheo de hibernate)

        if(!categoria.isPresent()){
            return new ResponseEntity<>("Categoría no e.ncontrada. " + FRASE_HACKER, HttpStatus.FORBIDDEN);
        }

        Optional<Tiempo> posibleTiempo = tiempoRepository.getByParticipanteAndCategoriaAndJornada(participanteLogeado, categoria.get(), jornadaActiva.get());

        if(!posibleTiempo.isPresent()){
            return new ResponseEntity<>("Tiempo de inicio no encontrado. " + FRASE_HACKER, HttpStatus.FORBIDDEN);
        }

        Tiempo tiempo = posibleTiempo.get();
        if (tiempo.getEnvio() != null){
            return new ResponseEntity<>("Parece que ya has enviado el tiempo de esta categoría antes. " + FRASE_HACKER, HttpStatus.FORBIDDEN);
        }

        tiempo.setEnvio(new Date());
        tiempo.setTiempo1(Math.floor(tiempo1 * 100)/100);
        tiempo.setTiempo2(Math.floor(tiempo2 * 100)/100);
        tiempo.setTiempo3(Math.floor(tiempo3 * 100)/100);
        tiempo.setTiempo4(Math.floor(tiempo4 * 100)/100);
        tiempo.setTiempo5(Math.floor(tiempo5 * 100)/100);
        tiempo.setExplicacion(explicacion == null ? null : new String(Base64.getDecoder().decode(explicacion.getBytes()), StandardCharsets.ISO_8859_1));
        tiempo.setSolucion( solucion == null ? null : new String(Base64.getDecoder().decode(solucion.getBytes()), StandardCharsets.ISO_8859_1));

        tiempoRepository.save(tiempo);

        cacheManager.getCache("participantes").evict(participanteLogeado.getNombre());
        cacheManager.getCache("rankingsGlobales").evict(Evento.getEventoId(competicion.get(), categoria.get()));
        cacheManager.getCache("rankingsJornada").evict(Evento.getEventoId(competicion.get(), categoria.get()) +"-"+jornadaActiva.get().getNumeroJornada());
        cacheManager.getCache("posicionesParticipanteEnCompeticion").evict(tiempo.getJornada().getCompeticion().getNombre() + "-" + participanteLogeado.getNombre());


        return new ResponseEntity<>("Los tiempos se han guardado correctamente", HttpStatus.OK);
    }

}
