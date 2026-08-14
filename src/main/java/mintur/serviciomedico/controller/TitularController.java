package mintur.serviciomedico.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.UUID;
import java.sql.SQLException;

import mintur.serviciomedico.model.Titular;
import mintur.serviciomedico.model.TitularFamiliar;
import mintur.serviciomedico.service.TitularService;
import mintur.serviciomedico.service.TitularFamiliarService;

@RestController
@RequestMapping("/api/titulares")
@CrossOrigin(origins = "*")
public class TitularController {

    @Autowired
    private TitularService titularService;

    @Autowired
    private TitularFamiliarService familiarService;

    @GetMapping
    public List<Titular> listarTitulares() throws SQLException {
        return titularService.obtenerTodos(); 
    }

    // Búsqueda dinámica para el buscador principal
    @GetMapping("/search/filter")
    public List<Titular> buscarPorNombre(@RequestParam String nombre) throws SQLException {
        return titularService.buscarPorNombre(nombre); 
    }

    @GetMapping("/cedula/{cedula}")
    public Titular obtenerPorCedula(@PathVariable Integer cedula) throws SQLException {
        return titularService.buscarPorCedula(cedula);
    }

    @GetMapping("/{uuid}")
    public Titular obtenerPorUuid(@PathVariable String uuid) throws SQLException {
        return titularService.buscarPorUuid(uuid);
    }

    // ==========================================================
    // GESTIÓN DEL GRUPO FAMILIAR (Pestaña 2 del Modal)
    // ==========================================================
    
    @GetMapping("/{uuid}/familiares")
    public List<TitularFamiliar> obtenerFamiliares(@PathVariable String uuid) throws SQLException {
        // Ahora devuelve la lista de familiares tipada y con descripciones (Parentesco/Sexo)
        return familiarService.listarPorUuidTitular(uuid);
    }
    /*
    @PostMapping("/familiares")
    public ResponseEntity<?> guardarFamiliar(
            @RequestBody TitularFamiliar familiar, 
            @RequestParam(required = false) UUID operadorId) {
        try {
            // ID de respaldo en caso de que el filtro JWT no lo inyecte directamente en la petición
            UUID userId = (operadorId != null) ? operadorId : UUID.fromString("a32da5c9-f9c9-4c3b-98ce-3120613acf97");
            
            if (familiar.getUuidTitularFamiliar() == null) {
                familiarService.insertar(familiar, userId);
            } else {
                familiarService.actualizar(familiar, userId);
            }
            return ResponseEntity.ok().body("{\"message\": \"Familiar procesado con éxito\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    */
        
    // ==========================================================
    // OPERACIONES DEL TITULAR
    // ==========================================================

    // NUEVO TITULAR (POST)
    @PostMapping
    public ResponseEntity<?> guardarTitular(
            @RequestBody Titular titular, 
            @RequestParam(required = false) UUID operadorId) {
        try {
            UUID userId = (operadorId != null) ? operadorId : UUID.fromString("a32da5c9-f9c9-4c3b-98ce-3120613acf97");
            titularService.guardar(titular, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\": \"Titular registrado exitosamente\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ACTUALIZAR TITULAR EXISTENTE (PUT - Requerido por Vue)
    @PutMapping
    public ResponseEntity<?> actualizarTitular(
            @RequestBody Titular titular, 
            @RequestParam(required = false) UUID operadorId) {
        try {
            UUID userId = (operadorId != null) ? operadorId : UUID.fromString("a32da5c9-f9c9-4c3b-98ce-3120613acf97");
            
            // Invocamos la lógica de actualización de tu capa Service
            titularService.guardar(titular, userId); 
            
            return ResponseEntity.ok().body("{\"message\": \"Ficha de titular actualizada exitosamente\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> eliminarTitular(
            @PathVariable String uuid, 
            @RequestParam(required = false) UUID operadorId) {
        try {
            UUID userId = (operadorId != null) ? operadorId : UUID.fromString("a32da5c9-f9c9-4c3b-98ce-3120613acf97");
            titularService.eliminar(uuid, userId);
            return ResponseEntity.ok().body("{\"info\": \"Registro deshabilitado correctamente\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}