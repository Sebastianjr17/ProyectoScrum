package com.ProyectoScrum.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoScrum.app.entity.Evento;
import com.ProyectoScrum.app.entity.Sala;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.EventoRepository;
import com.ProyectoScrum.app.repository.SalaRepository;

import java.util.List;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private SalaRepository salaRepository;

    @GetMapping("/")
    public String listaEventos(Model model) {
        model.addAttribute("eventos", eventoRepository.findAll());
        return "eventos"; // Este debe coincidir con el nombre del archivo HTML (eventos.html)
    }

    // Muestra el formulario para crear un nuevo evento
    @GetMapping("/new")
    public String nuevoEvento(Model model) {
        List<Sala> salas = salaRepository.findAll();
        model.addAttribute("salas", salas);
        model.addAttribute("evento", new Evento());
        return "formulario-evento"; // Asegúrate de que este archivo exista en la carpeta templates
    }

    // Muestra el formulario para editar un evento existente
    @GetMapping("/edit/{id}")
    public String editarEvento(@PathVariable("id") String id, Model model) {
        Evento evento = eventoRepository.findById(id).orElseThrow(() -> new NotFoundException("Evento no encontrado"));
        List<Sala> salas = salaRepository.findAll(); // Obtener las salas disponibles
        model.addAttribute("salas", salas);
        model.addAttribute("evento", evento);
        return "formulario-evento";
    }

    @PostMapping("/save")
    public String guardarEvento(@ModelAttribute("evento") Evento evento, RedirectAttributes redirectAttributes) {
        eventoRepository.save(evento);
        redirectAttributes.addFlashAttribute("mensaje", "Evento guardado exitosamente!");
        return "redirect:/eventos/"; // Asegúrate de que la barra diagonal final esté presente
    }


    // Elimina un evento
    @GetMapping("/delete/{id}")
    public String eliminarEvento(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Evento eliminado exitosamente!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Evento no encontrado!");
        }
        return "redirect:/eventos/"; // Redirige a la lista de eventos
    }
}

