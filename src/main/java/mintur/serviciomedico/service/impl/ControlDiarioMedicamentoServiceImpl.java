package mintur.serviciomedico.service.impl;

import mintur.serviciomedico.dao.ControlDiarioMedicamentoDao;
import mintur.serviciomedico.dao.LoteMedicamentoDao;
import mintur.serviciomedico.model.ControlDiarioMedicamento;
import mintur.serviciomedico.service.ControlDiarioMedicamentoService;
import mintur.serviciomedico.service.BitacoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ControlDiarioMedicamentoServiceImpl implements ControlDiarioMedicamentoService {

    private final ControlDiarioMedicamentoDao dao;
    private final LoteMedicamentoDao loteDao;
    private final BitacoraService bitacoraService;

    @Autowired
    public ControlDiarioMedicamentoServiceImpl(ControlDiarioMedicamentoDao dao, 
                                               LoteMedicamentoDao loteDao, 
                                               BitacoraService bitacoraService) {
        this.dao = dao;
        this.loteDao = loteDao;
        this.bitacoraService = bitacoraService;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEntrega(String uuidControl, String uuidMedicamento, String uuidLote) throws Exception {
        return dao.existeEntrega(uuidControl, uuidMedicamento, uuidLote);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertar(ControlDiarioMedicamento m, String ip) throws Exception {

        // 1. Validaciones de Negocio
        if (m.getUuidControl() == null || m.getUuidControl().isBlank())
            throw new Exception("El UUID del control es obligatorio.");

        if (m.getUuidMedicamento() == null || m.getUuidMedicamento().isBlank())
            throw new Exception("Debe seleccionar un medicamento.");

        if (m.getUuidLote() == null || m.getUuidLote().isBlank())
            throw new Exception("Debe seleccionar un lote válido.");

        if (m.getCantidad() <= 0)
            throw new Exception("La cantidad entregada debe ser mayor a cero.");

        // 2. Validar duplicado
        if (dao.existeEntrega(m.getUuidControl(), m.getUuidMedicamento(), m.getUuidLote()))
            throw new Exception("Este medicamento y lote ya aparecen como entregados en esta consulta.");

        // 3. Persistencia de la entrega
        dao.insertar(m);

        // 4. DESCUENTO DE STOCK (Crucial para Farmacia)
        // Pasamos la cantidad en negativo para que el UPDATE stock_actual = stock_actual + (?) reste.
        loteDao.actualizarStock(m.getUuidLote(), (m.getCantidad() * -1));

        // 5. Registro en Bitácora Profesional
       String usuarioIdStr = org.springframework.security.core.context.SecurityContextHolder
       .getContext().getAuthentication().getName(); // O el método donde obtengas el UUID del usuario en tu proyecto

        bitacoraService.registrar(
            UUID.fromString(usuarioIdStr), // Pasamos el UUID real del usuario autenticado
            "FARMACIA_ENTREGA",
            "INSERT",
            null,
            "Medicamento: " + m.getDescripcionMedicamento() + " | Cantidad: " + m.getCantidad() + " | Lote: " + m.getNumeroLote(),
            ip,
            "Entrega de medicamento vinculada al control: " + m.getUuidControl(),
            UUID.fromString(m.getUuidControl()),
            "control_diario_medicamento"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ControlDiarioMedicamento> listarPorControl(String uuidControl) throws Exception {
        return dao.listarPorControl(uuidControl);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ControlDiarioMedicamento> listarPorPaciente(String uuidTitular, String uuidFamiliar) throws Exception {
    return dao.listarPorPaciente(uuidTitular, uuidFamiliar);
}
}