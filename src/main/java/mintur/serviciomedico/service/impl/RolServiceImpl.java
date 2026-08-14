package mintur.serviciomedico.service.impl;

import java.util.List;
import java.util.Set;
import mintur.serviciomedico.dao.RolDAO;
import mintur.serviciomedico.model.Rol;
import mintur.serviciomedico.service.RolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la lógica de negocio para la gestión de Roles.
 * @author Hector
 */
@Service
public class RolServiceImpl implements RolService {

    private final RolDAO rolDAO;

    public RolServiceImpl(RolDAO rolDAO) {
        this.rolDAO = rolDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> listarActivos() throws Exception {
        return rolDAO.listarActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarModulosSistema() throws Exception {
        return rolDAO.listarModulosSistema();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> listarTodos() throws Exception {
        return rolDAO.listarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> obtenerPermisos(String uuidRol) throws Exception {
        return rolDAO.obtenerPermisos(uuidRol);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void guardarPermisos(String uuidRol, Set<String> permisos) throws Exception {
        rolDAO.guardarPermisos(uuidRol, permisos);
        // Punto de expansión: Aquí se podría integrar la auditoría/bitácora
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertar(Rol rol) throws Exception {
        rolDAO.insertar(rol);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizar(Rol rol) throws Exception {
        rolDAO.actualizar(rol);
    }
}