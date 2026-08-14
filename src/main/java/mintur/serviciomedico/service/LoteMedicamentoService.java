package mintur.serviciomedico.service;

import mintur.serviciomedico.model.LoteMedicamento; // Importante
import mintur.serviciomedico.model.LoteMedicamentoFull;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface LoteMedicamentoService {

    // --- ADMINISTRACIÓN DE LOTES (Módulo Farmacia / Full) ---
    void registrarLote(LoteMedicamentoFull lote) throws Exception;
    void actualizarLote(LoteMedicamentoFull lote) throws Exception;
    void inactivarLote(String uuidLote) throws Exception;
    void registrarFactura(List<LoteMedicamentoFull> lotes) throws Exception;
    List<LoteMedicamentoFull> listarPorProveedor(String proveedor) throws Exception;
    List<LoteMedicamentoFull> listarPorFactura(UUID uuidFactura) throws Exception;
    LoteMedicamentoFull buscarPorUUID(String uuidLote) throws Exception;

    // --- CONSULTA CLÍNICA (Módulo Médico / Simple) ---
    
    /**
     * Este es el método que busca el LoteMedicamentoClinicoController.
     * Retorna el modelo LoteMedicamento (el simple) para el frontend clínico.
     */
    List<LoteMedicamento> listarLotesDisponibles(String uuidMedicamento) throws Exception;

    /**
     * Mantiene la compatibilidad si otros servicios usan este nombre.
     */
    List<LoteMedicamentoFull> listarLotesPorMedicamento(String uuidMedicamento) throws Exception;

    // --- OPERACIONES DE STOCK ---

/**
 * Registra una salida de stock y crea automáticamente el registro en el histórico.
 */
void descontarStock(String uuidLote, int cantidad, String usuario, String referencia) throws Exception;

/**
 * Método para ajustes manuales o devoluciones (Suma al stock y registra histórico).
 */
void aumentarStock(String uuidLote, int cantidad, String usuario, String referencia) throws Exception;
}

