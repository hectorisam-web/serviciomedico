package mintur.serviciomedico.service.impl;

import mintur.serviciomedico.dao.TitularFamiliarDao;
import mintur.serviciomedico.model.TitularFamiliar;
import mintur.serviciomedico.service.TitularFamiliarService;
import mintur.serviciomedico.service.BitacoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class TitularFamiliarServiceImpl implements TitularFamiliarService {

    private final TitularFamiliarDao familiarDao;
    private final BitacoraService bitacoraService;

    @Autowired
    public TitularFamiliarServiceImpl(TitularFamiliarDao familiarDao, BitacoraService bitacoraService) {
        this.familiarDao = familiarDao;
        this.bitacoraService = bitacoraService;
    }

    @Override
    public List<TitularFamiliar> listarPorUuidTitular(String uuidTitular) throws SQLException {
        return familiarDao.listarPorUuidTitular(uuidTitular);
    }

    @Override
    public TitularFamiliar buscarPorCedulaFamiliar(int cedula) throws SQLException {
        return familiarDao.buscarPorCedulaFamiliar(cedula);
    }

    @Override
    public TitularFamiliar buscarPorUUID(String uuid) throws SQLException {
        return familiarDao.buscarPorUUID(uuid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertar(TitularFamiliar f, UUID operadorId) throws SQLException {
        // Asegurar UUID antes de insertar
        if (f.getUuidTitularFamiliar() == null || f.getUuidTitularFamiliar().isEmpty()) {
            f.setUuidTitularFamiliar(UUID.randomUUID().toString());
        }

        familiarDao.insertar(f);

        // Registro detallado en Bitácora
        bitacoraService.registrar(
            operadorId,
            "SERVICIO_MEDICO",      // modulo
            "INSERT",               // accion
            null,                   // anterior (es nuevo, no hay estado previo)
            f.toString(),           // nuevo
            "127.0.0.1",            // ip (esto deberías capturarlo del contexto web)
            "Registro de nuevo familiar: " + f.getNombresFamiliar(),
            UUID.fromString(f.getUuidTitularFamiliar()),
            "TITULAR_FAMILIAR"      // tabla
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizar(TitularFamiliar f, UUID operadorId) throws Exception {
        // 1. Obtener estado anterior para la bitácora
        TitularFamiliar anterior = familiarDao.buscarPorUUID(f.getUuidTitularFamiliar());
        
        if (anterior == null) {
            throw new Exception("No se encontró el registro para actualizar.");
        }

        // 2. Ejecutar actualización
        familiarDao.actualizar(f);

        // 3. Registro detallado en Bitácora
        bitacoraService.registrar(
            operadorId,
            "SERVICIO_MEDICO",      // modulo
            "UPDATE",               // accion
            anterior.toString(),    // anterior
            f.toString(),           // nuevo
            "127.0.0.1",            // ip
            "Actualización de datos del familiar C.I: " + f.getCedulaFamiliar(),
            UUID.fromString(f.getUuidTitularFamiliar()),
            "TITULAR_FAMILIAR"      // tabla
        );
    }
}