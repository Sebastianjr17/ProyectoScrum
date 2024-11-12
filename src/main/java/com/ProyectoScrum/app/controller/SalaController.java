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
		model.addAttribute("esRecepcionista", "recepcionista".equals(rolUsuario)); // Verifica si es recepcionista
		model.addAttribute("salas", salaRepository.findAll());
		return "salas";
	}

	@GetMapping("/new")
	public String nuevaSala(Model model) {
		// Permite solo a los administradores acceder a la creación de salas
		if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
			return "redirect:/salas/"; // Redirige si no es admin
		}
		model.addAttribute("sala", new Sala());
		return "salas-formulario";
	}

	@GetMapping("/edit/{id}")
	public String editarSala(@PathVariable("id") String id, Model model) {
		String rolUsuario = usuarioService.obtenerRolUsuarioActual();
		Sala sala = salaRepository.findById(id).orElseThrow(() -> new NotFoundException("Sala no encontrada"));

		if ("admin".equals(rolUsuario)) {
			// Los administradores pueden editar todos los campos
			model.addAttribute("sala", sala);
			model.addAttribute("estadoEditable", true); // El administrador puede cambiar el estado también
			return "salas-formulario";
		} else if ("recepcionista".equals(rolUsuario)) {
			// Los recepcionistas solo pueden editar el estado
			model.addAttribute("sala", sala);
			model.addAttribute("estadoEditable", true); // Indicador de que el estado es editable
			model.addAttribute("estadoSolo", true); // Indicador de que solo el estado es editable
			return "salas-formulario";
		}

		return "redirect:/salas/"; // Redirige si no es admin ni recepcionista
	}

	@PostMapping("/save")
	public String guardarSala(@ModelAttribute("sala") Sala sala, RedirectAttributes redirectAttributes) {
		String rolUsuario = usuarioService.obtenerRolUsuarioActual();

		if ("admin".equals(rolUsuario)) {
			// Los administradores pueden guardar cambios completos en la sala
			salaRepository.save(sala);
			redirectAttributes.addFlashAttribute("mensaje", "Sala guardada exitosamente!");
		} else if ("recepcionista".equals(rolUsuario)) {
			// Los recepcionistas solo pueden guardar el estado
			Sala salaExistente = salaRepository.findById(sala.getId())
					.orElseThrow(() -> new NotFoundException("Sala no encontrada"));
			salaExistente.setEstado(sala.getEstado()); // Solo actualiza el estado
			salaRepository.save(salaExistente);
			redirectAttributes.addFlashAttribute("mensaje", "Estado de la sala actualizado exitosamente!");
		}

		return "redirect:/salas/";
	}

	@GetMapping("/delete/{id}")
	public String eliminarSala(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
		// Verificamos si el usuario tiene el rol de "admin" antes de permitir la
		// eliminación
		if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
			return "redirect:/salas/"; // Redirigimos si no es admin
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
