package com.ProyectoScrum.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoScrum.app.entity.Dj;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.DjRepository;

@Controller
@RequestMapping("/djs")
public class DjController {

    @Autowired
    private DjRepository djsRepository;

    @GetMapping("/")
    public String listaDjs(Model model) {
        model.addAttribute("djs", djsRepository.findAll());
        return "djs"; // Asegúrate de que tienes una vista llamada 'djs.html'
    }

    @GetMapping("/new")
    public String nuevoDj(Model model) {
        model.addAttribute("dj", new Dj());
        return "djs-formulario"; // Asegúrate de que tienes una vista para el formulario de DJ
    }

    @GetMapping("/edit/{id}")
    public String editarDj(@PathVariable("id") String id, Model model) {
        Dj dj = djsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("DJ no encontrado"));
        model.addAttribute("dj", dj);
        return "djs-formulario"; // Asegúrate de que tienes una vista para editar DJ
    }

    @PostMapping("/save")
    public String guardarDj(@ModelAttribute("dj") Dj dj, RedirectAttributes redirectAttributes) {
        // MongoDB generará el ID si no se proporciona
        djsRepository.save(dj);
        redirectAttributes.addFlashAttribute("successMessage", "DJ guardado correctamente");
        return "redirect:/djs/";
    }

    @GetMapping("/delete/{id}")
    public String eliminarDj(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!djsRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "DJ no encontrado");
            return "redirect:/djs/";
        }
        djsRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "DJ eliminado correctamente");
        return "redirect:/djs/";
    }
}
