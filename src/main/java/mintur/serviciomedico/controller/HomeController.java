package mintur.serviciomedico.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

@GetMapping("/home")
public String mostrarMenu(Model model, java.security.Principal principal) {
    // Obtenemos el nombre del usuario logueado
    String nombre = (principal != null) ? principal.getName() : "Usuario";
    
    model.addAttribute("titulo", "Panel de Control Médico");
    model.addAttribute("usuario", nombre); // Esta es la variable que mostrarás
    return "home";
}
    
    @GetMapping("/login")
    public String login() {
        return "login"; // Por si necesitas definir la vista de login manual
    }
}
