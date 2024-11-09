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
import com.ProyectoScrum.app.service.UsuarioService;

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
    private UsuarioService usuarioService;

    // Verifica si el usuario es cliente
    private boolean esCliente() {
        return "cliente".equals(usuarioService.obtenerRolUsuarioActual());
    }

    // Verifica si el usuario es administrador
    private boolean esAdministrador() {
        return "admin".equals(usuarioService.obtenerRolUsuarioActual());
    }

    // Verifica si el usuario es recepcionista
    private boolean esRecepcionista() {
        return "recepcionista".equals(usuarioService.obtenerRolUsuarioActual());
    }

    // Lista las reservas dependiendo del rol del usuario
    @GetMapping("/")
    public String listaReservas(@RequestParam(value = "correo", required = false) String correo, Model model) {
        List<ReservaDTO> reservasDTO = null;

        // Si el usuario es administrador, solo ve todas las reservas, pero no puede editar ni eliminar
        if (esAdministrador()) {
            reservasDTO = reservaRepository.findAll().stream().map(reserva -> {
                String eventoNombre = eventoRepository.findById(reserva.getEventoId())
                        .map(Evento::getNombre)
                        .orElse("Evento no encontrado");
                return new ReservaDTO(
                        reserva.getId(),
                        reserva.getNombre(),
                        reserva.getCorreoElectronico(),
                        eventoNombre,
                        reserva.getTelefono(),
                        reserva.getCantidadEntradas(),
                        reserva.isAsistenciaMarcada() // El admin puede ver el estado de asistencia
                );
            }).collect(Collectors.toList());
        } else if (esRecepcionista()) {
            // El recepcionista puede ver todas las reservas y solo marcar la asistencia
            reservasDTO = reservaRepository.findAll().stream().map(reserva -> {
                String eventoNombre = eventoRepository.findById(reserva.getEventoId())
                        .map(Evento::getNombre)
                        .orElse("Evento no encontrado");
                return new ReservaDTO(
                        reserva.getId(),
                        reserva.getNombre(),
                        reserva.getCorreoElectronico(),
                        eventoNombre,
                        reserva.getTelefono(),
                        reserva.getCantidadEntradas(),
                        reserva.isAsistenciaMarcada() // El recepcionista puede ver el estado de asistencia
                );
            }).collect(Collectors.toList());
        } else if (esCliente() && correo != null && !correo.isEmpty()) {
            // Si es cliente, solo puede buscar sus propias reservas por correo
            reservasDTO = reservaRepository.findByCorreoElectronico(correo).stream().map(reserva -> {
                String eventoNombre = eventoRepository.findById(reserva.getEventoId())
                        .map(Evento::getNombre)
                        .orElse("Evento no encontrado");
                return new ReservaDTO(
                        reserva.getId(),
                        reserva.getNombre(),
                        reserva.getCorreoElectronico(),
                        eventoNombre,
                        reserva.getTelefono(),
                        reserva.getCantidadEntradas(),
                        reserva.isAsistenciaMarcada() // Solo el cliente puede ver su estado de asistencia
                );
            }).collect(Collectors.toList());
        }

        model.addAttribute("reservas", reservasDTO);
        model.addAttribute("esAdministrador", esAdministrador());
        model.addAttribute("esRecepcionista", esRecepcionista());
        model.addAttribute("esCliente", esCliente());
        return "reservas";
    }

    // Muestra el formulario para crear una nueva reserva (solo para clientes)
    @GetMapping("/new")
    public String nuevoReserva(Model model) {
        if (!esCliente()) {
            return "redirect:/";
        }

        model.addAttribute("reserva", new Reserva());
        List<Evento> eventos = eventoRepository.findAll();
        model.addAttribute("eventos", eventos);
        return "reservas-formulario";
    }

    // Guarda una nueva reserva (solo para clientes)
    @PostMapping("/save")
    public String guardarReserva(@ModelAttribute("reserva") Reserva reserva, RedirectAttributes redirectAttributes) {
        if (!esCliente()) {
            return "redirect:/";
        }

        reservaRepository.save(reserva);
        redirectAttributes.addFlashAttribute("successMessage", "Reserva guardada correctamente");
        return "redirect:/reservas/";
    }

    // Muestra el formulario para editar una reserva (solo para clientes)
    @GetMapping("/edit/{id}")
    public String editarReserva(@PathVariable("id") String id, Model model) {
        if (!esCliente()) {
            return "redirect:/";
        }

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        model.addAttribute("reserva", reserva);

        List<Evento> eventos = eventoRepository.findAll();
        model.addAttribute("eventos", eventos);

        return "reservas-formulario";
    }

    // Elimina una reserva (solo para clientes)
    @GetMapping("/delete/{id}")
    public String eliminarReserva(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!esCliente()) {
            return "redirect:/";
        }

        if (!reservaRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Reserva no encontrada");
            return "redirect:/reservas/";
        }

        reservaRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Reserva eliminada correctamente");
        return "redirect:/reservas/";
    }

    // Marca la asistencia a una reserva (solo para admin y recepcionista)
    @PostMapping("/asistencia/{id}")
    public String marcarAsistencia(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!esAdministrador() && !esRecepcionista()) {
            return "redirect:/";
        }

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        
        reserva.setAsistenciaMarcada(true); // Marca la asistencia
        reservaRepository.save(reserva);

        redirectAttributes.addFlashAttribute("successMessage", "Asistencia marcada correctamente.");
        return "redirect:/reservas/";
    }

    // Marca la no asistencia a una reserva (solo para admin y recepcionista)
    @PostMapping("/noAsistencia/{id}")
    public String marcarNoAsistencia(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!esAdministrador() && !esRecepcionista()) {
            return "redirect:/";
        }

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        
        reserva.setAsistenciaMarcada(false); // Marca la no asistencia
        reservaRepository.save(reserva);

        redirectAttributes.addFlashAttribute("successMessage", "No asistencia marcada correctamente.");
        return "redirect:/reservas/";
    }
}
