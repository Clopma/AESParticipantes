package com.example.miwebbase.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CacheController {


    @Autowired
    CacheManager cacheManager;


    @RequestMapping("/limpiarCache")
    @ResponseBody
    public String limpiarCache() {

        cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());

        return "¡Caché limpia y reluciente!";
    }


}
