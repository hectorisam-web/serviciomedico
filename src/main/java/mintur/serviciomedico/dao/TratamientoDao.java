package mintur.serviciomedico.dao;

import mintur.serviciomedico.model.Tratamiento;

import java.util.List;
import java.util.UUID;
import mintur.serviciomedico.model.Medicamento;

public interface TratamientoDao {

    boolean insertar(Tratamiento t);

    List<Tratamiento> listarPorControl(UUID uuidControl);
    
       Tratamiento buscarPorControlYMedicamento(UUID uuidControl, UUID uuidMedicamento);
}
