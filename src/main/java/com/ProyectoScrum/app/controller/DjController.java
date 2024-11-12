package com.ProyectoScrum.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoScrum.app.entity.Dj;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.DjRepository;
import com.ProyectoScrum.app.service.UsuarioService;

@Controller
@RequestMapping("/djs")
public class DjController {

	@Autowired
	private DjRepository djRepository;

	@Autowired
	private UsuarioService usuarioService; // Servicio para obtener el rol del usuario

	@GetMapping("/")
	public String listaDjs(Model model) {
		// Obtiene el rol actual desde el servicio
		String rolUsuario = usuarioService.obtenerRolUsuarioActual();
		model.addAttribute("esAdministrador", "admin".equals(rolUsuario)); // Verifica si es admin

		List<Dj> djs = djRepository.findAll();
		model.addAttribute("djs", djs);
		return "djs"; // Asegúrate de que tienes una vista llamada 'djs.html'
	}

	@GetMapping("/new")
	public String nuevoDj(Model model) {
		if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
			return "redirect:/djs/"; // Redirige si no es admin
		}
		model.addAttribute("dj", new Dj());
		return "djs-formulario"; // Asegúrate de que tienes una vista para el formulario de DJ
	}

	@GetMapping("/edit/{id}")
	public String editarDj(@PathVariable("id") String id, Model model) {
		if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
			return "redirect:/djs/"; // Redirige si no es admin
		}
		Dj dj = djRepository.findById(id).orElseThrow(() -> new NotFoundException("DJ no encontrado"));
		model.addAttribute("dj", dj);
		return "djs-formulario"; // Asegúrate de que tienes una vista para editar DJ
	}

	@PostMapping("/save")
	public String guardarDj(@ModelAttribute("dj") Dj dj, RedirectAttributes redirectAttributes) {
		if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
			return "redirect:/djs/"; // Redirige si no es admin
		}
		// MongoDB generará el ID si no se proporciona
		djRepository.save(dj);
		redirectAttributes.addFlashAttribute("successMessage", "DJ guardado correctamente");
		return "redirect:/djs/";
	}

	@GetMapping("/delete/{id}")
	public String eliminarDj(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
		if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
			return "redirect:/djs/"; // Redirige si no es admin
		}
		if (!djRepository.existsById(id)) {
			redirectAttributes.addFlashAttribute("errorMessage", "DJ no encontrado");
			return "redirect:/djs/";
		}
		djRepository.deleteById(id);
		redirectAttributes.addFlashAttribute("successMessage", "DJ eliminado correctamente");
		return "redirect:/djs/";
	}
}
