package com.ProyectoScrum.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoScrum.app.entity.Promocion;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.PromocionRepository;
import com.ProyectoScrum.app.repository.EventoRepository; // Asegúrate de tener este repositorio para eventos

@Controller
@RequestMapping("/promociones")
public class PromocionController {

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private EventoRepository eventoRepository; // Repositorio para acceder a los eventos

    @GetMapping("/")
    public String listaPromociones(Model model) {
        model.addAttribute("promociones", promocionRepository.findAll());
        return "promociones";
    }

    @GetMapping("/new")
    public String nuevaPromocion(Model model) {
        model.addAttribute("promocion", new Promocion());
        model.addAttribute("eventos", eventoRepository.findAll()); // Obtener todos los eventos para el dropdown
        return "promociones-formulario";
    }

    @GetMapping("/edit/{id}")
    public String editarPromocion(@PathVariable("id") String id, Model model) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Promocion no encontrada"));
        model.addAttribute("promocion", promocion);
        model.addAttribute("eventos", eventoRepository.findAll()); // También obtener eventos para el dropdown
        return "promociones-formulario";
    }

    @PostMapping("/save")
    public String guardarPromocion(@ModelAttribute("promocion") Promocion promocion, RedirectAttributes redirectAttributes) {
        promocionRepository.save(promocion);
        redirectAttributes.addFlashAttribute("successMessage", "Promoción guardada correctamente");
        return "redirect:/promociones/";
    }

    @GetMapping("/delete/{id}")
    public String eliminarPromocion(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!promocionRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Promoción no encontrada");
            return "redirect:/promociones/";
        }
        promocionRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Promoción eliminada correctamente");
        return "redirect:/promociones/";
    }
}

