package mintur.serviciomedico.dao;

import mintur.serviciomedico.model.Titular;
import java.sql.SQLException;
import java.util.List;

public interface TitularDao {
    
    // Cambiado: Ahora coincide exactamente con el nombre en el Impl y Service
    List<Titular> obtenerTodos() throws SQLException;
            
    List<Object> listarGrupoFamiliar(String uuid) throws SQLException;

    // IMPORTANTE: Cambiado a Integer para coincidir con la tabla int4 y el Controller
    Titular buscarPorCedula(Integer cedula) throws SQLException;

    Titular buscarPorUuid(String uuid) throws SQLException;

    void insertar(Titular t) throws SQLException;

    void actualizar(Titular t) throws SQLException;
    
    void eliminar(String uuid) throws SQLException;
}