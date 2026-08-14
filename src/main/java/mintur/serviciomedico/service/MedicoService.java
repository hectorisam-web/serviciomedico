package mintur.serviciomedico.service;

import mintur.serviciomedico.model.Medico;
import java.util.List;
import java.util.UUID;

public interface MedicoService {
    void guardar(Medico m, UUID operadorId, String ip) throws Exception;
    void eliminar(String uuid, UUID operadorId, String ip) throws Exception;
    List<Medico> listarTodos() throws Exception;
    List<Medico> listarActivos() throws Exception;
    Medico buscarPorUuid(String uuid) throws Exception;
    Medico buscarPorCedula(int cedula) throws Exception;
}