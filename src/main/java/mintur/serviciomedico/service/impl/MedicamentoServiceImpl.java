package mintur.serviciomedico.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import mintur.serviciomedico.dao.MedicamentoDao;
import mintur.serviciomedico.dao.LoteMedicamentoDao;
import mintur.serviciomedico.model.ItemMaestro;
import mintur.serviciomedico.model.Medicamento;
import mintur.serviciomedico.model.LoteMedicamento;
import mintur.serviciomedico.service.MedicamentoService;
import mintur.serviciomedico.service.BitacoraService;

import java.util.List;
import java.util.UUID;

@Service 
public class MedicamentoServiceImpl implements MedicamentoService {

    private final MedicamentoDao dao;
    private final LoteMedicamentoDao loteDao;
    private final BitacoraService bitacoraService;

    @Autowired 
    public MedicamentoServiceImpl(MedicamentoDao dao, LoteMedicamentoDao loteDao, BitacoraService bitacoraService) {
        this.dao = dao;
        this.loteDao = loteDao;
        this.bitacoraService = bitacoraService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> listar() throws Exception {
        return dao.listar();
    }

    @Override
    @Transactional(readOnly = true)
    public Medicamento buscarPorUUID(String uuid) throws Exception {
        return dao.buscarPorUUID(uuid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void guardar(Medicamento m, String ip) throws Exception {
        if (m.getDescripcionMedicamento() == null || m.getDescripcionMedicamento().isBlank()) {
            throw new Exception("La descripción del medicamento es obligatoria.");
        }

        Medicamento anterior = null;
        String accion;

        if (m.getUuidMedicamento() == null || m.getUuidMedicamento().isBlank()) {
            if (dao.existeDescripcion(m.getDescripcionMedicamento())) {
                throw new Exception("Ya existe un medicamento con esa descripción.");
            }
            accion = "INSERT";

            // Generamos el UUID aquí en el Service para que el objeto 'm' ya lo tenga asignado
            String nuevoUuid = UUID.randomUUID().toString();
            m.setUuidMedicamento(nuevoUuid);

            dao.insertar(m); 
        } else {
            accion = "UPDATE";
            anterior = dao.buscarPorUUID(m.getUuidMedicamento());
            dao.actualizar(m);
        }

        // Recuperamos dinámicamente el UUID del usuario autenticado
        UUID usuarioIdActual = obtenerUsuarioAutenticadoId();

        bitacoraService.registrar(
            usuarioIdActual, // Corregido: Ya no se envía null, viaja el ID del usuario del token
            "FARMACIA_CATALOGO",
            accion,
            anterior != null ? anterior.toString() : null,
            m.toString(),
            ip,
            accion.equals("INSERT") ? "Creación de medicamento: " + m.getDescripcionMedicamento() 
                                   : "Modificación de medicamento: " + m.getDescripcionMedicamento(),
            (m.getUuidMedicamento() != null) ? UUID.fromString(m.getUuidMedicamento()) : null,
            "medicamentos"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemMaestro> listarPresentaciones() throws Exception {
        return dao.listarPresentaciones();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> buscarDescripcionLike(String filtro) throws Exception {
        return dao.buscarDescripcionLike(filtro);
    }

    @Override
    @Transactional(readOnly = true)
    public Medicamento buscarPorDescripcionExacta(String descripcion) throws Exception {
        return dao.buscarPorDescripcionExacta(descripcion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteMedicamento> listarLotesDisponibles(String uuidMedicamento) throws Exception {
        return loteDao.listarLotesDisponibles(uuidMedicamento);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizarStockLote(String uuidLote, int cantidad, String ip) throws Exception {
        LoteMedicamento loteAnterior = loteDao.buscarPorUUID(uuidLote);
        if (loteAnterior == null) throw new Exception("Lote no encontrado.");

        loteDao.actualizarStock(uuidLote, cantidad);

        UUID usuarioIdActual = obtenerUsuarioAutenticadoId();

        // Se usa getStockDisponible() para coincidir con tu modelo
        bitacoraService.registrar(
            usuarioIdActual, // Corregido: Ya no se envía null
            "FARMACIA_INVENTARIO",
            "UPDATE_STOCK",
            "Stock anterior: " + loteAnterior.getStockDisponible(),
            "Ajuste: " + cantidad,
            ip,
            "Ajuste de stock para lote: " + loteAnterior.getNumeroLote(),
            UUID.fromString(uuidLote),
            "lotes_medicamentos"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEntrega(String uuidControl, String uuidMedicamento) throws Exception {
        return loteDao.existeEntrega(uuidControl, uuidMedicamento);
    }

    /**
     * Extrae de forma segura el ID del usuario del contexto de Spring Security / JWT
     */
    private UUID obtenerUsuarioAutenticadoId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // Dependiendo de cómo guardes el ID en tu CustomUserDetails o Principal, extraemos el String.
                // Habitualmente, tu JwtFilter setea el ID del usuario como el principal o dentro del objeto del usuario.
                String principalId = authentication.getName(); 
                return UUID.fromString(principalId);
            }
        } catch (Exception e) {
            System.err.println("Error extrayendo usuario_id para la bitácora: " + e.getMessage());
        }
        return null; // Si no hay sesión (contingencia), aunque idealmente caerá en la validación previa del filtro
    }
}