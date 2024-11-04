package com.ProyectoScrum.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoScrum.app.entity.Sala;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.SalaRepository;

    @Controller
    @RequestMapping("/salas")
    public class SalaController {

        @Autowired
        private SalaRepository salaRepository;

        @GetMapping("/")
        public String listaSalas(Model model) {
            model.addAttribute("salas", salaRepository.findAll());
            return "salas"; // Este debe coincidir con el nombre del archivo HTML (salas.html)
        }

        // Muestra el formulario para crear una nueva sala
        @GetMapping("/new")
        public String nuevaSala(Model model) {
            model.addAttribute("sala", new Sala());
            return "salas-formulario"; // Asegúrate de que este archivo exista en la carpeta templates
        }

        // Muestra el formulario para editar una sala existente
        @GetMapping("/edit/{id}")
        public String editarSala(@PathVariable("id") String id, Model model) {
            Sala sala = salaRepository.findById(id).orElseThrow(() -> new NotFoundException("Sala no encontrada"));
            model.addAttribute("sala", sala);
            return "salas-formulario";
        }

        // Guarda la nueva sala o actualiza una sala existente
        @PostMapping("/save")
        public String guardarSala(@ModelAttribute("sala") Sala sala, RedirectAttributes redirectAttributes) {
            salaRepository.save(sala);
            redirectAttributes.addFlashAttribute("mensaje", "Sala guardada exitosamente!");
            return "redirect:/salas/"; // Redirige a la lista de salas
        }

        // Elimina una sala
        @GetMapping("/delete/{id}")
        public String eliminarSala(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
            if (salaRepository.existsById(id)) {
                salaRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("mensaje", "Sala eliminada exitosamente!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Sala no encontrada!");
            }
            return "redirect:/salas/"; // Redirige a la lista de salas
        }
    }
