package mintur.serviciomedico.controller;

import mintur.serviciomedico.model.LoteMedicamento;
import mintur.serviciomedico.service.LoteMedicamentoService;
import mintur.serviciomedico.model.LoteMedicamentoFull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")

public class LoteMedicamentoClinicoController {

    // Cambiamos el nombre a 'service' para ser consistentes
    private final LoteMedicamentoService service;

    @Autowired
    public LoteMedicamentoClinicoController(LoteMedicamentoService service) {
        this.service = service;
    }

    // 1. Obtener lotes disponibles
    @GetMapping("/lotes/{uuidMedicamento}")
    public ResponseEntity<List<LoteMedicamento>> listarLotes(@PathVariable String uuidMedicamento) {
        try {
            List<LoteMedicamento> lotes = service.listarLotesDisponibles(uuidMedicamento);
            return ResponseEntity.ok(lotes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 2. Obtener stock total
    @GetMapping("/stock-total/{uuidMedicamento}")
    public ResponseEntity<Integer> obtenerStockTotal(@PathVariable String uuidMedicamento) {
        try {
            int total = service.listarLotesDisponibles(uuidMedicamento)
                               .stream()
                               .mapToInt(LoteMedicamento::getStockDisponible)
                               .sum();
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(0);
        }
    }

    // 3. Endpoint para descontar stock
    @PostMapping("/descontar")
    public ResponseEntity<?> descontar(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Extraer los datos del JSON
            String uuidLote = (String) payload.get("uuidLote");
            int cantidad = Integer.parseInt(payload.get("cantidad").toString());
            
            // CORRECCIÓN: Usamos 'service' (no servicio) y pasamos 'uuidLote' (no uuid)
            service.descontarStock(
                uuidLote, 
                cantidad, 
                "USUARIO_MEDICO", 
                "Entrega de medicina en consulta"
            );
            
            return ResponseEntity.ok().body(Map.of("message", "Stock actualizado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 4. Registro de un nuevo lote (Entrada de inventario)
        @PostMapping("/lotes")
        public ResponseEntity<?> registrarLote(@RequestBody LoteMedicamentoFull lote) {
        try {
            service.registrarLote(lote); // <--- 2. AHORA USAS EL MÉTODO CORRECTO
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    
    // 5  
    @PostMapping("/registrar-lote")
    public ResponseEntity<?> registrarLoteEntrada(@RequestBody LoteMedicamentoFull lote) {
    try {
        service.registrarLote(lote); // Usando tu interfaz
        return ResponseEntity.ok(Map.of("message", "Lote registrado y stock actualizado"));
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
    }
}
    // 6 Registra Lista de Medicamentos 
    @PostMapping("/lotes/registrar-lista")
    public ResponseEntity<?> registrarLista(@RequestBody List<LoteMedicamentoFull> listaLotes) {
    // AÑADE ESTO PARA DEPURAR
    String username = org.springframework.security.core.context.SecurityContextHolder
                      .getContext().getAuthentication().getName();
    System.out.println("DEBUG - Usuario intentando registrar lista: " + username);
    
    try {
        for (LoteMedicamentoFull lote : listaLotes) {
            service.registrarLote(lote);
        }
        return ResponseEntity.ok(Map.of("message", "Factura procesada con éxito"));
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
    }
}
    
    
}