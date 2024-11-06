package com.ProyectoScrum.app.controller;

import com.ProyectoScrum.app.entity.Usuario;
import com.ProyectoScrum.app.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeTemplateController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String login(Model model) {
        return "login"; // Retorna la vista del formulario de login
    }

    @PostMapping("/usuarios/ingresar")
    public String ingresar(@RequestParam String correo, 
                           @RequestParam String clave, 
                           Model model, 
                           HttpSession session) {
        // Lógica para validar el inicio de sesión
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        // Verifica si el usuario existe
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado");
            return "login"; // Regresa a la vista de login si el usuario no existe
        }

        // Verifica si la clave es correcta
        if (!usuario.getClave().equals(clave)) {
            model.addAttribute("error", "Credenciales inválidas");
            return "login"; // Regresa a la vista de login si la clave es incorrecta
        }

        // Almacenar el rol del usuario en la sesión
        session.setAttribute("rolUsuario", usuario.getRol());

        // Redirige según el rol
        if ("admin".equals(usuario.getRol())) {
            return "redirect:/index"; // Redirige a la página del administrador
        } else {
            return "redirect:/index"; // Redirige a la página principal para usuarios regulares
        }
    }

    @PostMapping("/usuarios/registrar")
    public String registrarUsuario(@RequestParam String correo, 
                                   @RequestParam String clave, 
                                   @RequestParam String rol, 
                                   Model model) {
        // Verifica si el usuario ya existe
        if (usuarioRepository.findByCorreo(correo) != null) {
            model.addAttribute("error", "El correo ya está registrado");
            return "registro"; // Regresa a la vista de registro si el correo ya existe
        }

        // Crea el nuevo usuario y lo guarda en la base de datos
        Usuario nuevoUsuario = new Usuario(correo, clave, rol);
        usuarioRepository.save(nuevoUsuario);
        
        return "redirect:/"; // Redirige al login después de registrarse
    }

    @GetMapping("/usuarios/registro")
    public String mostrarRegistro(Model model) {
        return "registro"; // Retorna la vista del formulario de registro
    }

    @GetMapping("/index")
    public String mostrarIndex(Model model, HttpSession session) {
        String rolUsuario = (String) session.getAttribute("rolUsuario");
        model.addAttribute("rolUsuario", rolUsuario);
        return "index"; // Retorna la vista de la página principal
    }

    @PostMapping("/usuarios/cambiar-contrasena")
    public String cambiarContrasena(@RequestParam String correo, 
                                     @RequestParam String nuevaClave, 
                                     Model model) {
        // Busca al usuario en la base de datos usando solo el correo
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        // Verifica si el usuario existe
        if (usuario == null) {
            model.addAttribute("error", "Usuario no encontrado");
            return "cambiar-contrasena"; // Regresa a la vista si el usuario no existe
        }

        // Actualiza la contraseña del usuario
        usuario.setClave(nuevaClave);
        usuarioRepository.save(usuario);

        model.addAttribute("mensaje", "Contraseña cambiada exitosamente");
        return "redirect:/"; // Redirige al login después de cambiar la contraseña
    }

    @GetMapping("/usuarios/cambiar-contrasena")
    public String mostrarFormularioCambioContrasena(Model model) {
        return "cambiar-contrasena"; // Retorna la vista de cambio de contraseña
    }
}

