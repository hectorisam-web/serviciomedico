package mintur.serviciomedico.controller;

import java.util.List;
import java.util.Map;
import mintur.serviciomedico.model.PermisosDTO;
import mintur.serviciomedico.model.Rol;
import mintur.serviciomedico.service.RolService;
import mintur.serviciomedico.service.RolPermisoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*") 
public class RolController {

    private static final Logger logger = LoggerFactory.getLogger(RolController.class);
    private final RolService rolService;
    private final RolPermisoService rolPermisoService;

    public RolController(RolService rolService, RolPermisoService rolPermisoService) {
        this.rolService = rolService;
        this.rolPermisoService = rolPermisoService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Rol>> listarTodos() {
        // Envolvemos en try-catch porque el servicio puede lanzar Exception
        try {
            return ResponseEntity.ok(rolService.listarTodos());
        } catch (Exception e) {
            logger.error("Error al listar roles", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/guardar")
    public ResponseEntity<Map<String, String>> guardar(@RequestBody Rol rol) {
        try {
            if (rol.getUuidRol() == null || rol.getUuidRol().isEmpty()) {
                rolService.insertar(rol);
            } else {
                rolService.actualizar(rol);
            }
            return ResponseEntity.ok(Map.of("mensaje", "Rol guardado exitosamente"));
        } catch (Exception e) {
            logger.error("Error al guardar rol", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{uuidRol}/permisos")
    public ResponseEntity<PermisosDTO> obtenerPermisos(@PathVariable String uuidRol) {
        try {
            // 1. Obtienes el catálogo general de permisos disponibles en el sistema (List<Map<String, String>>)
            List<Map<String, String>> disponibles = rolPermisoService.listarTodosLosPermisos();
            
            // 2. Obtienes los UUIDs de los permisos que ya tiene asignados este rol (List<String>)
            List<String> asignados = rolPermisoService.listarPermisosPorRol(uuidRol);

            // 3. Envuelves ambos en tu PermisosDTO y los retornas al frontend
            PermisosDTO permisosDto = new PermisosDTO(disponibles, asignados);
            return ResponseEntity.ok(permisosDto);
        } catch (Exception e) {
            logger.error("Error al obtener permisos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{uuidRol}/permisos")
    public ResponseEntity<Map<String, String>> guardarPermisos(
            @PathVariable String uuidRol, 
            @RequestBody List<String> permisos) {
        try {
            // Aquí estaban los otros errores de "unreported exception"
            rolPermisoService.guardarPermisos(uuidRol, permisos);
            return ResponseEntity.ok(Map.of("mensaje", "Permisos actualizados correctamente"));
        } catch (Exception e) {
            logger.error("Error al guardar permisos", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}