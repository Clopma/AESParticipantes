package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.*;
import com.example.aesparticipantes.Models.InscripcionModel;
import com.example.aesparticipantes.Repositories.*;
import com.example.aesparticipantes.Seguridad.UserData;
import com.example.aesparticipantes.Utils.AESUtils;
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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

        Optional<Participante> participanteLogeado;
        if (principal instanceof UserData) {
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
             participanteLogeado = participanteRepository.findByNombre(nombreParticipanteGuardado);
            if (!participanteLogeado.isPresent()){
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

        List<Inscripcion> inscripciones = inscripcionRepository.getInscripcionesDeParticipanteEnCompeticion(participanteLogeado.get().getNombre(), competicion.get().getNombre());
        List<Tiempo> tiemposParticipadosEnJornada = tiempoRepository.getTiemposEnJornada(jornadaActiva.get(), participanteLogeado.get());

        List<InscripcionModel> categoriasParticipadasYSoloInscritas = participanteLogeado.get().getInscripcionesParticipadasYNoParticipadasEnCompeticion(competicion.get(), inscripciones, tiemposParticipadosEnJornada);

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
            Optional<Participante> participanteLogeado;
            if (principal instanceof UserData) {
                String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
                participanteLogeado = participanteRepository.findByNombre(nombreParticipanteGuardado);
                if (!participanteLogeado.isPresent()) {
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

        Optional<Inscripcion> posibleInscripcion = inscripcionRepository.findByEvento_CompeticionAndEvento_CategoriaAndParticipante(competicion.get(), categoria.get(), participanteLogeado.get());
        Optional<Tiempo> posibleTiempo = tiempoRepository.getByParticipanteAndCategoriaAndJornada(participanteLogeado.get(), categoria.get(), jornadaActiva.get());

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
                    .participante(participanteLogeado.get())
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
        model.addAttribute("participante", participanteLogeado.get());

        return "participar";
    }

    @PostMapping("/participar/{nombreCompeticion}/{nombreCategoria}")
    // Cache evict from code
    public ResponseEntity<String> enviarTiempos(@PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("nombreCompeticion") String nombreCompeticion,
                                                @RequestHeader(value = "tiempo1", required = false) String tiempo1op,
                                                @RequestHeader(value = "tiempo2", required = false) String tiempo2op,
                                                @RequestHeader(value = "tiempo3", required = false) String tiempo3op,
                                                @RequestHeader(value = "tiempo4", required = false) String tiempo4op,
                                                @RequestHeader(value = "tiempo5", required = false) String tiempo5op,
                                                @RequestHeader(value = "solucion", required = false) String solucion,
                                                @RequestHeader(value = "explicacion", required = false) String explicacion, Principal principal) {


        BigDecimal tiempo1 = tiempo1op != null ? AESUtils.truncarTiempoIntroducido(tiempo1op) : BigDecimal.ZERO;
        BigDecimal tiempo2 = tiempo2op != null ? AESUtils.truncarTiempoIntroducido(tiempo2op) : BigDecimal.ZERO;
        BigDecimal tiempo3 = tiempo3op != null ? AESUtils.truncarTiempoIntroducido(tiempo3op) : BigDecimal.ZERO;
        BigDecimal tiempo4 = tiempo4op != null ? AESUtils.truncarTiempoIntroducido(tiempo4op) : BigDecimal.ZERO;
        BigDecimal tiempo5 = tiempo5op != null ? AESUtils.truncarTiempoIntroducido(tiempo5op) : BigDecimal.ZERO;


        Optional<Participante> participanteLogeado;
        if (principal instanceof UserData) {
            String nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            participanteLogeado = participanteRepository.findByNombre(nombreParticipanteGuardado);
            if (!participanteLogeado.isPresent()) {
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

        Optional<Categoria> categoria = categoriaRepository.findByNombre(nombreCategoria);

        if(!categoria.isPresent()){
            return new ResponseEntity<>("Categoría no encontrada. " + FRASE_HACKER, HttpStatus.FORBIDDEN);
        }

        Optional<Tiempo> posibleTiempo = tiempoRepository.getByParticipanteAndCategoriaAndJornada(participanteLogeado.get(), categoria.get(), jornadaActiva.get());

        if(!posibleTiempo.isPresent()){
            return new ResponseEntity<>("Tiempo de inicio no encontrado. " + FRASE_HACKER, HttpStatus.FORBIDDEN);
        }

        Tiempo tiempo = posibleTiempo.get();
        if (tiempo.getEnvio() != null){
            return new ResponseEntity<>("Parece que ya has enviado el tiempo de esta categoría antes. " + FRASE_HACKER, HttpStatus.FORBIDDEN);
        }

        tiempo.setEnvio(new Date());
        tiempo.setTiempo1(tiempo1.doubleValue());
        tiempo.setTiempo2(tiempo2.doubleValue());
        tiempo.setTiempo3(tiempo3.doubleValue());
        tiempo.setTiempo4(tiempo4.doubleValue());
        tiempo.setTiempo5(tiempo5.doubleValue());
        tiempo.setExplicacion(explicacion == null ? null : new String(Base64.getDecoder().decode(explicacion.getBytes()), StandardCharsets.UTF_8));
        tiempo.setSolucion(solucion == null ? null : new String(Base64.getDecoder().decode(solucion.getBytes()), StandardCharsets.UTF_8));

        tiempoRepository.save(tiempo);

        cacheManager.getCache("participantes").evict(participanteLogeado.get().getNombre());
        cacheManager.getCache("rankingsGlobales").evict(Evento.getEventoId(competicion.get(), categoria.get()));
        cacheManager.getCache("rankingsJornada").evict(Evento.getEventoId(competicion.get(), categoria.get()) +"-"+jornadaActiva.get().getNumeroJornada());
        cacheManager.getCache("posicionesParticipanteEnCompeticion").evict(tiempo.getJornada().getCompeticion().getNombre() + "-" + participanteLogeado.get().getNombre());


        return new ResponseEntity<>("Los tiempos se han guardado correctamente", HttpStatus.OK);
    }

}
