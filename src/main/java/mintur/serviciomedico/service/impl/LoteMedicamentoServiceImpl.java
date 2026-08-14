package mintur.serviciomedico.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import mintur.serviciomedico.dao.LoteMedicamentoDao;
import mintur.serviciomedico.model.LoteMedicamento;
import mintur.serviciomedico.model.LoteMedicamentoFull;
import mintur.serviciomedico.service.LoteMedicamentoService;

import java.util.List;
import java.util.UUID;

@Service
public class LoteMedicamentoServiceImpl implements LoteMedicamentoService {

    private final LoteMedicamentoDao dao;

    @Autowired
    public LoteMedicamentoServiceImpl(LoteMedicamentoDao dao) {
        this.dao = dao;
    }

    private String getUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "SISTEMA";
    }

    // ============================================================
    // MÉTODOS DE CONSULTA
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<LoteMedicamento> listarLotesDisponibles(String uuidMedicamento) throws Exception {
        return dao.listarLotesDisponibles(uuidMedicamento);
    }

    @Override
    @Transactional(readOnly = true)
    public LoteMedicamentoFull buscarPorUUID(String uuidLote) throws Exception {
        LoteMedicamento lote = dao.buscarPorUUID(uuidLote);
        if (lote instanceof LoteMedicamentoFull) {
            return (LoteMedicamentoFull) lote;
        }
        throw new Exception("El lote encontrado no corresponde al formato detallado (Full).");
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteMedicamentoFull> listarPorProveedor(String proveedor) throws Exception {
        return dao.listarPorProveedor(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteMedicamentoFull> listarPorFactura(UUID uuidFactura) throws Exception {
        return dao.listarPorFactura(uuidFactura.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteMedicamentoFull> listarLotesPorMedicamento(String uuidMedicamento) throws Exception {
        return dao.listarPorMedicamento(uuidMedicamento);
    }

    // ============================================================
    // MÉTODOS DE ESCRITURA Y AUDITORÍA
    // ============================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registrarLote(LoteMedicamentoFull lote) throws Exception {
        validarDatos(lote);
        
        if (lote.getUuidLote() == null || lote.getUuidLote().isBlank()) {
            lote.setUuidLote(UUID.randomUUID().toString());
        }
        
        lote.setStockDisponible(lote.getStockInicial());
        lote.setActivo(true);
        
        dao.insertar(lote);
        /*
        dao.registrarMovimiento(
        lote.getUuidLote(), // Ya es String
        lote.getUuidMedicamento(), // Ya es String
        "ENTRADA", 
        lote.getStockInicial(), 
        getUsuarioActual(), 
        "Carga inicial: " + lote.getNumeroLote()
    );
        */
        dao.registrarBitacoraGeneral(getUsuarioActual(), "FARMACIA", "INSERTAR", null, 
            "Nuevo lote: " + lote.getNumeroLote(), "0.0.0.0", "Registro inicial", lote.getUuidLote(), "medicamento_lotes");
    }

  @Override
    @Transactional(rollbackFor = Exception.class)
    public void descontarStock(String uuidLote, int cantidad, String usuario, String referencia) throws Exception {
        LoteMedicamento lote = dao.buscarPorUUID(uuidLote);
        if (lote == null) throw new Exception("Lote no encontrado");
        if (lote.getStockDisponible() < cantidad) throw new Exception("Stock insuficiente");

        dao.actualizarStock(uuidLote, -cantidad); 
        
        /* CORRECCIÓN: Pasar los IDs como String (tal cual vienen del modelo)
        dao.registrarMovimiento(
            uuidLote, 
            lote.getUuidMedicamento(), 
            "SALIDA", 
            cantidad, 
            usuario, 
            referencia
        );
        */
    }

@Override
    @Transactional(rollbackFor = Exception.class)
    public void aumentarStock(String uuidLote, int cantidad, String usuario, String referencia) throws Exception {
        LoteMedicamento lote = dao.buscarPorUUID(uuidLote);
        if (lote == null) throw new Exception("Lote no encontrado");

        dao.actualizarStock(uuidLote, cantidad);
        
        
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizarLote(LoteMedicamentoFull lote) throws Exception {
        validarDatosEdicion(lote);
        dao.actualizar(lote);
        dao.registrarBitacoraGeneral(getUsuarioActual(), "FARMACIA", "ACTUALIZAR", null, "Lote actualizado", "0.0.0.0", "Modificación", lote.getUuidLote(), "medicamento_lotes");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inactivarLote(String uuidLote) throws Exception {
        dao.inactivar(uuidLote);
        dao.registrarBitacoraGeneral(getUsuarioActual(), "FARMACIA", "INACTIVAR", null, "Inactivado", "0.0.0.0", "Baja de lote", uuidLote, "medicamento_lotes");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registrarFactura(List<LoteMedicamentoFull> lotes) throws Exception {
        for (LoteMedicamentoFull lote : lotes) {
            registrarLote(lote);
        }
    }

    private void validarDatos(LoteMedicamentoFull lote) throws Exception {
        if (lote.getUuidMedicamento() == null || lote.getUuidMedicamento().isBlank()) throw new Exception("Medicamento requerido");
        if (lote.getNumeroLote() == null || lote.getNumeroLote().isBlank()) throw new Exception("Número de lote requerido");
        if (lote.getFechaVencimiento() == null) throw new Exception("Fecha de vencimiento requerida");
        if (lote.getStockInicial() <= 0) throw new Exception("Stock inicial inválido");
    }

    private void validarDatosEdicion(LoteMedicamentoFull lote) throws Exception {
        if (lote.getUuidLote() == null) throw new Exception("Identificador de lote requerido");
    }
}