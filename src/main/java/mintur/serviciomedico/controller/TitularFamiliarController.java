package mintur.serviciomedico.controller;

import jakarta.servlet.http.HttpServletRequest;
import mintur.serviciomedico.model.TitularFamiliar;
import mintur.serviciomedico.service.TitularFamiliarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/familiar")
@CrossOrigin(origins = "*") // Ajustar según tu configuración de seguridad
public class TitularFamiliarController {

    private final TitularFamiliarService familiarService;

    @Autowired
    public TitularFamiliarController(TitularFamiliarService familiarService) {
        this.familiarService = familiarService;
    }

    @GetMapping("/titular/{uuidTitular}")
    public ResponseEntity<List<TitularFamiliar>> listarPorTitular(@PathVariable String uuidTitular) {
        try {
            return ResponseEntity.ok(familiarService.listarPorUuidTitular(uuidTitular));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@RequestBody TitularFamiliar familiar, HttpServletRequest request) {
        try {
            // Aquí deberías obtener el ID del usuario autenticado de tu SecurityContext
            // Por ahora usamos un random para pruebas o el que manejes por defecto
            UUID operadorId = UUID.randomUUID(); 
            
            familiarService.insertar(familiar, operadorId);
            return ResponseEntity.ok("Familiar registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al insertar: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizar(@RequestBody TitularFamiliar familiar, HttpServletRequest request) {
        try {
            // Capturar IP y ID del operador para la bitácora profesional
            String ipCliente = request.getRemoteAddr();
            UUID operadorId = UUID.randomUUID(); // TODO: Obtener del JWT/Session

            familiarService.actualizar(familiar, operadorId);
            return ResponseEntity.ok("Datos del familiar actualizados correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar: " + e.getMessage());
        }
    }
}