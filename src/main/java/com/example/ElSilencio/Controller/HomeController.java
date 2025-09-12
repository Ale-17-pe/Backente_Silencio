package com.example.ElSilencio.Controller;

import com.example.ElSilencio.Model.ClienteModel;
import com.example.ElSilencio.Service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class HomeController {

    private final ClienteService clienteService;

    public HomeController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Página principal
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        ClienteModel clienteLogueado = (ClienteModel) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuario", clienteLogueado);
        return "index"; // templates/index.html
    }

    // Mostrar login
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("error", "");
        return "login"; // templates/login.html
    }

    // Procesar login
    @PostMapping("/login")
    public String login(@RequestParam String dni,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<ClienteModel> cliente = clienteService.findByDni(dni);

        if (cliente.isPresent() && cliente.get().getUsuario().getPassword().equals(password)) {
            session.setAttribute("usuarioLogueado", cliente.get());
            return "redirect:/dashboard"; // <- Ahora redirige al dashboard
        } else {
            model.addAttribute("error", "DNI o contraseña incorrectos");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        ClienteModel clienteLogueado = (ClienteModel) session.getAttribute("usuarioLogueado");

        if (clienteLogueado == null) {
            return "redirect:/login"; // Si no hay sesión, redirige al login
        }

        model.addAttribute("usuario", clienteLogueado);
        // Si tienes reservas: model.addAttribute("reservas", reservaService.findByCliente(clienteLogueado));

        return "clientes/dashboard"; // templates/clientes/dashboard.html
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // Redirigir botón "Registrarse"
    @GetMapping("/registrar")
    public String registrar() {
        return "redirect:/clientes/registrar";
    }
}
