package mintur.serviciomedico.service;

import mintur.serviciomedico.model.TitularFamiliar;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Interfaz de Servicio para la gestión del Grupo Familiar de los Titulares.
 * Integra la lógica de negocio y auditoría del sistema.
 */
public interface TitularFamiliarService {

    /**
     * Obtiene todos los familiares asociados a un titular específico.
     * @param uuidTitular UUID del titular padre.
     * @return Lista de familiares activos.
     */
    List<TitularFamiliar> listarPorUuidTitular(String uuidTitular) throws SQLException;

    /**
     * Busca un familiar por su número de cédula.
     * @param cedula Número de documento.
     * @return Objeto TitularFamiliar o null si no existe.
     */
    TitularFamiliar buscarPorCedulaFamiliar(int cedula) throws SQLException;

    /**
     * Busca un registro específico por su UUID único.
     * @param uuid UUID del registro en titular_familiar.
     * @return Objeto TitularFamiliar.
     */
    TitularFamiliar buscarPorUUID(String uuid) throws SQLException;

    /**
     * Registra un nuevo familiar y genera el rastro en la bitácora.
     * @param f Objeto con los datos del familiar.
     * @param operadorId UUID del usuario que realiza la inserción.
     */
    void insertar(TitularFamiliar f, UUID operadorId) throws SQLException;

    /**
     * Actualiza los datos de un familiar existente y registra el cambio en bitácora.
     * @param f Objeto con los datos actualizados.
     * @param operadorId UUID del usuario que realiza la modificación.
     */
    void actualizar(TitularFamiliar f, UUID operadorId) throws Exception;
}