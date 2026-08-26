package mintur.serviciomedico.controller;

import mintur.serviciomedico.dao.CitaDao;
import mintur.serviciomedico.model.Cita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    @Autowired
    private CitaDao citaDao;

    @GetMapping
    public List<Cita> listarCitas() {
        return citaDao.listarCitas();
    }

    @PostMapping
    public ResponseEntity<?> guardarCita(@RequestBody Cita cita) {
        boolean guardado = citaDao.guardarCita(cita);
        if (guardado) {
            return ResponseEntity.ok(cita);
        }
        return ResponseEntity.badRequest().body("No se pudo guardar la cita en la base de datos.");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCita(@PathVariable UUID id, @RequestBody Cita cita) {
        boolean actualizado = citaDao.actualizarCita(id, cita);
        if (actualizado) {
            return ResponseEntity.ok(cita);
        }
        return ResponseEntity.badRequest().body("No se pudo actualizar la cita.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCita(@PathVariable UUID id) {
        boolean eliminado = citaDao.eliminarCita(id);
        if (eliminado) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}