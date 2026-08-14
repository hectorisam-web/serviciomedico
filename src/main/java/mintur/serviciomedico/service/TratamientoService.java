package mintur.serviciomedico.service;

import java.util.List;
import mintur.serviciomedico.model.Tratamiento;
import java.util.UUID;
import mintur.serviciomedico.model.Medicamento;

public interface TratamientoService {

    boolean registrarTratamiento(Tratamiento t);

    int calcularCantidadTotalPosologia(int cantidadPorToma, int intervaloHoras, int dias);

    int calcularCantidadTotalHorarios(String horarios, int dias);
    
     Tratamiento obtenerTratamiento(UUID uuidControl, UUID uuidMedicamento);
}
