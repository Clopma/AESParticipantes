package com.example.aesparticipantes.Controllers;


import com.example.aesparticipantes.Seguridad.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
public class CacheController {


    @Autowired
    CacheManager cacheManager;

    List<String> usuariosConPermisos = Arrays.asList("Carlos López Marí", "Rafael Rodriguez Santana", "Julio Perugorria Lorente", "Fernando Sáez Lázaro");

    @RequestMapping("/limpiarCache")
    public String limpiarCache(Model model, Principal principal) {

        if (principal instanceof UserData && usuariosConPermisos.contains(((UserData) principal).getPrincipal())) {
            limpiar();
            model.addAttribute("mensaje", "Caché limpia y reluciente en tiempo récord, incluso diría que es mi PB :O");
            return "mensaje";
        }

        model.addAttribute("mensaje", "No tienes permiso para limpiar la caché. Quizás no hayas iniciado sesión.");
        return "mensaje";
    }

    public void limpiar(){
        cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }


    @Scheduled(fixedRate = 3000000)
    public void continuaLimpieza(){
        limpiar();
    }


}
