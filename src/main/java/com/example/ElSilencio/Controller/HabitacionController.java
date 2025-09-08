package com.example.ElSilencio.Controller;

import com.example.ElSilencio.Model.HabitacionModel;
import com.example.ElSilencio.Service.HabitacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;

    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    @GetMapping
    public String listaHabitacion(Model model){
        model.addAttribute("habitaciones", habitacionService.findAll());
        return "habitaciones/lista";
    }

    @GetMapping("/nuevo")
    public String nuevaHabitacionForm(Model model) {
        model.addAttribute("habitación", new HabitacionModel());
        return "habitaciones/form";
    }

    @PostMapping("/guardar")
    public String guardarHabitacion(@ModelAttribute HabitacionModel habitacion) {
        habitacionService.save(habitacion);
        return "redirect:/habitaciones";
    }
    @GetMapping("/editar/{id}")
    public String editarHabitacion(@PathVariable Long id, Model model) {
        HabitacionModel habitacion = habitacionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("habitación", habitacion);
        return "habitaciones/form";
    }
    @GetMapping("/eliminar/{id}")
    public String eliminarHabitacion(@PathVariable Long id) {
        habitacionService.deleteById(id);
        return "redirect:/habitaciones";
    }
}

