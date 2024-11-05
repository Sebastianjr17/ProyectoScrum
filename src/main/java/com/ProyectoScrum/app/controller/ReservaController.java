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
import com.ProyectoScrum.app.repository.EventoRepository;
import com.ProyectoScrum.app.entity.ReservaDTO;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping("/")
    public String listaReservas(Model model) {
        List<Reserva> reservas = reservaRepository.findAll();
        List<ReservaDTO> reservaDTOs = new ArrayList<>();

        for (Reserva reserva : reservas) {
            Cliente cliente = clienteRepository.findById(reserva.getClienteId()).orElse(null);
            Evento evento = eventoRepository.findById(reserva.getEventoId()).orElse(null);

            reservaDTOs.add(new ReservaDTO(reserva.getId(), 
                                             cliente != null ? cliente.getNombre() : "Desconocido",
                                             evento != null ? evento.getNombre() : "Desconocido", 
                                             reserva.getCantidadEntradas()));
        }

        model.addAttribute("reservas", reservaDTOs);
        return "reservas"; // nombre de la vista
    }

    @GetMapping("/new")
    public String nuevaReserva(Model model) {
        model.addAttribute("reserva", new Reserva());

        List<Cliente> clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);

        List<Evento> eventos = eventoRepository.findAll();
        model.addAttribute("eventos", eventos);

        return "reservas-formulario"; 
    }

    @GetMapping("/edit/{id}")
    public String editarReserva(@PathVariable("id") String id, Model model) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));
        model.addAttribute("reserva", reserva);

        List<Cliente> clientes = clienteRepository.findAll();
        model.addAttribute("clientes", clientes);

        List<Evento> eventos = eventoRepository.findAll();
        model.addAttribute("eventos", eventos);

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
