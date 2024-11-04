package com.ProyectoScrum.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.ProyectoScrum.app.entity.Reserva;
import com.ProyectoScrum.app.entity.Cliente;
import com.ProyectoScrum.app.entity.Evento;
import com.ProyectoScrum.app.exception.NotFoundException;
import com.ProyectoScrum.app.repository.ReservaRepository;
import com.ProyectoScrum.app.repository.ClienteRepository;
import com.ProyectoScrum.app.repository.EventoRepository; // Importa el repositorio de Evento

import java.util.List;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EventoRepository eventoRepository; // Agrega el repositorio de Evento

    @GetMapping("/")
    public String listaReservas(Model model) {
        model.addAttribute("reservas", reservaRepository.findAll());
        return "reservas";
    }

    @GetMapping("/new")
    public String nuevaReserva(Model model) {
        model.addAttribute("reserva", new Reserva());

        List<Cliente> clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);

        List<Evento> eventos = eventoRepository.findAll(); // Obtiene todos los eventos
        model.addAttribute("eventos", eventos); // Agrega la lista de eventos al modelo

        return "reservas-formulario"; 
    }

    @GetMapping("/edit/{id}")
    public String editarReserva(@PathVariable("id") String id, Model model) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        model.addAttribute("reserva", reserva);

        List<Cliente> clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);

        List<Evento> eventos = eventoRepository.findAll(); // Obtiene todos los eventos
        model.addAttribute("eventos", eventos); // Agrega la lista de eventos al modelo para la edición

        return "reservas-formulario"; 
    }

    @PostMapping("/save")
    public String guardarReserva(@ModelAttribute("reserva") Reserva reserva) {
        reservaRepository.save(reserva);
        return "redirect:/reservas/";
    }

    @GetMapping("/delete/{id}")
    public String eliminarReserva(@PathVariable("id") String id) {
        reservaRepository.deleteById(id);
        return "redirect:/reservas/";
    }
}
