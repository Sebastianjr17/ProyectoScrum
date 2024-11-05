package com.ProyectoScrum.app.controller;

import java.util.List;

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
import com.ProyectoScrum.app.service.UsuarioService;

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
        model.addAttribute("esAdministrador", "admin".equals(rolUsuario)); // Verifica si es admin

        List<ReservaDTO> reservasDTO = obtenerReservasDTO();
        model.addAttribute("reservas", reservasDTO); // Usar reservasDTO para la vista
        return "reservas"; // Vista para listar reservas
    }

    @GetMapping("/new")
    public String nuevoReserva(Model model) {
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/reservas/"; // Redirige si no es admin
        }
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("eventos", eventoRepository.findAll()); // Obtener y agregar eventos
        return "reservas-formulario"; // Vista para crear una nueva reserva
    }

    @GetMapping("/edit/{id}")
    public String editarReserva(@PathVariable("id") String id, Model model) {
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/reservas/"; // Redirige si no es admin
        }
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        model.addAttribute("reserva", reserva);
        model.addAttribute("eventos", eventoRepository.findAll()); // Obtener eventos para la edición
        return "reservas-formulario"; // Vista para editar la reserva
    }

    @PostMapping("/save")
    public String guardarReserva(@ModelAttribute("reserva") Reserva reserva, RedirectAttributes redirectAttributes) {
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/reservas/"; // Redirige si no es admin
        }
        reservaRepository.save(reserva); // Guardar la reserva
        redirectAttributes.addFlashAttribute("successMessage", "Reserva guardada correctamente");
        return "redirect:/reservas/"; // Redirigir a la lista de reservas
    }

    @GetMapping("/delete/{id}")
    public String eliminarReserva(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/reservas/"; // Redirige si no es admin
        }
        if (!reservaRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Reserva no encontrada");
            return "redirect:/reservas/";
        }
        reservaRepository.deleteById(id); // Eliminar la reserva
        redirectAttributes.addFlashAttribute("successMessage", "Reserva eliminada correctamente");
        return "redirect:/reservas/"; // Redirigir a la lista de reservas
    }

    // Método auxiliar para obtener reservas como DTO
    private List<ReservaDTO> obtenerReservasDTO() {
        return reservaRepository.findAll().stream().map(reserva -> {
            String eventoNombre = eventoRepository.findById(reserva.getEventoId())
                    .map(Evento::getNombre)
                    .orElse("Evento no encontrado");
            return new ReservaDTO(reserva.getId(), reserva.getNombre(), eventoNombre, reserva.getTelefono(), reserva.getCantidadEntradas());
        }).collect(Collectors.toList());
    }
}
