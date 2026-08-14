package mintur.serviciomedico.controller;

import jakarta.servlet.http.HttpServletRequest;
import mintur.serviciomedico.model.Medico;
import mintur.serviciomedico.service.MedicoService;
import mintur.serviciomedico.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") 
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<Medico> medicos = medicoService.listarTodos(); 
            return ResponseEntity.ok(medicos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener la lista: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Medico medico, HttpServletRequest request) {
        try {
            String operadorIdStr = (String) request.getAttribute("operadorId");
            if (operadorIdStr == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Sesión inválida"));
            }

            String ip = WebUtils.getClientIp(request);
            UUID operadorId = UUID.fromString(operadorIdStr); 
            
            medicoService.guardar(medico, operadorId, ip);
            return ResponseEntity.status(HttpStatus.CREATED).body(medico);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{uuid}")
public ResponseEntity<?> actualizar(@PathVariable String uuid, @RequestBody Medico medico, HttpServletRequest request) {
    try {
        // 1. CAMBIO CLAVE: Leer desde el Header, no desde el Attribute
        String operadorIdStr = request.getHeader("X-Operador-ID");
        
        if (operadorIdStr == null || operadorIdStr.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No se recibió el encabezado X-Operador-ID"));
        }

        // 2. Preparar datos
        String ip = WebUtils.getClientIp(request);
        medico.setUuidMedico(uuid); 

        // 3. Llamar al service
        medicoService.guardar(medico, UUID.fromString(operadorIdStr), ip);
        
        return ResponseEntity.ok(Map.of("mensaje", "Médico actualizado con éxito"));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Formato de ID de operador inválido"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error en servidor: " + e.getMessage()));
    }
}

    @DeleteMapping("/{uuid}")
public ResponseEntity<?> eliminar(@PathVariable String uuid, HttpServletRequest request) {
    try {
        // ✅ Solución: Leer desde el Header tal como se hace en actualizar
        String operadorIdStr = request.getHeader("X-Operador-ID");
        if (operadorIdStr == null || operadorIdStr.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No se recibió el encabezado X-Operador-ID"));
        }
        
        String ip = WebUtils.getClientIp(request);
        medicoService.eliminar(uuid, UUID.fromString(operadorIdStr), ip);
        
        return ResponseEntity.ok(Map.of("mensaje", "Registro procesado exitosamente."));
    } catch (Exception e) {
        String mensaje = e.getMessage();
        if (mensaje != null && mensaje.contains("INFO_DESHABILITADO")) {
            return ResponseEntity.ok(Map.of("info", mensaje)); 
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", mensaje));
    }
}
}