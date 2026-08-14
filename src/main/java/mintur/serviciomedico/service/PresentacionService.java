package mintur.serviciomedico.service;

import mintur.serviciomedico.model.Presentacion;
import java.util.List;
import java.util.UUID;

public interface PresentacionService {

    void guardar(Presentacion p) throws Exception;

    void actualizar(Presentacion p) throws Exception;

    void eliminar(UUID id) throws Exception;

    List<Presentacion> listar() throws Exception;
}