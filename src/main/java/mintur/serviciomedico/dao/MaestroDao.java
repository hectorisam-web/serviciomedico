package mintur.serviciomedico.dao;

import mintur.serviciomedico.model.ItemMaestro;
import java.sql.SQLException;
import java.util.List;

public interface MaestroDao {

    // ============================================================
    // MÉTODOS EXISTENTES (NO SE TOCAN)
    // ============================================================

    // 1. Buscar categoría raíz (maestro = 0)
    ItemMaestro buscarCategoriaRaiz(String descripcionRaiz) throws SQLException;

    // 2. Listar hijos por uuid_maestro
    List<ItemMaestro> listarHijos(String uuidMaestro) throws SQLException;

    // 3. Listar hijos directamente por descripción de la raíz
    List<ItemMaestro> listarHijosPorDescripcionRaiz(String descripcionRaiz) throws SQLException;

    // 4. Presentaciones (si las usas aparte)
    List<ItemMaestro> listarPresentaciones() throws Exception;

    // 5. Buscar por UUID
    ItemMaestro buscarPorUUID(String uuid) throws SQLException;


    // ============================================================
    // NUEVOS MÉTODOS PARA FRMPERMISOS (ADMINISTRACIÓN DE JERARQUÍA)
    // ============================================================

    // 6. Insertar un nuevo nodo (menú, submenú o acción)
    void insertar(ItemMaestro item) throws SQLException;

    // 7. Actualizar un nodo existente
    void actualizar(ItemMaestro item) throws SQLException;

    // 8. Eliminar un nodo por UUID
    void eliminar(String uuid) throws SQLException;

    // 9. Listar nodos raíz (maestro = 0)
    List<ItemMaestro> listarPadres() throws SQLException;

    // 10. Obtener el siguiente ordinal disponible para un padre
    int obtenerSiguienteOrdinal(String uuidPadre) throws SQLException;
}
