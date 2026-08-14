package mintur.serviciomedico.dao;

import java.sql.SQLException;
import java.util.List;

import mintur.serviciomedico.model.ControlDiarioMedicamento;

public interface ControlDiarioMedicamentoDao {

    boolean existeEntrega(String uuidControl, String uuidMedicamento, String uuidLote) throws SQLException;

    void insertar(ControlDiarioMedicamento m) throws SQLException;

    List<ControlDiarioMedicamento> listarPorControl(String uuidControl) throws SQLException;
         
    /*List<ControlDiarioMedicamento> listarPorPaciente(String uuidTitular, String uuidFamiliar) throws SQLException;*/
    
    List<ControlDiarioMedicamento> listarPorPaciente(String uuidControl, String uuidFamiliar) throws SQLException;
}

