package com.ProyectoScrum.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoScrum.app.entity.Bebida;
import com.ProyectoScrum.app.entity.PromocionDTO;
import com.ProyectoScrum.app.entity.Promocion;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.PromocionRepository;
import com.ProyectoScrum.app.repository.BebidaRepository;
import com.ProyectoScrum.app.service.UsuarioService;

@Controller
@RequestMapping("/promociones")
public class PromocionController {

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private BebidaRepository bebidaRepository;

    @Autowired
    private UsuarioService usuarioService;

    // Método para verificar si el usuario es administrador
    private boolean esAdministrador() {
        return "admin".equals(usuarioService.obtenerRolUsuarioActual());
    }

    // Mostrar la lista de promociones
    @GetMapping("/")
    public String listaPromociones(Model model) {
        // Verifica si el usuario es administrador
        model.addAttribute("esAdministrador", esAdministrador());

        List<Promocion> promociones = promocionRepository.findAll();
        List<Bebida> bebidas = bebidaRepository.findAll();

        // Mapea las promociones a DTOs
        List<PromocionDTO> promocionesDTO = promociones.stream().map(promocion -> {
            List<String> nombreBebidas = promocion.getBebidasIds().stream()
                .map(bebidaId -> bebidas.stream()
                    .filter(bebida -> bebida.getId().equals(bebidaId))
                    .map(Bebida::getNombre)
                    .findFirst()
                    .orElse("Desconocido"))
                .collect(Collectors.toList());

            return new PromocionDTO(promocion.getId(), promocion.getDescuento(), nombreBebidas, promocion.getActivo());
        }).collect(Collectors.toList());

        model.addAttribute("promociones", promocionesDTO);
        return "promociones";  // Nombre de la vista de listado de promociones
    }

    // Formulario para crear una nueva promoción
    @GetMapping("/new")
    public String nuevaPromocion(Model model) {
        if (!esAdministrador()) {
            return "redirect:/promociones/";  // Redirige si no es administrador
        }
        model.addAttribute("promocion", new Promocion());
        model.addAttribute("bebidas", bebidaRepository.findAll());  // Todas las bebidas para el formulario
        return "promociones-formulario";  // Vista del formulario para nueva promoción
    }

    // Formulario para editar una promoción existente
    @GetMapping("/edit/{id}")
    public String editarPromocion(@PathVariable("id") String id, Model model) {
        if (!esAdministrador()) {
            return "redirect:/promociones/";  // Redirige si no es administrador
        }
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Promoción no encontrada"));
        model.addAttribute("promocion", promocion);
        model.addAttribute("bebidas", bebidaRepository.findAll());  // Todas las bebidas para el formulario
        return "promociones-formulario";  // Vista del formulario para editar
    }

    // Guardar la promoción (crear o actualizar)
    @PostMapping("/save")
    public String guardarPromocion(@ModelAttribute("promocion") Promocion promocion, RedirectAttributes redirectAttributes) {
        if (!esAdministrador()) {
            return "redirect:/promociones/";  // Redirige si no es administrador
        }

        // Si no se especifica el valor de "activo", lo establecemos en false por defecto
        if (promocion.getActivo() == null) {
            promocion.setActivo(false);
        }

        // Guardar o actualizar la promoción
        promocionRepository.save(promocion);
        redirectAttributes.addFlashAttribute("successMessage", "Promoción guardada correctamente");
        return "redirect:/promociones/";  // Redirige al listado de promociones
    }

    // Eliminar una promoción
    @GetMapping("/delete/{id}")
    public String eliminarPromocion(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!esAdministrador()) {
            return "redirect:/promociones/";  // Redirige si no es administrador
        }

        // Verifica si la promoción existe antes de eliminarla
        if (!promocionRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Promoción no encontrada");
            return "redirect:/promociones/";  // Redirige si la promoción no existe
        }

        // Eliminar la promoción
        promocionRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Promoción eliminada correctamente");
        return "redirect:/promociones/";  // Redirige al listado de promociones
    }
}
