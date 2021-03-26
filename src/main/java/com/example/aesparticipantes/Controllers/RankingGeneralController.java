package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.*;
import com.example.aesparticipantes.Models.Posicion;
import com.example.aesparticipantes.Repositories.*;
import com.example.aesparticipantes.Utils.AESUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class RankingGeneralController {

    @Autowired
    TiempoRepository tiempoRepository;

    @Autowired
    CompeticionController competicionController;

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    JornadaRepository jornadaRepository;

    @Autowired
    EventoRepository eventoRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    ClasificadoRepository clasificadoRepository;

    @Autowired
    MezclaRepository mezclaRepository;

    @Autowired
    DescalificacionRepository descalificacionRepository;

    @Autowired
    RankingGeneralController self;

    @RequestMapping("/ranking/{nombreCompeticion}/{nombreCategoria}")
    public String getVistaCategoria(Model model, @PathVariable("nombreCompeticion") String nombreCompeticion, @PathVariable("nombreCategoria") String nombreCategoria){

        Optional<Competicion> competicion = competicionRepository.findByNombre(nombreCompeticion);
        Optional<Categoria> categoria = categoriaRepository.findByNombre(nombreCategoria);

        if(!competicion.isPresent() || !categoria.isPresent()){
            return "error/404";
        }

        Optional<Evento> evento = eventoRepository.findByCategoriaAndCompeticion(categoria.get(), competicion.get());

        if(!evento.isPresent()){
            model.addAttribute("mensaje", "El evento compuesto por "+ nombreCompeticion +" y " + nombreCategoria + " no existe.");
        }

        List<Posicion> posiciones = self.getRankingGlobal(evento.get(), descalificacionRepository.findAllByEvento(evento.get()), clasificadoRepository.findAllByEvento(evento.get()));

        Set<Evento> eventosCompeticionEnOrden = competicion.get().getEventos();

        model.addAttribute("evento", evento.get());
        model.addAttribute("anteriorEvento", AESUtils.anteriorElemento(eventosCompeticionEnOrden, evento.get()));
        model.addAttribute("siguienteEvento", AESUtils.siguienteElemento(eventosCompeticionEnOrden, evento.get()));
        model.addAttribute("posiciones", posiciones);
        model.addAttribute("largoClasificacion", getLargoClasificacion(posiciones, evento.get().getCortePlayOffs()));
        model.addAttribute("eventos", eventosCompeticionEnOrden);

        return "rankingGeneral";
    }



    @RequestMapping("/ranking/{nombreCompeticion}/{nombreCategoria}/jornada/{numeroJornada}")
    public String getVistaCategoriaJornada(Model model, @PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("nombreCompeticion") String nombreCompeticion, @PathVariable("numeroJornada") int numeroJornada){

        Optional<Competicion> competicion = competicionRepository.findByNombre(nombreCompeticion);
        Optional<Categoria> categoria = categoriaRepository.findByNombre(nombreCategoria);

        if(!competicion.isPresent() || !categoria.isPresent()){
            return "error/404";
        }

        Optional<Evento> evento = eventoRepository.findByCategoriaAndCompeticion(categoria.get(), competicion.get());

        if(!evento.isPresent()){
            model.addAttribute("mensaje", "El evento compuesto por "+ nombreCompeticion +" y " + nombreCategoria + " no existe.");
        }

        Set<Evento> eventosCompeticionEnOrden = competicion.get().getEventos();
        model.addAttribute("evento", evento.get());
            Jornada jornada = jornadaRepository.findByCompeticionAndNumeroJornada(competicion.get(), numeroJornada);
            if(jornada.isAcabada()){
                List<Mezcla> mezclas = mezclaRepository.findAllByJornadaAndCategoria(jornada, categoria.get());
                model.addAttribute("mezclas", mezclas.isEmpty() ? null : mezclas);
            }
        model.addAttribute("anteriorEvento", AESUtils.anteriorElemento(eventosCompeticionEnOrden, evento.get()));
        model.addAttribute("siguienteEvento", AESUtils.siguienteElemento(eventosCompeticionEnOrden,  evento.get()));
        model.addAttribute("tiempos", self.getRankingJornada( evento.get(), numeroJornada));
        model.addAttribute("numJornadas",  evento.get().getCompeticion().getJornadas().size());

        return "jornada";
    }

    @Cacheable(value = "rankingsGlobales", key = "#evento.id")
    public List<Posicion> getRankingGlobal(Evento evento, List<Descalificacion> descalificados, List<Clasificado> clasificados) {
        evento.getTiempos().stream().collect(Collectors.groupingBy(Tiempo::getJornada)).forEach((j, ts) -> AESUtils.setPosicionesEnTiempos(ts));
        return evento.getRankingGlobal(descalificados, clasificados);
    }


    //@Cacheable(value = "rankingsJornada", key = "#evento.id+ '-' + #numeroJornada")
    public List<Tiempo> getRankingJornada(Evento evento, int numeroJornada){
        return evento.getRankingJornada(numeroJornada);
    }

    private int getLargoClasificacion(List<Posicion> puntuaciones, int largoPredeterminado) {

        int largoClasificacion = 0;
        int numClasificados = 0;
        for (Posicion puntuacion : puntuaciones){

            if(numClasificados == largoPredeterminado){
                break;
            }

            if(puntuacion.isClasificado()){
                numClasificados++;
            }

            largoClasificacion++;
        }

        return largoClasificacion;
    }





}
