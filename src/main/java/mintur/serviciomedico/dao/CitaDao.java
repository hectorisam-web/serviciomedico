package mintur.serviciomedico.dao;

import mintur.serviciomedico.model.Cita;
import java.util.List;
import java.util.UUID;

public interface CitaDao {
    List<Cita> listarCitas();
    boolean actualizarCita(UUID uuidCita, Cita cita);
    boolean guardarCita(Cita cita);
    boolean eliminarCita(UUID id);
}