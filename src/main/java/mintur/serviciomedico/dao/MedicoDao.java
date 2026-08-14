package mintur.serviciomedico.dao;

import mintur.serviciomedico.model.Medico;
import java.sql.SQLException;
import java.util.List;

public interface MedicoDao {

    void insertar(Medico m) throws Exception;

    void actualizar(Medico m) throws Exception;

    /** * Elimina físicamente el registro si no tiene dependencias.
     */
    void eliminar(String uuid) throws Exception;

    /** * Verifica si el médico tiene registros en la tabla control_diario.
     */
    boolean tieneConsultas(String uuid);

    /** * Lista todos los médicos (Activos e Inactivos).
     */
    List<Medico> listarTodos() throws SQLException;

    /** * Lista solo médicos con status = true.
     */
    List<Medico> listarActivos() throws SQLException;

    Medico buscarPorUuid(String uuid) throws SQLException;

    Medico buscarPorCedula(int cedula) throws Exception;
}