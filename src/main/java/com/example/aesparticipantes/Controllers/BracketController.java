package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.*;
import com.example.aesparticipantes.Models.DivisionJornada;
import com.example.aesparticipantes.Models.Posicion;
import com.example.aesparticipantes.Models.TimelinePointDivisiones;
import com.example.aesparticipantes.Repositories.*;
import com.example.aesparticipantes.Utils.AESUtils;
import com.example.aesparticipantes.Utils.SpecialCaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
//TODO: Toda esta clase es terriblemente terrible, aquí no hace falta un refactor, aquí hace falta eliminar todo y rehacerlo
public class BracketController {

    @Autowired
    ClasificadoRepository clasificadoRepository;

    @Autowired
    ClasificacionRepository clasificacionRepository;

    @Autowired
    RankingGeneralController rankingGeneralController;

    @Autowired
    TemporadaRepository temporadaRepository;

    @Autowired
    DivisionController divisionController;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    EventoRepository eventoRepository;

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    DescalificacionRepository descalificacionRepository;

    @Autowired
    CompeticionController competicionController;

    List<Integer> ordenCuartos = Arrays.asList(0, 7, 4, 3, 2, 5, 6, 1);
    List<Integer> ordenSemifinales = Arrays.asList(0, 3, 2, 1);

    @GetMapping("/bracket")
    public String showForm(Model model) {
        return "bracket";
    }


    @GetMapping("/bracket/competicion/{nombreCompeticion}/{nombreCategoria}")
    public String getBracketCompeticion(@PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("nombreCompeticion") String nombreCompeticion, Model model) {

        Optional<Competicion> competicion = competicionRepository.findByNombre(nombreCompeticion);
        Optional<Categoria> categoria = categoriaRepository.findByNombre(nombreCategoria);

        if (!competicion.isPresent() || !categoria.isPresent()) {
            model.addAttribute("mensaje", "No hay ninguna competición llamada " + nombreCompeticion + " o categoría llamada " + nombreCategoria + ".");
            return "error/404";
        }

        Optional<Evento> evento = eventoRepository.findByCategoriaAndCompeticion(categoria.get(), competicion.get());

        if (!evento.isPresent()) {
            model.addAttribute("mensaje", "El evento compuesto por " + nombreCompeticion + " y " + nombreCategoria + " no existe.");
        }

        model.addAttribute("tamano", evento.get().getCortePlayOffs());
        model.addAttribute("nombreCategoria", evento.get().getCategoria().getNombre());
        List<Clasificado> cuartos = clasificadoRepository.getRonda(evento.get(), Clasificado.NombreRonda.CUARTO.name());
        List<Clasificado> semis = clasificadoRepository.getRonda(evento.get(), Clasificado.NombreRonda.SEMIFINAL.name());
        List<Clasificado> finales = clasificadoRepository.getRonda(evento.get(), Clasificado.NombreRonda.FINAL.name());
        List<Clasificado> ganador = clasificadoRepository.getRonda(evento.get(), Clasificado.NombreRonda.GANADOR.name());

        Clasificado[] cuartosClasi;
        Clasificado[] semisClasi;


        if (evento.get().getCortePlayOffs() == 8) {
            cuartosClasi = iniciarBracket(rankingGeneralController.getRankingGlobal(evento.get(), descalificacionRepository.findAllByEvento(evento.get()), clasificadoRepository.findAllByEvento(evento.get())), descalificacionRepository.findAllByEvento(evento.get()), 8, semis, cuartos, evento);
            semisClasi = rellenarBracket(semis, 4, evento, Optional.empty(), finales, Arrays.asList(cuartosClasi));
            model.addAttribute("listaCuartos", cuartosClasi);
            model.addAttribute("listaSemis", semisClasi);
            model.addAttribute("listaFinal", rellenarBracket(finales, 2, evento, Optional.empty(), ganador, Arrays.asList(semisClasi)));
        } else if (evento.get().getCortePlayOffs() == 4) {
            semisClasi = iniciarBracket(rankingGeneralController.getRankingGlobal(evento.get(), descalificacionRepository.findAllByEvento(evento.get()), clasificadoRepository.findAllByEvento(evento.get())), descalificacionRepository.findAllByEvento(evento.get()), 4, finales, semis, evento);
            model.addAttribute("listaSemis", semisClasi);
            model.addAttribute("listaFinal", rellenarBracket(finales, 2, evento, Optional.empty(), ganador, Arrays.asList(semisClasi)));
        }


        return "fragments/bracket";
    }


