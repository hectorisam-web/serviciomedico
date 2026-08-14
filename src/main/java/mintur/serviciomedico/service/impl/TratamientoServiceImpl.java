package mintur.serviciomedico.service.impl;

import java.util.List;
import mintur.serviciomedico.dao.TratamientoDao;
import mintur.serviciomedico.model.Tratamiento;
import mintur.serviciomedico.service.TratamientoService;

import java.util.UUID;
import mintur.serviciomedico.service.TratamientoService;


public class TratamientoServiceImpl implements TratamientoService {

    private final TratamientoDao tratamientoDao;

    public TratamientoServiceImpl(TratamientoDao tratamientoDao) {
        this.tratamientoDao = tratamientoDao;
    }

    @Override
    public boolean registrarTratamiento(Tratamiento t) {

        if (t.getTipoTratamiento() == null) {
            throw new IllegalArgumentException("Tipo de tratamiento no puede ser nulo.");
        }

        if (t.getTipoTratamiento().equalsIgnoreCase("posologia")) {
            t.setCantidadTotal(calcularCantidadTotalPosologia(
                    t.getCantidadPorToma(),
                    t.getIntervaloHoras(),
                    t.getDuracionDias()
            ));
        } else if (t.getTipoTratamiento().equalsIgnoreCase("horarios")) {
            t.setCantidadTotal(calcularCantidadTotalHorarios(
                    t.getHorarios(),
                    t.getDuracionDias()
            ));
        } else {
            throw new IllegalArgumentException("Tipo de tratamiento no válido.");
        }

        return tratamientoDao.insertar(t);
    }

    @Override
    public int calcularCantidadTotalPosologia(int cantidadPorToma, int intervaloHoras, int dias) {

        if (cantidadPorToma <= 0) {
            throw new IllegalArgumentException("Cantidad por toma debe ser mayor a cero.");
        }
        if (intervaloHoras <= 0 || intervaloHoras > 24) {
            throw new IllegalArgumentException("Intervalo de horas inválido.");
        }
        if (dias <= 0) {
            throw new IllegalArgumentException("Días debe ser mayor a cero.");
        }

        int tomasPorDia = 24 / intervaloHoras;
        return cantidadPorToma * tomasPorDia * dias;
    }

    @Override
    public int calcularCantidadTotalHorarios(String horarios, int dias) {

        if (horarios == null || horarios.isBlank()) {
            throw new IllegalArgumentException("Debe especificar los horarios.");
        }
        if (dias <= 0) {
            throw new IllegalArgumentException("Días debe ser mayor a cero.");
        }

        String[] lista = horarios.split(",");
        int tomasPorDia = lista.length;

        return tomasPorDia * dias;
    }

    @Override
    public Tratamiento obtenerTratamiento(UUID uuidControl, UUID uuidMedicamento) {
        return tratamientoDao.buscarPorControlYMedicamento(uuidControl, uuidMedicamento);
    }

   
}
