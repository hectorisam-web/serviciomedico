package mintur.serviciomedico.service.impl;

import mintur.serviciomedico.dao.PresentacionDao;
import mintur.serviciomedico.model.Presentacion;
import mintur.serviciomedico.service.PresentacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PresentacionServiceImpl implements PresentacionService {

    private final PresentacionDao dao;

    @Autowired
    public PresentacionServiceImpl(PresentacionDao dao) {
        this.dao = dao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void guardar(Presentacion p) throws Exception {
        if (p.getDescripcionPresentacion() == null || p.getDescripcionPresentacion().isBlank()) {
            throw new Exception("La descripción no puede estar vacía.");
        }

        if (dao.existeDescripcion(p.getDescripcionPresentacion())) {
            throw new Exception("Ya existe una presentación con esa descripción.");
        }

        dao.insertar(p);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizar(Presentacion p) throws Exception {
        if (p.getDescripcionPresentacion() == null || p.getDescripcionPresentacion().isBlank()) {
            throw new Exception("La descripción no puede estar vacía.");
        }

        dao.actualizar(p);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminar(UUID id) throws Exception { // Cambiado de SQLException a Exception
        dao.eliminar(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Presentacion> listar() throws Exception { // Cambiado de SQLException a Exception
        return dao.listar();
    }
}