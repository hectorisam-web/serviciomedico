package mintur.serviciomedico.service;

import java.util.List;
import java.util.Map;

/**
 * Interfaz para la gestión de permisos asociados a un rol.
 */
public interface RolPermisoService {

    // Obtiene el catálogo completo de permisos del sistema en formato Map (id, descripcion)
    List<Map<String, String>> listarTodosLosPermisos() throws Exception;

    // Obtiene los UUIDs de los permisos asignados a un rol específico
    List<String> listarPermisosPorRol(String uuidRol) throws Exception;

    // Guarda o actualiza los permisos de un rol
    void guardarPermisos(String uuidRol, List<String> permisos) throws Exception;
}
