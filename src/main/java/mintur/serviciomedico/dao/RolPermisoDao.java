package mintur.serviciomedico.dao;

import java.util.List;
import java.util.Map;

public interface RolPermisoDao {

    List<String> listarPermisosPorRol(String uuidRol) throws Exception;

    void eliminarPermisos(String uuidRol) throws Exception;

    void insertarPermisos(String uuidRol, List<String> permisos) throws Exception;
    List<Map<String, String>> listarTodosLosPermisos() throws Exception;
}
