package com.ProyectoScrum.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoScrum.app.entity.Cliente;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.ClienteRepository;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/")
    public String listaClientes(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "clientes";
    }

    @GetMapping("/new")
    public String nuevoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes-formulario";
    }

    @GetMapping("/edit/{id}")
    public String editarCliente(@PathVariable("id") String id, Model model) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
        model.addAttribute("cliente", cliente);
        return "clientes-formulario";
    }

    @PostMapping("/save")
    public String guardarCliente(@ModelAttribute("cliente") Cliente cliente, RedirectAttributes redirectAttributes) {
        // MongoDB generará el ID si no se proporciona
        clienteRepository.save(cliente);
        redirectAttributes.addFlashAttribute("successMessage", "Cliente guardado correctamente");
        return "redirect:/clientes/";
    }



    @GetMapping("/delete/{id}")
    public String eliminarCliente(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        if (!clienteRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cliente no encontrado");
            return "redirect:/clientes/";
        }
        clienteRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Cliente eliminado correctamente");
        return "redirect:/clientes/";
    }
}

