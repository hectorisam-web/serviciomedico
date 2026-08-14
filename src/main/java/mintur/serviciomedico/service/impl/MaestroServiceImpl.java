package mintur.serviciomedico.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import mintur.serviciomedico.dao.MaestroDao;
import mintur.serviciomedico.model.ItemMaestro;
import mintur.serviciomedico.service.MaestroService;
import mintur.serviciomedico.service.BitacoraService;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Service
public class MaestroServiceImpl implements MaestroService {

    private final MaestroDao maestroDao;
    private final BitacoraService bitacoraService;

    @Autowired
    public MaestroServiceImpl(MaestroDao maestroDao, BitacoraService bitacoraService) {
        this.maestroDao = maestroDao;
        this.bitacoraService = bitacoraService;
    }

    // ============================================================
    // IMPLEMENTACIÓN DE MÉTODOS DE CONSULTA
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public ItemMaestro buscarCategoriaRaiz(String descripcionRaiz) throws Exception {
        return maestroDao.buscarCategoriaRaiz(descripcionRaiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemMaestro> listarHijos(String uuidMaestro) throws Exception {
        return maestroDao.listarHijos(uuidMaestro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemMaestro> listarHijosPorDescripcionRaiz(String descripcionRaiz) throws Exception {
        return maestroDao.listarHijosPorDescripcionRaiz(descripcionRaiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemMaestro> listarPresentaciones() throws Exception {
        return maestroDao.listarPresentaciones();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemMaestro buscarPorUUID(String uuid) throws Exception {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException("El UUID suministrado no es válido.");
        }
        return maestroDao.buscarPorUUID(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemMaestro> listarPadres() throws Exception {
        return maestroDao.listarPadres();
    }

    @Override
    @Transactional(readOnly = true)
    public int obtenerSiguienteOrdinal(String uuidPadre) throws Exception {
        return maestroDao.obtenerSiguienteOrdinal(uuidPadre);
    }

    // ============================================================
    // IMPLEMENTACIÓN DE MÉTODOS ATÓMICOS DE ESCRITURA
    // ============================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertar(ItemMaestro item) throws Exception {
        if (item.getDescripcion() == null || item.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción del ítem es obligatoria.");
        }
        maestroDao.insertar(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizar(ItemMaestro item) throws Exception {
        if (item.getUuid() == null || item.getUuid().trim().isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar un ítem sin su identificador (UUID).");
        }
        maestroDao.actualizar(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminar(String uuid) throws Exception {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException("El UUID para la eliminación es obligatorio.");
        }
        maestroDao.eliminar(uuid);
    }

    // ============================================================
    // OPERACIONES UNIFICADAS CON CONTROL DE AUDITORÍA (BITÁCORA)
    // ============================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void guardar(ItemMaestro item, String ipCliente) throws Exception {
        if (item.getDescripcion() == null || item.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción del maestro es obligatoria.");
        }

        ItemMaestro estadoAnterior = null;
        String accion;
        String detalleAuditoria;

        // Si no tiene UUID asignado, interpretamos que es un registro NUEVO
        if (item.getUuid() == null || item.getUuid().trim().isEmpty()) {
            accion = "INSERT";
            
            // 👉 SOLUCIÓN: Generamos el UUID en Java antes de persistir para asegurar trazabilidad total
            String nuevoUuid = UUID.randomUUID().toString();
            item.setUuid(nuevoUuid);
            
            // Si el frontend no calculó el ordinal, lo recuperamos preventivamente en el Servidor
            if (item.getOrdinal() == null || item.getOrdinal() == 0) {
                item.setOrdinal(maestroDao.obtenerSiguienteOrdinal(item.getUuidMaestro()));
            }
            
            maestroDao.insertar(item);
            detalleAuditoria = "Creación del ítem maestro: " + item.getDescripcion() + " [Código: " + item.getIdCodigo() + "]";
        } else {
            // Si viene con UUID, es una MODIFICACIÓN de datos
            accion = "UPDATE";
            estadoAnterior = maestroDao.buscarPorUUID(item.getUuid());
            if (estadoAnterior == null) {
                throw new Exception("El registro que intenta modificar ya no existe en el sistema.");
            }
            
            maestroDao.actualizar(item);
            detalleAuditoria = "Modificación del ítem maestro: " + item.getDescripcion() + " [UUID: " + item.getUuid() + "]";
        }

        // Recuperar dinámicamente el ID del usuario del contexto de Spring Security
        UUID usuarioIdActual = obtenerUsuarioAutenticadoId();

        // Registro de traza en la Bitácora del Sistema
        bitacoraService.registrar(
            usuarioIdActual,
            "TABLA_MAESTROS",
            accion,
            estadoAnterior != null ? estadoAnterior.toString() : null,
            item.toString(),
            ipCliente,
            detalleAuditoria,
            item.getUuid() != null ? UUID.fromString(item.getUuid()) : null,
            "maestro"
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminar(String uuid, String ipCliente) throws Exception {
        ItemMaestro registroOriginal = maestroDao.buscarPorUUID(uuid);
        if (registroOriginal == null) {
            throw new Exception("El registro que intenta eliminar ya no existe.");
        }

        // Verificación de integridad: Validar si este nodo tiene ramas hijas dependientes
        List<ItemMaestro> hijosDependientes = maestroDao.listarHijos(uuid);
        if (hijosDependientes != null && !hijosDependientes.isEmpty()) {
            throw new Exception("No se puede eliminar este registro porque contiene subcategorías o elementos hijos asociados.");
        }

        maestroDao.eliminar(uuid);

        UUID usuarioIdActual = obtenerUsuarioAutenticadoId();

        bitacoraService.registrar(
            usuarioIdActual,
            "TABLA_MAESTROS",
            "DELETE",
            registroOriginal.toString(),
            null,
            ipCliente,
            "Eliminación física del ítem maestro: " + registroOriginal.getDescripcion(),
            UUID.fromString(uuid),
            "maestro"
        );
    }

    // ============================================================
    // MÉTODO DE SOPORTE INTERNO (SPRING SECURITY EXTRACCIÓN)
    // ============================================================

    private UUID obtenerUsuarioAutenticadoId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // Asume que guardas el UUID del usuario como el string del name principal del token
                return UUID.fromString(authentication.getName());
            }
        } catch (Exception e) {
            System.err.println("Error extrayendo usuario_id en la capa de servicio de maestros: " + e.getMessage());
        }
        return null; // Fallback seguro para la bitácora si corre en hilos anónimos
    }
}