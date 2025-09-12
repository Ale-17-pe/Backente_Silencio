package com.example.ElSilencio.Controller;

import com.example.ElSilencio.ClienteModel;
import com.example.ElSilencio.Model.UsuarioModel;
import com.example.ElSilencio.Service.ClienteService;
import com.example.ElSilencio.Service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class RegistroController {

    private final UsuarioService usuarioService;
    private final ClienteService clienteService;


    public RegistroController(UsuarioService usuarioService, ClienteService clienteService) {
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioModel());
        model.addAttribute("cliente", new ClienteModel());
        return "registro"; // templates/registro.html
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute UsuarioModel usuarioModel, @ModelAttribute ClienteModel clienteModel) {
        UsuarioModel usuarioGuardado = usuarioService.save(usuarioModel);

        // Asociar cliente con usuario
        clienteModel.setNombre(usuarioGuardado.getUsername());
        clienteService.save(clienteModel);

        return "redirect:/";
    }
}
