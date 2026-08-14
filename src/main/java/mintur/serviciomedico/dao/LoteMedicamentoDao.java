package mintur.serviciomedico.dao;

import mintur.serviciomedico.model.LoteMedicamento;
import mintur.serviciomedico.model.LoteMedicamentoFull;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface LoteMedicamentoDao {

    // --- ESCRITURA Y GESTIÓN ---

    void insertar(LoteMedicamentoFull lote) throws SQLException;

    void actualizar(LoteMedicamentoFull lote) throws SQLException;
    
    void inactivar(String uuidLote) throws SQLException;
    
    /**
     * Método centralizado para mover stock. 
     * Si la cantidad es negativa resta, si es positiva suma.
     */
    void actualizarStock(String uuidLote, int cantidad) throws SQLException;

    /**
     * REGISTRO HISTÓRICO: Inserta en medicamento_movimientos.
     * @param tipo Debe ser 'ENTRADA' o 'SALIDA' según el CHECK de tu tabla.
   
    void registrarMovimiento(String uuidLote, String uuidMedicamento, String tipo, int cantidad, String usuario, String referencia) throws SQLException;
    *   */
    
    /**
     * Alias por compatibilidad o lógica específica de descuento atómico.
     */
    default void descontarStock(String uuidLote, int cantidad) throws SQLException {
        actualizarStock(uuidLote, (cantidad * -1));
    }
    
    void registrarBitacoraGeneral(String usuarioId, String modulo, String accion, String anterior, String nuevo, String ip, String descripcion, String uuid, String tabla) throws SQLException;
    // --- BÚSQUEDAS ---

    LoteMedicamento buscarPorUUID(String uuidLote) throws SQLException;

    boolean existeEntrega(String uuidControl, String uuidMedicamento) throws SQLException;

    // --- LISTADOS ---

    /**
     * Lista lotes activos y con stock para el proceso de entrega médica.
     */
    List<LoteMedicamento> listarLotesDisponibles(String uuidMedicamento) throws SQLException;

    /**
     * Alias para compatibilidad con servicios existentes.
     */
    default List<LoteMedicamento> listarLotesPorMedicamento(String uuidMedicamento) throws SQLException {
        return listarLotesDisponibles(uuidMedicamento);
    }

    List<LoteMedicamentoFull> listarPorFactura(String uuidFactura) throws SQLException;

    List<LoteMedicamentoFull> listarPorProveedor(String proveedor) throws SQLException;
    
    List<LoteMedicamentoFull> listarTodoDetallado() throws SQLException;
    
    List<LoteMedicamentoFull> listarPorMedicamento(String uuidMedicamento);
}