    @GetMapping("/bracket/temporada/{nombreTemporada}/{nombreCategoria}")
    public String getBracketTemporada(@PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("nombreTemporada") String nombreTemporada, Model model) {

        Optional<Temporada> temporada = temporadaRepository.findByNombre(nombreTemporada);
        Optional<Categoria> categoria = categoriaRepository.findByNombre(nombreCategoria);

        if (!temporada.isPresent() || !categoria.isPresent()) {
            model.addAttribute("mensaje", "No hay ninguna temoprada llamada " + nombreTemporada + " o categoría llamada " + nombreCategoria + ".");
            return "error/404";
        }

        Optional<Clasificacion> clasificacion = clasificacionRepository.findByCategoriaAndTemporada(categoria.get(), temporada.get());

        if (!clasificacion.isPresent()) {
            model.addAttribute("mensaje", "La clasificación compuesta por " + nombreTemporada + " y " + nombreCategoria + " no existe.");
        }


        model.addAttribute("tamano", clasificacion.get().getTamanoDivisiones() - 4);
        model.addAttribute("nombreCategoria", clasificacion.get().getCategoria().getNombre());
        List<Clasificado> cuartos = clasificadoRepository.getRonda(clasificacion.get(), Clasificado.NombreRonda.CUARTO.name());
        List<Clasificado> semis = clasificadoRepository.getRonda(clasificacion.get(), Clasificado.NombreRonda.SEMIFINAL.name());
        List<Clasificado> finales = clasificadoRepository.getRonda(clasificacion.get(), Clasificado.NombreRonda.FINAL.name());
        List<Clasificado> ganador = clasificadoRepository.getRonda(clasificacion.get(), Clasificado.NombreRonda.GANADOR.name());

        Clasificado[] cuartosClasi;
        Clasificado[] semisClasi;


        if (clasificacion.get().getTamanoDivisiones() - 4 == 8) {
            cuartosClasi = iniciarBracketTemporada(clasificacion.get(), 8, semis, cuartos);
            semisClasi = rellenarBracket(semis, 4, Optional.empty(), clasificacion, finales, Arrays.asList(cuartosClasi));
            model.addAttribute("listaCuartos", cuartosClasi);
            model.addAttribute("listaSemis", semisClasi);
            model.addAttribute("listaFinal", rellenarBracket(finales, 2, Optional.empty(), clasificacion, ganador, Arrays.asList(semisClasi)));
        } else if (clasificacion.get().getTamanoDivisiones() - 4 == 4) {
            semisClasi = iniciarBracketTemporada(clasificacion.get(), 4, finales, semis);
            model.addAttribute("listaSemis", semisClasi);
            model.addAttribute("listaFinal", rellenarBracket(finales, 2, Optional.empty(), clasificacion, ganador, Arrays.asList(semisClasi)));
        }


        return "fragments/bracket";
    }


    @GetMapping("/bracket/evento/{evento}")
    public String getBracketCompeticion(@PathVariable("evento") String evento) {

        return "fragments/eventos/" + evento;
    }

    private Clasificado[] rellenarBracket(List<Clasificado> clasificados, int numClasificados, Optional<Evento> evento, Optional<Clasificacion> clasificacion, List<Clasificado> siguienteRonda, List<Clasificado> rondaAnterior) {
        int tamano = evento.isPresent() ? evento.get().getCortePlayOffs() : clasificacion.get().getTamanoDivisiones();

        Clasificado[] rondas = new Clasificado[numClasificados];
        for (int i = 0; i < numClasificados; i++) {
            int finalI = i;

            clasificados.forEach(c -> c.setPosicion((rondaAnterior.indexOf(rondaAnterior.stream().filter(a -> a.getParticipante().equals(c.getParticipante())).findFirst().get()) / 2)));
            rondas[i] = clasificados.stream().filter(c -> c.getPosicion() == finalI).findFirst().orElse(
                    Clasificado.builder().participante(Participante.builder().nombre(tamano == numClasificados ? puestoEnLugar(finalI, numClasificados) : "-").build()).posicion(i).build());
            int finalI1 = i;
            rondas[i].setVictoria(siguienteRonda.stream().anyMatch(s -> rondas[finalI1].getParticipante().equals(s.getParticipante())));
        }
        setMedallas(evento, clasificacion, rondas);
        return rondas;
    }

