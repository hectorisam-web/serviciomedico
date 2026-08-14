package mintur.serviciomedico.controller;

import mintur.serviciomedico.model.Factura;
import mintur.serviciomedico.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*") // Ajusta según tu configuración de CORS
public class FacturaController {

    private final FacturaService facturaService;

    @Autowired
    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    // 1. Registro: Devuelve la factura creada (vital para el UUID)
    @PostMapping
    public ResponseEntity<Factura> registrar(@RequestBody Factura factura) {
        try {
            facturaService.registrarFactura(factura);
            // Devolvemos la factura que ya tiene el UUID asignado por el DAO
            return ResponseEntity.ok(factura); 
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 2. Listado para la tabla
    @GetMapping
    public ResponseEntity<List<Factura>> listar() {
        try {
            return ResponseEntity.ok(facturaService.listarTodas());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 3. Búsqueda por UUID (útil para ir a cargar los lotes de una factura específica)
    @GetMapping("/{uuid}")
    public ResponseEntity<Factura> buscar(@PathVariable UUID uuid) {
        try {
            Factura f = facturaService.buscarPorUuid(uuid);
            return f != null ? ResponseEntity.ok(f) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}