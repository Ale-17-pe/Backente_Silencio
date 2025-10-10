package com.example.ElSilencio.Controller;

import com.example.ElSilencio.Model.ClienteModel;
import com.example.ElSilencio.Service.ClienteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/nuevo")
    public String nuevoClienteForm(Model model) {
        model.addAttribute("cliente", new ClienteModel());
        return "clientes/form";
    }

    // Guardar cliente y lo actuliza
    @PostMapping("/guardar")
    public String guardarCliente(@Valid @ModelAttribute("cliente") ClienteModel cliente,
                                 BindingResult result,
                                 Model model,
                                 HttpSession session)  {
        if (result.hasErrors()) {
            return "clientes/form";
        }

        if (cliente.getId() == null && clienteService.existsByDni(cliente.getDni())) {
            model.addAttribute("error", "El DNI ya está registrado.");
            return "clientes/form";
        }

        clienteService.save(cliente);

        // Login automático
        session.setAttribute("usuarioLogueado", cliente);

        return "redirect:/"; // redirige al home
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
        clienteService.deleteById(id);
        return "redirect:/clientes";
    }

    @GetMapping("/registrar")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new ClienteModel());
        return "clientes/form";
    }


}
