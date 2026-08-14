package mintur.serviciomedico.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.List;
import mintur.serviciomedico.model.Medicamento;
import mintur.serviciomedico.model.ControlDiarioMedicamento;
import mintur.serviciomedico.service.MedicamentoService;
import mintur.serviciomedico.service.ControlDiarioMedicamentoService;

@RestController
@RequestMapping("/api/medicamentos") // <-- Restaurado en plural para que coincida con Vue
@CrossOrigin(origins = "*")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;
    private final ControlDiarioMedicamentoService controlDiarioMedicamentoService;
    
    @Autowired
    public MedicamentoController(MedicamentoService medicamentoService, ControlDiarioMedicamentoService controlDiarioMedicamentoService) {
        this.medicamentoService = medicamentoService;
        this.controlDiarioMedicamentoService = controlDiarioMedicamentoService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<Medicamento> lista = medicamentoService.listar();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> obtenerPorId(@PathVariable String uuid) {
        try {
            Medicamento m = medicamentoService.buscarPorUUID(uuid);
            return m != null ? ResponseEntity.ok(m) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Medicamento m, HttpServletRequest request) {
        try {
            String ipReal = obtenerIpCliente(request);
            medicamentoService.guardar(m, ipReal);
            return ResponseEntity.ok("{\"status\": \"SUCCESS\", \"message\": \"Operación exitosa\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/control-medicamento")
    public ResponseEntity<?> registrarEntregaControl(@RequestBody ControlDiarioMedicamento payload, HttpServletRequest request) {
        try {
            String ipReal = obtenerIpCliente(request);
            controlDiarioMedicamentoService.insertar(payload, ipReal);
            return ResponseEntity.ok("{\"status\": \"SUCCESS\", \"message\": \"Entrega registrada correctamente\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
        
    @GetMapping("/historico/paciente")
    public ResponseEntity<List<ControlDiarioMedicamento>> listarHistoricoPaciente(
        @RequestParam String uuidTitular, 
        @RequestParam(required = false) String uuidFamiliar) {
        
        System.out.println(">>> [CONTROLLER] Entró a listarHistoricoPaciente | uuidTitular: " + uuidTitular + " | uuidFamiliar: " + uuidFamiliar);
        
    try {
        List<ControlDiarioMedicamento> historico = controlDiarioMedicamentoService.listarPorPaciente(uuidTitular, uuidFamiliar);
        return ResponseEntity.ok(historico);
    } catch (Exception e) {
        return ResponseEntity.internalServerError().build();
    }
    }
    
    @GetMapping({"/lotes/{uuidMedicamento}", "/lote/{uuidMedicamento}"})
    public ResponseEntity<?> listarLotes(@PathVariable String uuidMedicamento) {
        try {
            return ResponseEntity.ok(medicamentoService.listarLotesDisponibles(uuidMedicamento));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    
    @GetMapping("/todos")
    public ResponseEntity<?> listarTodos() {
        try {
            List<Medicamento> lista = medicamentoService.listar();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    private String obtenerIpCliente(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}