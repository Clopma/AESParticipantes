package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Clasificado;
import com.example.miwebbase.Entities.Descalificacion;
import com.example.miwebbase.Entities.Participante;
import com.example.miwebbase.Models.RankingCategoriaParticipante;
import com.example.miwebbase.repositories.CategoriaRepository;
import com.example.miwebbase.repositories.ClasificadoRepository;
import com.example.miwebbase.repositories.DescalificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CalendarioController {

    @Autowired
    ClasificadoRepository clasificadoRepository;

    @Autowired
    CategoriaController categoriaController;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    DescalificacionRepository descalificacionRepository;

    List<Integer> ordenCuartos = Arrays.asList(0, 7, 4, 3, 2, 5, 6, 1);
    List<Integer> ordenSemifinales = Arrays.asList(0, 3, 2, 1);

    @GetMapping("/calendario")
    public String showForm(Model model) {
        return "calendario";
    }


    @GetMapping("/calendario/playoffs/{categoria}")
    public String evento(@PathVariable("categoria") String nombreCategoria, Model model) {

        Categoria categoria = categoriaRepository.findByNombre(nombreCategoria);

        List<Clasificado> cuartos = clasificadoRepository.getRonda(categoria, Clasificado.NombreRonda.CUARTO.name());
        List<Clasificado> semis = clasificadoRepository.getRonda(categoria, Clasificado.NombreRonda.SEMIFINAL.name());
        List<Clasificado> finales = clasificadoRepository.getRonda(categoria, Clasificado.NombreRonda.FINAL.name());
        List<Clasificado> ganador = clasificadoRepository.getRonda(categoria, Clasificado.NombreRonda.GANADOR.name());

        Clasificado[] cuartosClasi;
        Clasificado[] semisClasi;

        model.addAttribute("categoria", categoria);

        if(categoria.getCortePlayOffs() == 8){
            cuartosClasi = iniciarBracket(categoriaController.getRankingsCategoria(categoria), descalificacionRepository.findAllByCategoria(categoria), 8, semis, cuartos);
            semisClasi = rellenarBracket(semis, 4, categoria, finales, Arrays.asList(cuartosClasi));
            model.addAttribute("listaCuartos", cuartosClasi);
            model.addAttribute("listaSemis", semisClasi);
            model.addAttribute("listaFinal", rellenarBracket(finales, 2, categoria, ganador, Arrays.asList(semisClasi)));
        } else if (categoria.getCortePlayOffs() == 4) {
            semisClasi = iniciarBracket(categoriaController.getRankingsCategoria(categoria), descalificacionRepository.findAllByCategoria(categoria), 4, finales, semis);
            model.addAttribute("listaSemis", semisClasi);
            model.addAttribute("listaFinal", rellenarBracket(finales, 2, categoria, ganador, Arrays.asList(semisClasi)));
        }



        return "fragments/bracket";
    }


    @GetMapping("/calendario/evento/{evento}")
    public String evento(@PathVariable("evento") String evento) {
        return "fragments/eventos/"+evento;
    }

    private Clasificado[] rellenarBracket(List<Clasificado> clasificados, int numClasificados, Categoria categoria, List<Clasificado> siguienteRonda, List<Clasificado> rondaAnterior) {
        Clasificado[] rondas = new Clasificado[numClasificados];
        for (int i = 0; i < numClasificados; i++) {
            int finalI = i;

            clasificados.forEach(c -> c.setPosicion((rondaAnterior.indexOf(rondaAnterior.stream().filter(a -> a.getParticipante().equals(c.getParticipante())).findFirst().get())/2)));
            rondas[i] = clasificados.stream().filter(c -> c.getPosicion() == finalI).findFirst().orElse(
                    Clasificado.builder().participante(Participante.builder().nombre(categoria.getCortePlayOffs() == numClasificados ? puestoEnLugar(finalI, numClasificados) : "-").build()).posicion(i).build());
            int finalI1 = i;
            rondas[i].setVictoria(siguienteRonda.stream().anyMatch(s -> rondas[finalI1].getParticipante().equals(s.getParticipante())));
        }
        return rondas;
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


    private Clasificado[] iniciarBracket(List<RankingCategoriaParticipante> rankingCategoria, List<Descalificacion> descalificados, int numClasificados, List<Clasificado> siguienteRonda, List<Clasificado> rondaActual)  {

        List<Clasificado>  clasificados = rankingCategoria.stream().filter(p -> descalificados.stream()
                .noneMatch(d -> p.equals(d.getParticipante())))
                .map(p -> Clasificado.builder()
                        .participante(Participante.builder().nombre(p.getNombre()).build())
                        .victoria(siguienteRonda.stream().anyMatch(s -> s.getParticipante().equals(p)))
                        .puntuacion(rondaActual.stream().filter(a -> a.getParticipante().equals(p)).findFirst().orElse(Clasificado.builder().build()).getPuntuacion())
                .build())
                .collect(Collectors.toList())
                .subList(0, numClasificados);

        Clasificado[] clasificadosEnOrden = new Clasificado[numClasificados];

       for(int i = 0; i < numClasificados; i++){
           clasificadosEnOrden[numClasificados == 8 ? ordenCuartos.get(i) : ordenSemifinales.get(i)] = clasificados.get(i);

       }

       return clasificadosEnOrden;
    }

}
