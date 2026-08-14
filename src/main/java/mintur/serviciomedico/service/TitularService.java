package mintur.serviciomedico.service;

import mintur.serviciomedico.model.Titular;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface TitularService {

    Titular buscarPorCedula(Integer cedula) throws SQLException;

    Titular buscarPorUuid(String uuid) throws SQLException;

    List<Titular> obtenerTodos() throws SQLException; 

    // NUEVO MÉTODO: Para la búsqueda dinámica por texto
    List<Titular> buscarPorNombre(String filtro) throws SQLException;

    List<Object> listarGrupoFamiliar(String uuidTitular) throws SQLException;

    void guardar(Titular t, UUID operadorId) throws SQLException;

    void eliminar(String uuid, UUID operadorId) throws SQLException;
    
    void insertar(Titular t) throws SQLException;
    void actualizar(Titular t) throws SQLException;
}