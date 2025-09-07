package com.example.ElSilencio.Controller;

import com.example.ElSilencio.Model.PagoModel;
import com.example.ElSilencio.Service.PagoService;
import com.example.ElSilencio.Service.ReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final ReservaService reservaService;

    public PagoController(PagoService pagoService, ReservaService reservaService) {
        this.pagoService = pagoService;
        this.reservaService = reservaService;
    }

    // Listar todos los pagos
    @GetMapping
    public String listarPagos(Model model) {
        model.addAttribute("pagos", pagoService.findAll());
        return "pagos/lista";
    }

    // Formulario de nuevo pago
    @GetMapping("/nuevo")
    public String nuevoPagoForm(Model model) {
        model.addAttribute("pago", new PagoModel());
        model.addAttribute("reservas", reservaService.findAll());
        return "pagos/form";
    }

    // Guardar pago
    @PostMapping("/guardar")
    public String guardarPago(@ModelAttribute PagoModel pago) {
        pagoService.save(pago);
        return "redirect:/pagos";
    }

    // Editar pago
    @GetMapping("/editar/{id}")
    public String editarPago(@PathVariable Long id, Model model) {
        PagoModel pago = pagoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("pago", pago);
        model.addAttribute("reservas", reservaService.findAll());
        return "pagos/form";
    }

    // Eliminar pago
    @GetMapping("/eliminar/{id}")
    public String eliminarPago(@PathVariable Long id) {
        pagoService.deleteByiId(id);
        return "redirect:/pagos";
    }
}
