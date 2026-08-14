package mintur.serviciomedico.dao;

import mintur.serviciomedico.model.Presentacion;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface PresentacionDao {

    void insertar(Presentacion p) throws SQLException;

    void actualizar(Presentacion p) throws SQLException;

    void eliminar(UUID id) throws SQLException;

    List<Presentacion> listar() throws SQLException;

    boolean existeDescripcion(String descripcion) throws SQLException;
}
