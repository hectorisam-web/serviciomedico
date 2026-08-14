package mintur.serviciomedico.controller;

import jakarta.servlet.http.HttpServletRequest;
import mintur.serviciomedico.model.ControlDiarioMedicamento;
import mintur.serviciomedico.service.ControlDiarioMedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/control-medicamento")
@CrossOrigin(origins = "*") // Ajustar según tu configuración de seguridad
public class ControlDiarioMedicamentoController {

    private final ControlDiarioMedicamentoService service;

    @Autowired
    public ControlDiarioMedicamentoController(ControlDiarioMedicamentoService service) {
        this.service = service;
    }

    /**
     * Lista los medicamentos entregados en una consulta específica.
     */
    @GetMapping("/control/{uuidControl}")
    public ResponseEntity<?> listarPorControl(@PathVariable String uuidControl) {
    try {
        System.out.println("=== [CONTROLLER] Entró a listarPorControl con ID: " + uuidControl);
        List<ControlDiarioMedicamento> lista = service.listarPorControl(uuidControl);
        System.out.println("=== [CONTROLLER] Registros obtenidos del service: " + (lista != null ? lista.size() : "null"));
        return ResponseEntity.ok(lista);
    } catch (Exception e) {
        // 🛑 IMPRIME LA TRAZA COMPLETA EN LA CONSOLA PARA VER EL ERROR REAL
        System.out.println("=== [ERROR CRÍTICO EN CONTROLLER] ===");
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("Error al listar: " + e.getMessage());
    }
    }

    /**
     * Registra una nueva entrega de medicamento y descuenta stock.
     */
    @PostMapping("/entregar")
    public ResponseEntity<?> entregar(@RequestBody ControlDiarioMedicamento cdm, HttpServletRequest request) {
        try {
            // Capturamos la IP para la Bitácora Profesional
            String ip = request.getRemoteAddr();
            
            service.insertar(cdm, ip);
            
            return ResponseEntity.ok("{\"mensaje\": \"Medicamento entregado correctamente\"}");
        } catch (Exception e) {
            // Aquí capturamos los throw new Exception del Service (duplicados, falta de stock, etc.)
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Historial de medicamentos de un paciente (Titular o Familiar).
     */
    @GetMapping("/historial")
    public ResponseEntity<?> historial(@RequestParam String uuidTitular, 
                                       @RequestParam(required = false) String uuidFamiliar) {
        try {
            List<ControlDiarioMedicamento> historial = service.listarPorPaciente(uuidTitular, uuidFamiliar);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener historial: " + e.getMessage());
        }
    }
}