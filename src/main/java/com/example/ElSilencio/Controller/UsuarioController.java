    package com.example.ElSilencio.Controller;

    import com.example.ElSilencio.Model.UsuarioModel;
    import com.example.ElSilencio.Service.UsuarioService;
    import jakarta.validation.Valid;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.validation.BindingResult;
    import org.springframework.web.bind.annotation.*;

    @Controller
    @RequestMapping("/usuarios")

    public class UsuarioController {
        private final UsuarioService usuarioService;

        public UsuarioController(UsuarioService usuarioService) {
            this.usuarioService = usuarioService;
        }

        @GetMapping
        public String listarUsuarios(Model model) {
            model.addAttribute("usuarios", usuarioService.findAll());
            return "usuarios/lista";
        }

        @GetMapping("/nuevo")
        public String nuevoUsuarioForm(Model model) {
            model.addAttribute("usuario", new UsuarioModel());
            return "usuarios/form";
        }

        // Guardar usuario
        @PostMapping("/guardar")
        public String guardarUsuario(@Valid  @ModelAttribute("usuario") UsuarioModel usuario, BindingResult result, Model model) {
            if (result.hasErrors()) {
                return "usuarios/form";
            }
            if (usuario.getId() == null && usuarioService.existsByUsername(usuario.getUsername())) {
                model.addAttribute("erro", "El Usuario ya existe");
                return "usuarios/form";
            }
            usuarioService.save(usuario);
            return "redirect:/usuarios";
        }

        // Editar usuario
        @GetMapping("/editar/{id}")
        public String editarUsuario(@PathVariable Long id, Model model) {
            UsuarioModel usuario = usuarioService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
            model.addAttribute("usuario", usuario);
            return "usuarios/form";
        }

        // Eliminar usuario
        @GetMapping("/eliminar/{id}")
        public String eliminarUsuario(@PathVariable Long id) { //@PathVariable es una anotación
            usuarioService.deleteById(id);
            return "redirect:/usuarios";
        }

    }
