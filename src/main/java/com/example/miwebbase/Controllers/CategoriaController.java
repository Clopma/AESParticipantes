package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Descalificacion;
import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Models.PuntuacionIndividual;
import com.example.miwebbase.Models.RankingCategoriaParticipante;
import com.example.miwebbase.Models.Resultado;
import com.example.miwebbase.Utils.AESUtils;
import com.example.miwebbase.repositories.CategoriaRepository;
import com.example.miwebbase.repositories.DescalificacionRepository;
import com.example.miwebbase.repositories.TiempoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
public class CategoriaController {

    @Autowired
    TiempoRepository tiempoRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    DescalificacionRepository descalificacionRepository;

    @Autowired CategoriaController self;

    @RequestMapping("/categoria/{nombreCategoria}")
    public String getVistaCategoria(Model model, @PathVariable("nombreCategoria") String nombreCategoria){

        Categoria categoria = self.getCategoria(nombreCategoria);

        List<RankingCategoriaParticipante> puntuaciones = self.getRankingsCategoria(categoria);


        model.addAttribute("categoria", categoria);
        model.addAttribute("posiciones", puntuaciones);
        model.addAttribute("largoClasificacion", getLargoClasificacion(puntuaciones, categoria.getCortePlayOffs()));
        model.addAttribute("categorias", self.getCategoriasEnOrden());
        model.addAttribute("columnasJornadas", new int[AESUtils.JORNADAS_CAMPEONATO]);

        return "categoria";
    }

