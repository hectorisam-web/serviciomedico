package mintur.serviciomedico.dao;

import mintur.serviciomedico.model.ControlDiario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ControlDiarioDao {

    List<ControlDiario> listar() throws SQLException;

    List<ControlDiario> listarPorTitular(String uuidTitular) throws SQLException;

    ControlDiario buscarPorUUID(String uuid) throws SQLException;

    List<ControlDiario> listarPorFecha(LocalDate fecha) throws SQLException;

    void actualizarSignosVitales(ControlDiario c) throws SQLException;

    void insertar(ControlDiario c) throws SQLException;

    // Campo especial que usas en formularios
    String getUuidConsultorio();
    void setUuidConsultorio(String uuidConsultorio);
}
