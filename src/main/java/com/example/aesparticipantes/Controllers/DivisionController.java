package com.example.aesparticipantes.Controllers;


import com.example.aesparticipantes.Entities.*;
import com.example.aesparticipantes.Models.DivisionJornada;
import com.example.aesparticipantes.Models.DivisionesEnJornada;
import com.example.aesparticipantes.Models.ParticipanteModel;
import com.example.aesparticipantes.Models.TimelinePointDivisiones;
import com.example.aesparticipantes.Repositories.CategoriaRepository;
import com.example.aesparticipantes.Repositories.CompeticionRepository;
import com.example.aesparticipantes.Repositories.TemporadaRepository;
import com.example.aesparticipantes.Utils.AESUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DivisionController {


    @Autowired
    TemporadaRepository temporadaRepository;

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    RankingGeneralController rankingGeneralController;

    @Autowired
    DivisionController self;

    Logger logger = LoggerFactory.getLogger(DivisionController.class);

    @RequestMapping("/temporada/{nombreTemporada}/competicion/{nombreCompeticion}/jornada/{numeroJornada}/categoria/{nombreCategoria}")
    public String getVistaCategoria(Model model,
                                    @PathVariable("nombreTemporada") String nombreTemporada,
                                    @PathVariable("nombreCompeticion") String nombreCompeticion,
                                    @PathVariable("numeroJornada") int numeroJornada,
                                    @PathVariable("nombreCategoria") String nombreCategoria){

        Optional<Temporada> temporada = temporadaRepository.findByNombre(nombreTemporada);

        if (!temporada.isPresent()){
            model.addAttribute("mensaje", "No hay ninguna temporada llamada "+ nombreTemporada +".");
            logger.warn("404 con mensaje: " + Objects.requireNonNull(model.getAttribute("mensaje")).toString());
            return "error/404";
        }

        Optional<Competicion> competicion = temporada.get().getCompeticiones().stream().filter(c -> c.getNombre().equals(nombreCompeticion)).findFirst();

        if (!competicion.isPresent()){
            model.addAttribute("mensaje", "No hay ninguna competición llamada "+ nombreCompeticion +".");
            logger.warn("404 con mensaje: " + Objects.requireNonNull(model.getAttribute("mensaje")).toString());
            return "error/404";
        }

        List<Clasificacion> clasificaciones = temporada.get().getClasificaciones();

        Optional<Clasificacion> clasificacion = clasificaciones.stream().filter(c -> c.getCategoria().getNombre().equals(nombreCategoria)).findAny(); //Nota 001-A

        if (!clasificacion.isPresent()){
            model.addAttribute("mensaje", nombreCategoria + " no es una categoría con divisiones en " + temporada.get().getNombre() +".");
            logger.warn("404 con mensaje: " + Objects.requireNonNull(model.getAttribute("mensaje")).toString());
            return "error/404";
        }

        Optional<Evento> evento = competicion.get().getEventos().stream().filter(e -> e.getCategoria().getNombre().equals(nombreCategoria)).findFirst(); // Nota 001-B
        Optional<Jornada> jornada = competicion.get().getJornadas().stream().filter(j -> j.getNumeroJornada() == numeroJornada).findFirst();

        if (!evento.isPresent() || !jornada.isPresent()){
            model.addAttribute("mensaje", "No existe "+ nombreCategoria +" en "+ nombreCompeticion +", o no existe la jornada"+ numeroJornada +".");
            logger.warn("404 con mensaje: " + Objects.requireNonNull(model.getAttribute("mensaje")).toString());
            return "error/404";
        }

        if (!jornada.get().isAcabada()){
            model.addAttribute("mensaje", "Esta jornada no está acabada, por lo tanto no hay divisiones.");
            logger.warn("404 con mensaje: " + Objects.requireNonNull(model.getAttribute("mensaje")).toString());
            return "error/404";
        }


        TimelinePointDivisiones actualTimelinePointDivisiones = new TimelinePointDivisiones(temporada.get(), jornada.get());
        List<TimelinePointDivisiones> timeline = TimelinePointDivisiones.getTimelineCompleta(temporada.get());

        model.addAttribute("divisionJornada", self.getVistaDivision(actualTimelinePointDivisiones, evento.get()));
        model.addAttribute("nombreTemporada", temporada.get().getNombre());

        //TODO: Convertir a objeto de vista sin entidades
        model.addAttribute("anteriorClasificacion", AESUtils.anteriorElemento(clasificaciones, clasificacion.get()));
        model.addAttribute("siguienteClasificacion", AESUtils.siguienteElemento(clasificaciones, clasificacion.get()));
        model.addAttribute("actualEvento", actualTimelinePointDivisiones);
        model.addAttribute("anteriorEvento", actualTimelinePointDivisiones.getAnterior());
        model.addAttribute("siguienteEvento", actualTimelinePointDivisiones.getSiguiente());
        model.addAttribute("timeline", timeline);


        return "rankingDivisiones";
    }

//    @Cacheable(value = "vistaDivisionEnJornada", key = "#temporada.nombre + #evento.competicion.nombre + #jornada.numeroJornada + #evento.categoria.nombre")
    public DivisionesEnJornada[] getVistaDivision(TimelinePointDivisiones timelinePointDivisiones, Evento evento){


        DivisionesEnJornada estadoJornadaAnteriorFase1 = new DivisionesEnJornada();

        Optional<TimelinePointDivisiones> anteriorTimelinePoint = timelinePointDivisiones.getAnterior();
        if(anteriorTimelinePoint.isPresent()){
         estadoJornadaAnteriorFase1 = self.getVistaDivision(anteriorTimelinePoint.get(),
                 Evento.builder().competicion(anteriorTimelinePoint.get().getJornada().getCompeticion()).categoria(evento.getCategoria()).build()
                 )[1];
        }

        List<Tiempo> rankingJornada = rankingGeneralController.getRankingJornada(evento, timelinePointDivisiones.getJornada().getNumeroJornada());

        //FASE 0
        DivisionesEnJornada fase0 = new DivisionesEnJornada();
        if(!anteriorTimelinePoint.isPresent()){
            int i = 1;
            int numDivision = 0;
            for (Tiempo tiempo : rankingJornada) {
                if(i == 1){
                    fase0.getDivisiones().add(new DivisionJornada());
                }
                fase0.getDivisiones().get(numDivision).getParticipantes().add(new ParticipanteModel(tiempo.getParticipante()));
                if(i++ == getTamanyoDivision(timelinePointDivisiones.getTemporada(), evento.getCategoria(), numDivision)){
                    i = 1;
                    numDivision++;
                }
            }

        } else {
            for (int i = 0; i < estadoJornadaAnteriorFase1.getDivisiones().size(); i++){

                estadoJornadaAnteriorFase1.getDivisiones().get(i).getParticipantes().sort(Comparator.comparing(p ->
                            rankingJornada.stream().filter(pp ->
                            pp.getParticipante().getNombre().equals(p.getNombre())).findAny().orElse(
                                    // Los que no participan en una jornada, quedan los últimos de SU división incluso por debajo de los que compiten por primera vez en la temporada
                                    Tiempo.builder().posicion(AESUtils.POSICION_NO_PARTICIPADOS).build())
                                .getPosicion()));

                fase0.getDivisiones().add(new DivisionJornada(estadoJornadaAnteriorFase1.getDivisiones().get(i).getParticipantes(), false));

            }

            // Añadir los nuevos participantes en el hueco que quede en la última división entre participados y no participados, y los que no quepan, en una nueva división
            int tamanyoMaximoUltimaDivision = getTamanyoDivision(timelinePointDivisiones.getTemporada(), evento.getCategoria(), fase0.getDivisiones().size());

            //TODO: check n+1
            List<Tiempo> nuevosParticipantes = rankingJornada.stream().filter(p -> fase0.getDivisiones().stream()
                    .allMatch(d -> d.getParticipantes().stream()
                            .noneMatch(pd -> {
                               return  pd.getNombre().equals(p.getParticipante().getNombre());
                            }))).collect(Collectors.toList());

            int numeroNoParticipantesUltimaDivision = (int) fase0.getDivisiones().get(fase0.getDivisiones().size() -1).getParticipantes().stream().filter(
                    p -> rankingJornada.stream().noneMatch(pp -> pp.getParticipante().getNombre().equals(p.getNombre()))).count();

            for (int i = 0; i < nuevosParticipantes.size(); i ++){

                if (tamanyoMaximoUltimaDivision > fase0.getDivisiones().get(fase0.getDivisiones().size() - 1).getParticipantes().size()){
                    fase0.getDivisiones().get(fase0.getDivisiones().size() - 1).getParticipantes().add(fase0.getDivisiones().get(fase0.getDivisiones().size() - 1).getParticipantes().size() - numeroNoParticipantesUltimaDivision /* hueco */,
                            new ParticipanteModel(nuevosParticipantes.get(i).getParticipante()));
                } else {
                    //Si supera el límite, crear una nueva división, en la siguiente iteración entrará en el if y no en el else
                    fase0.getDivisiones().add(new DivisionJornada(true));
                    tamanyoMaximoUltimaDivision = getTamanyoDivision(timelinePointDivisiones.getTemporada(), evento.getCategoria(), fase0.getDivisiones().size());
                    numeroNoParticipantesUltimaDivision = 0;
                    fase0.getDivisiones().get(fase0.getDivisiones().size() - 1).getParticipantes().add(new ParticipanteModel(nuevosParticipantes.get(i).getParticipante()));
                }
            }
        }

        //Fase 1

        fase0.getDivisiones().forEach(d -> d.getParticipantes().forEach(p -> {p.asciende = false; p.desciende = false;}));
        DivisionesEnJornada fase1 = fase0.clone();


        if(anteriorTimelinePoint.isPresent()){
            for(int i = 0; i < fase0.getDivisiones().size(); i++){

                List<ParticipanteModel> descensos = new ArrayList<>();
                List<ParticipanteModel> ascensos = new ArrayList<>();


                List<ParticipanteModel> participantesDivisionActualFase0 = fase0.getDivisiones().get(i).getParticipantes();


                if (i < fase0.getDivisiones().size() - 1 && !fase0.getDivisiones().get(i +1).isNueva()) {
                    List<ParticipanteModel> participantesDivisionSiguienteFase0 = fase0.getDivisiones().get(i + 1).getParticipantes();
                    ascensos = participantesDivisionSiguienteFase0.subList(0, Math.min(4, participantesDivisionSiguienteFase0.size()));

                    ascensos.forEach(p -> p.asciende = true);
                    descensos = participantesDivisionActualFase0.subList(participantesDivisionActualFase0.size() - ascensos.size(), participantesDivisionActualFase0.size());
                    descensos.forEach(p -> p.desciende = true);
                }


                for (int j = ascensos.size() - 1; j >= 0; j--) {
                    fase1.getDivisiones().get(i).getParticipantes().set(fase1.getDivisiones().get(i).getParticipantes().size() - ascensos.size() + j, ascensos.get(j));
                    fase1.getDivisiones().get(i + 1).getParticipantes().set(j, descensos.get(j));
                }


            }
        }

        return new DivisionesEnJornada[]{fase0, fase1};
    }

    private int getTamanyoDivision(Temporada temporada, Categoria categoria, int numDivision /* base 0 */) {

        if(numDivision > 2){ return 15; }
        if(numDivision > 0){ return 12; }
        //numDivision == 1

        //Siempre debería estar present ya que el evento que le pases debe haberse sacado en función de las categorías de la jornada (Notas 001-A y 001-B)
        return temporada.getClasificaciones().stream().filter(c -> c.getCategoria().equals(categoria)).findAny().get().getTamanoDivisiones();
    }
}
