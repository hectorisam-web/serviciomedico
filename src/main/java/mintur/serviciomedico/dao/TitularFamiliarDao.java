package mintur.serviciomedico.dao;

import mintur.serviciomedico.model.TitularFamiliar;

import java.sql.SQLException;
import java.util.List;

public interface TitularFamiliarDao {

    List<TitularFamiliar> listarPorUuidTitular(String uuidTitular) throws SQLException;

    void insertar(TitularFamiliar f) throws SQLException;

    TitularFamiliar buscarPorCedulaFamiliar(int cedula) throws SQLException;

    void actualizar(TitularFamiliar f) throws Exception;

    TitularFamiliar buscarPorUUID(String uuid) throws SQLException;
}
