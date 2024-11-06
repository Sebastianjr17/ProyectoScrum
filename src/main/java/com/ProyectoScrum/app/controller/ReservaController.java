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
	private UsuarioService usuarioService;

	// Verifica el rol del usuario y redirige si no es cliente
	private boolean esCliente() {
		return "cliente".equals(usuarioService.obtenerRolUsuarioActual());
	}

	// Verifica si el usuario es administrador
	private boolean esAdministrador() {
		return "admin".equals(usuarioService.obtenerRolUsuarioActual());
	}

	// Lista las reservas dependiendo del rol del usuario
	@GetMapping("/")
	public String listaReservas(@RequestParam(value = "correo", required = false) String correo, Model model) {
		List<ReservaDTO> reservasDTO = null;

		if (esAdministrador()) {
			// El admin siempre ve todas las reservas
			reservasDTO = reservaRepository.findAll().stream().map(reserva -> {
				String eventoNombre = eventoRepository.findById(reserva.getEventoId()).map(Evento::getNombre)
						.orElse("Evento no encontrado");
				return new ReservaDTO(reserva.getId(), reserva.getNombre(), reserva.getCorreoElectronico(),
						eventoNombre, reserva.getTelefono(), reserva.getCantidadEntradas());
			}).collect(Collectors.toList());
		} else if (esCliente() && correo != null && !correo.isEmpty()) {
			// El cliente solo puede ver sus propias reservas
			reservasDTO = reservaRepository.findByCorreoElectronico(correo).stream().map(reserva -> {
				String eventoNombre = eventoRepository.findById(reserva.getEventoId()).map(Evento::getNombre)
						.orElse("Evento no encontrado");
				return new ReservaDTO(reserva.getId(), reserva.getNombre(), reserva.getCorreoElectronico(),
						eventoNombre, reserva.getTelefono(), reserva.getCantidadEntradas());
			}).collect(Collectors.toList());
		}

		model.addAttribute("reservas", reservasDTO);
		model.addAttribute("esAdministrador", esAdministrador());
		return "reservas";
	}

	// Crea una nueva reserva
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

	// Guarda una nueva reserva
	@PostMapping("/save")
	public String guardarReserva(@ModelAttribute("reserva") Reserva reserva, RedirectAttributes redirectAttributes) {
		if (!esCliente()) {
			return "redirect:/";
		}

		reservaRepository.save(reserva);
		redirectAttributes.addFlashAttribute("successMessage", "Reserva guardada correctamente");
		return "redirect:/reservas/";
	}

	// Edita una reserva existente
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

	// Elimina una reserva
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
}
