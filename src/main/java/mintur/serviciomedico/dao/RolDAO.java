package mintur.serviciomedico.dao;

import java.util.List;
import java.util.Set;
import mintur.serviciomedico.model.Rol;

/**
 * Interfaz para las operaciones de acceso a datos de la entidad Rol.
 * @author Hector
 */
public interface RolDAO {

    /**
     * Lista solo los roles que tienen estatus activo (true).
     */
    List<Rol> listarActivos() throws Exception;

    /**
     * Obtiene la lista de módulos disponibles en el sistema para gestión de permisos.
     */
    List<String> listarModulosSistema() throws Exception;

    /**
     * Lista todos los roles existentes, independientemente de su estatus.
     */
    List<Rol> listarTodos() throws Exception;

    /**
     * Inserta un nuevo rol en la base de datos.
     */
    void insertar(Rol rol) throws Exception;

    /**
     * Actualiza la información de un rol existente.
     */
    void actualizar(Rol rol) throws Exception;

    /**
     * Obtiene el conjunto de permisos asignados a un rol específico mediante su UUID.
     */
    Set<String> obtenerPermisos(String uuidRol) throws Exception;

    /**
     * Persiste los permisos asociados a un rol.
     */
    void guardarPermisos(String uuidRol, Set<String> permisos) throws Exception;
}