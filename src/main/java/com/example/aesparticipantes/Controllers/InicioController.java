package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Participante;
import com.example.aesparticipantes.Repositories.CategoriaRepository;
import com.example.aesparticipantes.Repositories.ParticipanteRepository;
import com.example.aesparticipantes.Seguridad.UserData;
import com.example.aesparticipantes.Utils.AESUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InicioController {


    @Autowired
    ParticipanteRepository participanteRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    InicioController self;

    @GetMapping("/")
    public String inicio(Model model, Principal principal) {

        if(principal instanceof UserData){

            String  nombreParticipanteGuardado = ((UserData) principal).getPrincipal();
            Participante participante = participanteRepository.findByNombre(nombreParticipanteGuardado);
            model.addAttribute("nombreWca", ((UserData) principal).getWcaName());

            if(participante == null) {
                model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.NV.name());
            } else {

                model.addAttribute("nombreParticipante", nombreParticipanteGuardado);
                if (participante.isConfirmado()){
                    model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.C.name());
                } else {
                    model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.NC.name());
                }
            }
        } else {
            model.addAttribute("tipoUsuario", AESUtils.TiposUsuarios.N.name());

        }

        model.addAttribute("participantes", self.getParticipantes()); //TODO: Ponerte a ti mismo primero quizás...
        model.addAttribute("categorias", self.getCategoriasEnOrden());
        model.addAttribute("numJornadas", 5);

        return "inicio";
    }

    @Cacheable("nombresDeParticipantes")
    public List<String> getParticipantes() {
        return participanteRepository.getNombres();
    }

    @Cacheable("listaDeCategorias")
    public List<Categoria> getCategoriasEnOrden() {
        return categoriaRepository.findAllByOrderByOrden();
    }

    public List<RenderedImage> getImagesFromPDF(PDDocument document) throws IOException {
        List<RenderedImage> images = new ArrayList<>();
        for (PDPage page : document.getPages()) {
            images.addAll(getImagesFromResources(page.getResources()));
        }

        return images;
    }

    private List<RenderedImage> getImagesFromResources(PDResources resources) throws IOException {
        List<RenderedImage> images = new ArrayList<>();

        for (COSName xObjectName : resources.getXObjectNames()) {
            PDXObject xObject = resources.getXObject(xObjectName);

            if (xObject instanceof PDFormXObject) {
                images.addAll(getImagesFromResources(((PDFormXObject) xObject).getResources()));
            } else if (xObject instanceof PDImageXObject) {
                images.add(((PDImageXObject) xObject).getImage());
            }
        }

        return images;
    }


    @GetMapping("/cambios")
    public String verCambios() {
        return "cambios";
    }


}