    private int getLargoClasificacion(List<RankingCategoriaParticipante> puntuaciones, int largoPredeterminado) {

        int largoClasificacion = 0;
        int numClasificados = 0;
        for (RankingCategoriaParticipante puntuacion : puntuaciones){

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

    @RequestMapping("/categoria/{nombreCategoria}/jornada/{numeroJornada}")
    public String getVistaCategoriaJornada(Model model, @PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("numeroJornada") int numeroJornada){

        Categoria categoria = self.getCategoria(nombreCategoria);
        model.addAttribute("categoria", categoria);
        model.addAttribute("categorias", self.getCategoriasEnOrden());
        model.addAttribute("resultado", self.getRankingJornada(categoria, numeroJornada));
        model.addAttribute("numJornadas", AESUtils.JORNADAS_CAMPEONATO);

        return "jornada";
    }


    @Cacheable(key = "#categoria.nombre", value = "puntuacionesGenerales")
    public List<RankingCategoriaParticipante> getRankingsCategoria(Categoria categoria){

        List<RankingCategoriaParticipante> puntuacionesTotales = tiempoRepository.getParticipantesPuntosTotalesCategoria(categoria);


        List<RankingCategoriaParticipante> puntuacionesTotalesAux = new ArrayList<>(puntuacionesTotales);

        // Ordena por puntuación y si no, por puntuación desempatada
        Comparator<RankingCategoriaParticipante> comparadorPuntosYPosiciones =  ((Comparator<RankingCategoriaParticipante>)(p1, p2) -> p2.getPuntuacion_total().compareTo(p1.getPuntuacion_total()))
                .thenComparing(p -> getPosicionDeParticipante(p.getNombre(), categoria, puntuacionesTotalesAux, tiempoRepository));

        puntuacionesTotales.sort(comparadorPuntosYPosiciones);

        // Setea todas las posiciones tal y como se han ordenado
        AtomicInteger posicion = new AtomicInteger(1);
        puntuacionesTotales.forEach(p -> p.setPosicion(posicion.getAndIncrement()));

        puntuacionesTotales.forEach(pt -> pt.setPuntuacionesIndividuales(tiempoRepository.getParticipantesPuntosIndividualesCategoria(categoria).stream()
                .filter(pi -> pi.getNombre().equals(pt.getNombre())).collect(Collectors.toList())));


        List<Descalificacion> descalificados = descalificacionRepository.findAllByCategoria(categoria);
        puntuacionesTotales.stream().filter(p -> descalificados.stream()
                .noneMatch(d -> p.equals(d.getParticipante())))
                .collect(Collectors.toList())
                .subList(0, categoria.getCortePlayOffs())
                .forEach(p -> p.setClasificado(true));

        return puntuacionesTotales;

    }

    @Cacheable(key = "#categoria.nombre + #numeroJornada", value = "puntuacionesJornada")
    public List<Resultado.Categoria.Jornada> getRankingJornada(Categoria categoria, int numeroJornada){

        List<Tiempo> tiemposJornada = tiempoRepository.findAllByCategoriaAndJornadaOrderByPosicion(categoria, numeroJornada);

        Resultado.Categoria categoriaModel = new Resultado.Categoria(categoria);
        tiemposJornada.forEach(p -> categoriaModel.generarYAnadirJornada(p, categoria));

        return categoriaModel.getJornadas();

    }

    public RankingCategoriaParticipante getRankingParticipante(Categoria categoria, String nombreParticipante) {
        return getRankingsCategoria(categoria).stream().filter(r -> nombreParticipante.equals(r.getNombre())).findFirst().orElse(null);
    }

    @Cacheable(value = "categorias")
    public Categoria getCategoria(String nombreCategoria){
        return  categoriaRepository.findByNombre(nombreCategoria);
    }

    @Cacheable(value = "listaDeCategorias")
    public List<Categoria> getCategoriasEnOrden(){
        return categoriaRepository.findAllByOrderByOrden();
    }


    private static Integer getPosicionDeParticipante(String nombreParticipante, Categoria categoria, List<RankingCategoriaParticipante> puntuaciones, TiempoRepository tiempoRepository) {

        RankingCategoriaParticipante puntuacionTotalParticipantes = puntuaciones.stream()
                .filter(p -> p.getNombre().equals(nombreParticipante))
                .findFirst()
                .get().clone();

        List<RankingCategoriaParticipante> puntuacionesEmpatadasOriginalmentePorPuntuacionTotal = puntuaciones.stream().map(RankingCategoriaParticipante::clone)
                .filter(p -> p.getPuntuacion_total().intValue() == puntuacionTotalParticipantes.getPuntuacion_total().intValue()).collect(Collectors.toList());

        if (puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.size() == 1) {
            return puntuaciones.indexOf(puntuaciones.stream().filter(p -> p.getNombre().equals(nombreParticipante)).findFirst().get()) + 1;
        }

        puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.forEach(p -> p.setPuntuacionesIndividuales(tiempoRepository.getParticipantePuntosIndividualesCategoria(categoria, p.getNombre())));

        List<RankingCategoriaParticipante> puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.stream().map(RankingCategoriaParticipante::clone).collect(Collectors.toList());

        List<String> personasDesempatadasEnOrden = new ArrayList<>();

        //Mientras el nombre que buscamos no esté ordenado
        while (!personasDesempatadasEnOrden.stream().anyMatch(p -> p.equals(nombreParticipante))) {

            List<RankingCategoriaParticipante> puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas =
                    puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream().map(RankingCategoriaParticipante::clone).collect(Collectors.toList());

            puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.forEach(p -> p.getPuntuacionesIndividuales().sort(Comparator.comparingInt(PuntuacionIndividual::getPuntuacion_jornada).reversed()));

            List<RankingCategoriaParticipante> puntuacionesEmpatadasPorPuntuacionesIndividuales;
            while (true) {

                int mejorPuntuacionJornadaN = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.stream()
                        .mapToInt(ps -> ps.getPuntuacionesIndividuales().size() > 0 ? ps.getPuntuacionesIndividuales().get(0).getPuntuacion_jornada() : 0)
                        .max().getAsInt();

                List<RankingCategoriaParticipante> aux = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.stream().map(RankingCategoriaParticipante::clone)
                        .filter(p -> p.getPuntuacionesIndividuales().size() > 0 && p.getPuntuacionesIndividuales().get(0).getPuntuacion_jornada() == mejorPuntuacionJornadaN)
                        .collect(Collectors.toList());

                if (aux.size() > 1) {

                    puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.forEach(p -> p.getPuntuacionesIndividuales().remove(0));

                } else if (aux.size() == 1) {

                    puntuacionesEmpatadasPorPuntuacionesIndividuales = aux;
                    break;

                } else if (aux.size() == 0) {

                    puntuacionesEmpatadasPorPuntuacionesIndividuales = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas;
                    break;

                }

            }

            if (puntuacionesEmpatadasPorPuntuacionesIndividuales.size() == 1) {

                String personaConMejorPuntuacionIndividual = puntuacionesEmpatadasPorPuntuacionesIndividuales.get(0).getNombre();
                personasDesempatadasEnOrden.add(personaConMejorPuntuacionIndividual);
                puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream()
                        .filter(p -> !p.getNombre().equals(personaConMejorPuntuacionIndividual))
                        .collect(Collectors.toList());

            } else {

                //Desempatar por media
                List<Tiempo> tiemposEmpatados = tiempoRepository.getTiemposDeVariosParticipantes(categoria, puntuacionesEmpatadasPorPuntuacionesIndividuales.stream().map(RankingCategoriaParticipante::getNombre).collect(Collectors.toSet()));
                tiemposEmpatados.stream().forEach(t -> t.setMedia(AESUtils.getTiemposCalculados(Arrays.asList(t.getTiempo1(), t.getTiempo2(), t.getTiempo3(), t.getTiempo4(), t.getTiempo5()), categoria)[1]));

                Tiempo mejorMedia = tiemposEmpatados.stream().reduce((acc, val) -> (acc.getMedia() > 0) && (acc.getMedia() < val.getMedia()) ? acc : val).get();

                String personaConMejorMedia = puntuacionesEmpatadasPorPuntuacionesIndividuales.stream().filter(p -> p.getNombre().equals(mejorMedia.getParticipante().getNombre())).findFirst().get().getNombre();
                personasDesempatadasEnOrden.add(personaConMejorMedia);
                puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream()
                        .filter(p -> !p.getNombre().equals(personaConMejorMedia))
                        .collect(Collectors.toList());

            }
        }

        List<String> nombresEmpatados = puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.stream().map(RankingCategoriaParticipante::getNombre).collect(Collectors.toList());
        int posicionParticipanteSuperior = puntuaciones.indexOf(puntuaciones.stream()
                .filter(p -> nombresEmpatados.contains(p.getNombre()))
                .findFirst()
                .get());

        int posicionParticipante = personasDesempatadasEnOrden.indexOf(personasDesempatadasEnOrden.stream()
                .filter(nombreParticipante::equals)
                .findFirst()
                .get()) + 1;

        return posicionParticipanteSuperior + posicionParticipante;
    }


}