    private void setMedallas(Optional<Evento> evento, Optional<Clasificacion> clasificacion, Clasificado[] rondas) {
        if (evento.isPresent()) { // Si es competición o temporada
            Arrays.asList(rondas).forEach(r -> r.setMedalla(AESUtils.getPosicionFinal(clasificadoRepository.getRondasParticipante(r.getParticipante(), evento.get()))));
        } else {
            Arrays.asList(rondas).forEach(r -> r.setMedalla(AESUtils.getPosicionFinal(clasificadoRepository.getRondasParticipante(r.getParticipante(), clasificacion.get()))));
        }
    }

    private String puestoEnLugar(int finalI, int numClasificados) {

        if (numClasificados == 8) {
            return "Posición " + ordenCuartos.get(finalI);
        }

        if (numClasificados == 4) {
            return "Posición " + ordenSemifinales.get(finalI);
        }

        return "Posición " + (finalI + 1);


    }


    private Clasificado[] iniciarBracketTemporada(Clasificacion clasificacion, int numClasificados, List<Clasificado> siguienteRonda, List<Clasificado> rondaActual) {

        TimelinePointDivisiones timelinePoint;

        try {
            timelinePoint = TimelinePointDivisiones.getUltimaAcabada(clasificacion.getTemporada());
        } catch (SpecialCaseException sce){
            return new Clasificado[0]; //La temporada acaba de empezar
        }

        List<DivisionJornada> divisionesJornada = divisionController.getVistaDivision(timelinePoint, Evento.builder().competicion(timelinePoint.getJornada().getCompeticion()).categoria(clasificacion.getCategoria()).build())[1].getDivisiones();

        if (divisionesJornada.isEmpty()) {
            return new Clasificado[0]; //La temporada acaba de empezar
        }

        if (divisionesJornada.get(0).getParticipantes().size() < numClasificados) {
            return new Clasificado[0]; //No hay suficientes participantes para montar los playoffs
        }

        List<Clasificado> clasificados = divisionesJornada.get(0).getParticipantes().subList(0, numClasificados).stream()
                .map(p -> Clasificado.builder()
                        .participante(Participante.builder().nombre(p.getNombre()).build())
                        .victoria(siguienteRonda.stream().anyMatch(s -> s.getParticipante().getNombre().equals(p.getNombre())))
                        .puntuacion(rondaActual.stream().filter(a -> a.getParticipante().getNombre().equals(p.getNombre())).findFirst().orElse(Clasificado.builder().build()).getPuntuacion())
                        .build())
                .collect(Collectors.toList())
                .subList(0, numClasificados);

        Clasificado[] clasificadosEnOrden = new Clasificado[numClasificados];

        for (int i = 0; i < numClasificados; i++) {
            clasificadosEnOrden[numClasificados == 8 ? ordenCuartos.get(i) : ordenSemifinales.get(i)] = clasificados.get(i);
        }

        setMedallas(Optional.empty(), Optional.of(clasificacion), clasificadosEnOrden);


        return clasificadosEnOrden;
    }

    private Clasificado[] iniciarBracket(List<Posicion> rankingCategoria, List<Descalificacion> descalificados, int numClasificados, List<Clasificado> siguienteRonda, List<Clasificado> rondaActual, Optional<Evento> evento) {

        List<Clasificado> clasificados = rankingCategoria.stream().filter(p -> descalificados.stream()
                .noneMatch(d -> p.getNombreParticipante().equals(d.getParticipante().getNombre())))
                .map(p -> Clasificado.builder()
                        .participante(Participante.builder().nombre(p.getNombreParticipante()).build())
                        .victoria(siguienteRonda.stream().anyMatch(s -> s.getParticipante().getNombre().equals(p.getNombreParticipante())))
                        .puntuacion(rondaActual.stream().filter(a -> a.getParticipante().getNombre().equals(p.getNombreParticipante())).findFirst().orElse(Clasificado.builder().build()).getPuntuacion())
                        .build())
                .collect(Collectors.toList())
                .subList(0, numClasificados);

        Clasificado[] clasificadosEnOrden = new Clasificado[numClasificados];

        for (int i = 0; i < numClasificados; i++) {
            clasificadosEnOrden[numClasificados == 8 ? ordenCuartos.get(i) : ordenSemifinales.get(i)] = clasificados.get(i);
        }
        setMedallas(evento, Optional.empty(), clasificadosEnOrden);


        return clasificadosEnOrden;
    }

}
