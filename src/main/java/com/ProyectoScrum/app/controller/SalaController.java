package com.ProyectoScrum.app.controller;

import com.ProyectoScrum.app.entity.Sala;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.SalaRepository;
import com.ProyectoScrum.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/salas")
public class SalaController {

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String listaSalas(Model model) {
        // Obtiene el rol actual desde el servicio
        String rolUsuario = usuarioService.obtenerRolUsuarioActual();
        model.addAttribute("esAdministrador", "admin".equals(rolUsuario)); // Verifica si es admin
        model.addAttribute("salas", salaRepository.findAll());
        return "salas";
    }

    @GetMapping("/new")
    public String nuevaSala(Model model) {
        // Permite solo a los administradores acceder
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/salas/"; // Redirige si no es admin
        }
        model.addAttribute("sala", new Sala());
        return "salas-formulario";
    }

    @GetMapping("/edit/{id}")
    public String editarSala(@PathVariable("id") String id, Model model) {
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/salas/";
        }
        Sala sala = salaRepository.findById(id).orElseThrow(() -> new NotFoundException("Sala no encontrada"));
        model.addAttribute("sala", sala);
        return "salas-formulario";
    }

    @PostMapping("/save")
    public String guardarSala(@ModelAttribute("sala") Sala sala, RedirectAttributes redirectAttributes) {
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/salas/";
        }
        salaRepository.save(sala);
        redirectAttributes.addFlashAttribute("mensaje", "Sala guardada exitosamente!");
        return "redirect:/salas/";
    }

    @GetMapping("/delete/{id}")
    public String eliminarSala(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/salas/";
        }
        if (salaRepository.existsById(id)) {
            salaRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Sala eliminada exitosamente!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Sala no encontrada!");
        }
        return "redirect:/salas/";
    }
}

