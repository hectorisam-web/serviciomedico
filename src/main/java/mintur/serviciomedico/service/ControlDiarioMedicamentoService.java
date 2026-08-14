package mintur.serviciomedico.service;

import java.util.List;
import mintur.serviciomedico.model.ControlDiarioMedicamento;

public interface ControlDiarioMedicamentoService {

    /**
     * Registra la entrega, descuenta stock del lote y audita en bitácora.
     * @param m Objeto con la data de entrega
     * @param ip IP del cliente para la bitácora
     * @throws Exception Si no hay stock o hay error de DB
     */
    void insertar(ControlDiarioMedicamento m, String ip) throws Exception;

    List<ControlDiarioMedicamento> listarPorControl(String uuidControl) throws Exception;
    
    List<ControlDiarioMedicamento> listarPorPaciente(String uuidTitular, String uuidFamiliar) throws Exception;
    
    boolean existeEntrega(String uuidControl, String uuidMedicamento, String uuidLote) throws Exception;
}