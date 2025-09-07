package com.example.ElSilencio.Controller;

import com.example.ElSilencio.Model.ReservaModel;
import com.example.ElSilencio.Service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("reservas")
public class ReservaController {
    private final ReservaService reservaService;
    private final ClienteService clienteService;
    private final HabitacionService habitacionService;

    public ReservaController(ReservaService reservaService, ClienteService clienteService, HabitacionService habitacionService) {
        this.reservaService = reservaService;
        this.clienteService = clienteService;
        this.habitacionService = habitacionService;
    }
    @GetMapping
    public String listarReservas(Model model) {
        model.addAttribute("reservas", reservaService.findAll());
        return "reservas/lista";
    }
    @GetMapping("/nuevo")
    public String nuevaReservaForm(Model model) {
        model.addAttribute("reserva", new ReservaModel());
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("habitaciones", habitacionService.findAll());
        return "reservas/form";
    }
    @PostMapping("/guardar")
    public String guardarReserva(@ModelAttribute ReservaModel reserva) {
        reservaService.save(reserva);
        return "redirect:/reservas";
    }
    @GetMapping("/editar/{id}")
    public String editarReserva(@PathVariable Long id, Model model) {
        ReservaModel reserva = reservaService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("reserva", reserva);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("habitaciones", habitacionService.findAll());
        return "reservas/form";
    }
    @GetMapping("/eliminar/{id}")
    public String eliminarReserva(@PathVariable Long id) {
        reservaService.deleteById(id);
        return "redirect:/reservas";
    }
}

