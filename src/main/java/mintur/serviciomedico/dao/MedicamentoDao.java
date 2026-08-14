package mintur.serviciomedico.dao;

import mintur.serviciomedico.model.ItemMaestro;
import mintur.serviciomedico.model.Medicamento;

import java.sql.SQLException;
import java.util.List;

public interface MedicamentoDao {

    // ---------------------------------------------------------
    // LISTAR
    // ---------------------------------------------------------
    List<Medicamento> listar() throws SQLException;

    // ---------------------------------------------------------
    // PRESENTACIONES (CATÁLOGO)
    // ---------------------------------------------------------
    List<ItemMaestro> listarPresentaciones() throws SQLException;

    // ---------------------------------------------------------
    // VALIDACIONES
    // ---------------------------------------------------------
    boolean existeDescripcion(String descripcion) throws SQLException;

    // ---------------------------------------------------------
    // CRUD
    // ---------------------------------------------------------
    void insertar(Medicamento m) throws SQLException;

    void actualizar(Medicamento m) throws SQLException;

    // ---------------------------------------------------------
    // BÚSQUEDAS
    // ---------------------------------------------------------
    List<String> buscarDescripcionLike(String filtro) throws SQLException;

    Medicamento buscarPorDescripcionExacta(String descripcion) throws SQLException;

    // NECESARIO PARA FARMACIA
    Medicamento buscarPorUUID(String uuid) throws SQLException;
}
