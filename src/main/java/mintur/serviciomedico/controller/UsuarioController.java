package mintur.serviciomedico.controller;

import mintur.serviciomedico.model.Usuario;
import mintur.serviciomedico.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest; // O javax.servlet dependiendo de tu versión

import java.util.List;
import java.util.Map;
import java.util.UUID;
import mintur.serviciomedico.model.PermisosDTO;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        System.out.println(">>> DEBUG: Entrando a UsuarioController.listarTodos()");
        try {
            return ResponseEntity.ok(usuarioService.listarTodos());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
        
    @GetMapping("/{uuid}/permisos")
    public ResponseEntity<PermisosDTO> obtenerPermisos(@PathVariable String uuid) {
    var disponibles = usuarioService.listarTodosLosPermisos();
    var asignados = usuarioService.listarPermisosAsignados(uuid);
    return ResponseEntity.ok(new PermisosDTO(disponibles, asignados));
}
    
    // Registro de nuevo usuario
  @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody Usuario usuario, HttpServletRequest request) {
        try {
            // Obtenemos el ID del admin desde el token (SecurityContext)
            String adminId = SecurityContextHolder.getContext().getAuthentication().getName();

            // Obtenemos la IP desde el objeto request directamente
            String ip = request.getRemoteAddr();

            usuarioService.registrarUsuario(usuario, UUID.fromString(adminId), ip);

            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar: " + e.getMessage());
        }
    }

    // Actualización de usuario existente
 @PutMapping("/actualizar")
public ResponseEntity<String> actualizar(@RequestBody Usuario usuario, HttpServletRequest request) {
    try {
        // 1. Obtener el ID del administrador desde el token ya validado
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // 2. Obtener la IP de forma automática
        String ip = request.getRemoteAddr();
        
        usuarioService.actualizarUsuario(usuario, UUID.fromString(adminId), ip);
        return ResponseEntity.ok("Usuario actualizado exitosamente");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Error al actualizar: " + e.getMessage());
    }
}

    @PostMapping("/actualizar-permisos")
public ResponseEntity<String> actualizarPermisos(@RequestBody Map<String, Object> payload) {
    try {
        String uuidUsuario = (String) payload.get("uuidUsuario");
        // Asegúrate de que el front esté enviando "permisos" como key
        List<String> permisos = (List<String>) payload.get("permisos"); 
        
        System.out.println("DEBUG: Guardando permisos para: " + uuidUsuario);
        
        usuarioService.guardarPermisos(uuidUsuario, permisos);
        
        return ResponseEntity.ok("Permisos actualizados exitosamente");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body("Error al guardar permisos: " + e.getMessage());
    }
}



    @PostMapping("/validar")
    public ResponseEntity<Usuario> login(@RequestBody Map<String, String> credenciales) {
        try {
            Usuario u = usuarioService.login(credenciales.get("usuario"), credenciales.get("pass"));
            return u != null ? ResponseEntity.ok(u) : ResponseEntity.status(401).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
}