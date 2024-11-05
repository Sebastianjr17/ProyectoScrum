package com.ProyectoScrum.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoScrum.app.entity.Evento;
import com.ProyectoScrum.app.entity.Reserva;
import com.ProyectoScrum.app.entity.ReservaDTO;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.EventoRepository; 
import com.ProyectoScrum.app.repository.ReservaRepository;
import com.ProyectoScrum.app.service.UsuarioService; // Servicio para obtener el rol del usuario

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioService usuarioService; // Servicio para obtener el rol del usuario

    @GetMapping("/")
    public String listaReservas(Model model) {
        // Obtiene el rol actual desde el servicio
        String rolUsuario = usuarioService.obtenerRolUsuarioActual();
        
        // Obtener reservas según el rol
        List<ReservaDTO> reservasDTO = reservaRepository.findAll().stream().map(reserva -> {
            String eventoNombre = eventoRepository.findById(reserva.getEventoId())
                    .map(Evento::getNombre)
                    .orElse("Evento no encontrado");
            return new ReservaDTO(reserva.getId(), reserva.getNombre(), eventoNombre, reserva.getTelefono(), reserva.getCantidadEntradas());
        }).collect(Collectors.toList());

        model.addAttribute("reservas", reservasDTO); // Usar reservasDTO para la vista
        model.addAttribute("esAdministrador", "admin".equals(rolUsuario)); // Añadir la información del rol al modelo
        return "reservas"; // Vista para listar reservas
    }

    @GetMapping("/new")
    public String nuevoReserva(Model model) {
        if (!"cliente".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/"; // Redirige si no es cliente
        }

        model.addAttribute("reserva", new Reserva());
        List<Evento> eventos = eventoRepository.findAll(); // Obtén todos los eventos disponibles
        model.addAttribute("eventos", eventos); // Agrega los eventos al modelo para la vista
        return "reservas-formulario"; // Vista para crear una nueva reserva
    }

    @PostMapping("/save")
    public String guardarReserva(@ModelAttribute("reserva") Reserva reserva, RedirectAttributes redirectAttributes) {
        if (!"cliente".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/"; // Redirige si no es cliente
        }

        reservaRepository.save(reserva);
        redirectAttributes.addFlashAttribute("successMessage", "Reserva guardada correctamente");
        return "redirect:/reservas/";
    }

    @GetMapping("/edit/{id}")
    public String editarReserva(@PathVariable("id") String id, Model model) {
        if (!"cliente".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/"; // Redirige si no es cliente
        }

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        model.addAttribute("reserva", reserva);
        
        List<Evento> eventos = eventoRepository.findAll(); // Obtener los eventos para la edición
        model.addAttribute("eventos", eventos);
        
        return "reservas-formulario"; // Vista para editar la reserva
    }

    @GetMapping("/delete/{id}")
    public String eliminarReserva(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!"cliente".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/"; // Redirige si no es cliente
        }

        if (!reservaRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Reserva no encontrada");
            return "redirect:/reservas/";
        }
        reservaRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Reserva eliminada correctamente");
        return "redirect:/reservas/";
    }
}
