package com.ProyectoScrum.app.controller;

import com.ProyectoScrum.app.entity.EventoDTO;
import com.ProyectoScrum.app.entity.Evento;
import com.ProyectoScrum.app.entity.Sala;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.EventoRepository;
import com.ProyectoScrum.app.repository.SalaRepository;
import com.ProyectoScrum.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/eventos")
public class EventoController {

	@Autowired
	private EventoRepository eventoRepository;

	@Autowired
	private SalaRepository salaRepository;

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping("/")
	public String listaEventos(Model model) {
		// Obtiene el rol actual desde el servicio
		String rolUsuario = usuarioService.obtenerRolUsuarioActual();
		model.addAttribute("esAdministrador", "admin".equals(rolUsuario)); // Verifica si es admin

		// Obtiene la lista de eventos
		List<Evento> eventos = eventoRepository.findAll();

		// Convierte los eventos a DTOs
		List<EventoDTO> eventoDTOs = eventos.stream().map(evento -> {
			// Obtiene el nombre de la sala usando el ID de la sala
			Sala sala = salaRepository.findById(evento.getSalaId()).orElse(null);
			String salaNombre = (sala != null) ? sala.getNombre() : "Sala no disponible";

			// Crea un DTO para el evento
			return new EventoDTO(evento.getId(), evento.getNombre(), evento.getFecha(), evento.getDescripcion(),
					evento.getPrecioEntrada(), // Se pasa directamente como BigDecimal
					salaNombre);
		}).collect(Collectors.toList());

		model.addAttribute("eventos", eventoDTOs);
		return "eventos"; // Asegúrate de que este archivo exista en templates
	}

	@GetMapping("/new")
	public String nuevoEvento(Model model) {
		// Permite solo a los administradores acceder
		if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
			return "redirect:/eventos/"; // Redirige si no es admin
		}
		List<Sala> salas = salaRepository.findAll();
		model.addAttribute("salas", salas);
		model.addAttribute("evento", new Evento());
		return "formulario-evento"; // Asegúrate de que este archivo exista en templates
	}

	@GetMapping("/edit/{id}")
	public String editarEvento(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
		if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
			return "redirect:/eventos/"; // Redirige si no es admin
		}
		Evento evento = eventoRepository.findById(id).orElseThrow(() -> {
			redirectAttributes.addFlashAttribute("error", "Evento no encontrado");
			return new NotFoundException("Evento no encontrado");
		});
		List<Sala> salas = salaRepository.findAll();
		model.addAttribute("salas", salas);
		model.addAttribute("evento", evento);
		return "formulario-evento";
	}

	@PostMapping("/save")
	public String guardarEvento(@ModelAttribute("evento") Evento evento, RedirectAttributes redirectAttributes) {
		if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
			return "redirect:/eventos/"; // Redirige si no es admin
		}
		eventoRepository.save(evento);
		redirectAttributes.addFlashAttribute("mensaje", "Evento guardado exitosamente!");
		return "redirect:/eventos/"; // Asegúrate de que la barra diagonal final esté presente
	}

	@GetMapping("/delete/{id}")
	public String eliminarEvento(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
		if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
			return "redirect:/eventos/"; // Redirige si no es admin
		}
		if (eventoRepository.existsById(id)) {
			eventoRepository.deleteById(id);
			redirectAttributes.addFlashAttribute("mensaje", "Evento eliminado exitosamente!");
		} else {
			redirectAttributes.addFlashAttribute("error", "Evento no encontrado!");
		}
		return "redirect:/eventos/"; // Redirige a la lista de eventos
	}
}
