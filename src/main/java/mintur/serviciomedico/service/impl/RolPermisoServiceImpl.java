package mintur.serviciomedico.service.impl;

import mintur.serviciomedico.dao.RolPermisoDao;
import mintur.serviciomedico.service.RolPermisoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class RolPermisoServiceImpl implements RolPermisoService {

    private final RolPermisoDao rolPermisoDao;

    public RolPermisoServiceImpl(RolPermisoDao rolPermisoDao) {
        this.rolPermisoDao = rolPermisoDao;
    }

    @Override
    public List<String> listarPermisosPorRol(String uuidRol) throws Exception {
        // Esto ahora devuelve List<String> correctamente
        return rolPermisoDao.listarPermisosPorRol(uuidRol);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void guardarPermisos(String uuidRol, List<String> permisos) throws Exception {
        rolPermisoDao.eliminarPermisos(uuidRol);
        if (permisos != null && !permisos.isEmpty()) {
            rolPermisoDao.insertarPermisos(uuidRol, permisos);
        }
    }
    
    @Override
public List<Map<String, String>> listarTodosLosPermisos() throws Exception {
    // Llamas a tu DAO correspondiente para traer todos los permisos de la BD
    return rolPermisoDao.listarTodosLosPermisos();
}
    
}