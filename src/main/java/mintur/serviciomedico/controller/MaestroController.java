package mintur.serviciomedico.controller;

import jakarta.servlet.http.HttpServletRequest;
import mintur.serviciomedico.model.ItemMaestro;
import mintur.serviciomedico.service.MaestroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maestros")
@CrossOrigin(origins = "http://localhost:5173")
public class MaestroController {

    @Autowired
    private MaestroService maestroService;

    // ============================================================
    // ENDPOINTS DE CONSULTA (NAVEGACIÓN DE JERARQUÍA)
    // ============================================================

    /**
     * Lista todas las categorías principales o raíces (maestro = 0)
     */
    @GetMapping("/padres")
    public ResponseEntity<List<ItemMaestro>> listarPadres() {
        try {
            List<ItemMaestro> padres = maestroService.listarPadres();
            return ResponseEntity.ok(padres);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint específico para los combos modales (Ej: "Presentacion")
     */
    @GetMapping("/hijos-por-raiz/{descripcion}")
public ResponseEntity<List<ItemMaestro>> listarHijosPorRaiz(@PathVariable String descripcion) {
    try {
        System.out.println("Buscando hijos para la raíz: " + descripcion);
        // CAMBIO AQUÍ: Usar el nombre correcto de la interfaz
        List<ItemMaestro> hijos = maestroService.listarHijosPorDescripcionRaiz(descripcion);
        return ResponseEntity.ok(hijos);
    } catch (Exception e) {
        e.printStackTrace(); 
        return ResponseEntity.internalServerError().build();
    }
}

    /**
     * Lista los nodos hijos directos de un UUID padre específico.
     * Crucial para construir árboles dinámicos o grids anidados en el frontend.
     */
    @GetMapping("/hijos/{uuidPadre}")
    public ResponseEntity<List<ItemMaestro>> listarHijosPorUuid(@PathVariable String uuidPadre) {
        try {
            List<ItemMaestro> hijos = maestroService.listarHijos(uuidPadre);
            return ResponseEntity.ok(hijos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene el correlativo sugerido para un nuevo nodo dentro de una rama.
     */
    @GetMapping("/siguiente-ordinal")
    public ResponseEntity<Integer> obtenerSiguienteOrdinal(@RequestParam(required = false) String uuidPadre) {
        try {
            int siguiente = maestroService.obtenerSiguienteOrdinal(uuidPadre);
            return ResponseEntity.ok(siguiente);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============================================================
    // ENDPOINTS DE OPERACIÓN (ESCRITURA Y AUDITORÍA)
    // ============================================================

    /**
     * Guardado unificado: Procesa inserciones (si el uuid viene vacío) o actualizaciones.
     * Captura el objeto de petición http para extraer la IP cliente para la Bitácora.
     */
    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@RequestBody ItemMaestro item, HttpServletRequest request) {
        try {
            String ipCliente = request.getRemoteAddr();
            maestroService.guardar(item, ipCliente);
            return ResponseEntity.ok().body("{\"message\": \"Item maestro procesado con éxito\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Remueve un nodo de la jerarquía por su UUID.
     */
    @DeleteMapping("/eliminar/{uuid}")
    public ResponseEntity<?> eliminar(@PathVariable String uuid, HttpServletRequest request) {
        try {
            String ipCliente = request.getRemoteAddr();
            maestroService.eliminar(uuid, ipCliente);
            return ResponseEntity.ok().body("{\"message\": \"Item maestro eliminado con éxito\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}