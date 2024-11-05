package com.ProyectoScrum.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoScrum.app.entity.Evento;
import com.ProyectoScrum.app.entity.PromocionDTO;
import com.ProyectoScrum.app.entity.Promocion;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.PromocionRepository;
import com.ProyectoScrum.app.repository.EventoRepository;
import com.ProyectoScrum.app.service.UsuarioService;

@Controller
@RequestMapping("/promociones")
public class PromocionController {

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioService usuarioService; // Servicio para obtener el rol del usuario

    @GetMapping("/")
    public String listaPromociones(Model model) {
        // Obtiene el rol actual desde el servicio
        String rolUsuario = usuarioService.obtenerRolUsuarioActual();
        model.addAttribute("esAdministrador", "admin".equals(rolUsuario)); // Verifica si es admin

        List<Promocion> promociones = promocionRepository.findAll();
        List<Evento> eventos = eventoRepository.findAll();
        List<PromocionDTO> promocionesDTO = promociones.stream().map(promocion -> {
            List<String> nombreEventos = promocion.getEventosIds().stream()
                .map(eventoId -> eventos.stream()
                    .filter(evento -> evento.getId().equals(eventoId))
                    .map(Evento::getNombre)
                    .findFirst()
                    .orElse("Desconocido"))
                .collect(Collectors.toList());
            
            return new PromocionDTO(promocion.getId(), promocion.getNombre(), promocion.getDescripcion(), promocion.getDescuento(), nombreEventos);
        }).collect(Collectors.toList());

        model.addAttribute("promociones", promocionesDTO);
        return "promociones";
    }

    @GetMapping("/new")
    public String nuevaPromocion(Model model) {
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/promociones/"; // Redirige si no es admin
        }
        model.addAttribute("promocion", new Promocion());
        model.addAttribute("eventos", eventoRepository.findAll()); // Obtener eventos para el dropdown
        return "promociones-formulario";
    }

    @GetMapping("/edit/{id}")
    public String editarPromocion(@PathVariable("id") String id, Model model) {
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/promociones/"; // Redirige si no es admin
        }
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Promoción no encontrada"));
        model.addAttribute("promocion", promocion);
        model.addAttribute("eventos", eventoRepository.findAll()); // Obtener eventos para el dropdown
        return "promociones-formulario";
    }

    @PostMapping("/save")
    public String guardarPromocion(@ModelAttribute("promocion") Promocion promocion, RedirectAttributes redirectAttributes) {
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/promociones/"; // Redirige si no es admin
        }
        promocionRepository.save(promocion);
        redirectAttributes.addFlashAttribute("successMessage", "Promoción guardada correctamente");
        return "redirect:/promociones/";
    }

    @GetMapping("/delete/{id}")
    public String eliminarPromocion(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!"admin".equals(usuarioService.obtenerRolUsuarioActual())) {
            return "redirect:/promociones/"; // Redirige si no es admin
        }
        if (!promocionRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Promoción no encontrada");
            return "redirect:/promociones/";
        }
        promocionRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Promoción eliminada correctamente");
        return "redirect:/promociones/";
    }
}
