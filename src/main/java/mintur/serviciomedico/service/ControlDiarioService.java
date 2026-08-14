package mintur.serviciomedico.service;

import mintur.serviciomedico.model.ControlDiario;
import java.time.LocalDate;
import java.util.List;

public interface ControlDiarioService {
    void guardar(ControlDiario c, String ip) throws Exception;
    void actualizarSignosVitales(ControlDiario c, String ip) throws Exception;
    ControlDiario buscarPorUUID(String uuid) throws Exception;
    List<ControlDiario> listar() throws Exception;
    List<ControlDiario> listarPorTitular(String uuidTitular) throws Exception;
    List<ControlDiario> listarPorFecha(LocalDate fecha) throws Exception;
    String getUuidConsultorio();
    void setUuidConsultorio(String uuidConsultorio);
}