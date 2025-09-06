package com.example.ElSilencio.Controller;

import com.example.ElSilencio.Model.ClienteModel;
import com.example.ElSilencio.Service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        return "clientes/lista";
    }

    // Formulario de nuevo cliente
    @GetMapping("/nuevo")
    public String nuevoClienteForm(Model model) {
        model.addAttribute("cliente", new ClienteModel());
        return "clientes/form";
    }

    // Guardar cliente
    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute ClienteModel cliente) {
        clienteService.save(cliente);
        return "redirect:/clientes";
    }

    // Editar cliente
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model) {
        ClienteModel cliente = clienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("cliente", cliente);
        return "clientes/form";
    }

    // Eliminar cliente
    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        clienteService.delete(id);
        return "redirect:/clientes";
    }
}
