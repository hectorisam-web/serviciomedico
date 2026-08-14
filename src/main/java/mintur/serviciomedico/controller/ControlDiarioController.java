package mintur.serviciomedico.controller;

import jakarta.servlet.http.HttpServletRequest;
import mintur.serviciomedico.model.ControlDiario;
import mintur.serviciomedico.service.ControlDiarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/control-diario")
@CrossOrigin(origins = "*")
public class ControlDiarioController {

    private final ControlDiarioService controlService;

    @Autowired
    public ControlDiarioController(ControlDiarioService controlService) {
        this.controlService = controlService;
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@RequestBody ControlDiario control, HttpServletRequest request) {
       
        try {
            String jsonRecibido = null;
             System.out.println("JSON RECIBIDO: " + jsonRecibido);
            // Pasamos la IP desde el HttpServletRequest
            controlService.guardar(control, request.getRemoteAddr());
            return ResponseEntity.ok("Registro de control diario guardado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar-signos")
    public ResponseEntity<?> actualizarSignos(@RequestBody ControlDiario control, HttpServletRequest request) {
        try {
            controlService.actualizarSignosVitales(control, request.getRemoteAddr());
            return ResponseEntity.ok("Signos vitales actualizados correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar: " + e.getMessage());
        }
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<ControlDiario>> listarHoy() {
        try {
            LocalDate hoy = LocalDate.now(ZoneId.of("America/Caracas"));
            return ResponseEntity.ok(controlService.listarPorFecha(LocalDate.now()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}