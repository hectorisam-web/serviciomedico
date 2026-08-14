package mintur.serviciomedico.service;

import java.util.List;
import java.util.Set;
import mintur.serviciomedico.model.Rol;

public interface RolService {

    List<Rol> listarActivos() throws Exception;

    List<String> listarModulosSistema() throws Exception;

    List<Rol> listarTodos() throws Exception;

    Set<String> obtenerPermisos(String uuidRol) throws Exception;

    void guardarPermisos(String uuidRol, Set<String> permisos) throws Exception;

    void insertar(Rol rol) throws Exception;

    void actualizar(Rol rol) throws Exception;
}
