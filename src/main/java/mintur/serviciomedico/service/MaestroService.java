package mintur.serviciomedico.service;

import mintur.serviciomedico.model.ItemMaestro;
import java.util.List;

public interface MaestroService {

    // ============================================================
    // MÉTODOS ORIGINALES (SE MANTIENEN INTACTOS)
    // ============================================================

    ItemMaestro buscarCategoriaRaiz(String descripcionRaiz) throws Exception;

    List<ItemMaestro> listarHijos(String uuidMaestro) throws Exception;

    List<ItemMaestro> listarHijosPorDescripcionRaiz(String descripcionRaiz) throws Exception;

    List<ItemMaestro> listarPresentaciones() throws Exception;

    ItemMaestro buscarPorUUID(String uuid) throws Exception;

    void insertar(ItemMaestro item) throws Exception;

    void actualizar(ItemMaestro item) throws Exception;

    void eliminar(String uuid) throws Exception;

    List<ItemMaestro> listarPadres() throws Exception;

    int obtenerSiguienteOrdinal(String uuidPadre) throws Exception;

    // ============================================================
    // MÉTODOS EXTENDIDOS PARA OPERACIÓN UNIFICADA Y BITÁCORA
    // ============================================================

    void guardar(ItemMaestro item, String ipCliente) throws Exception;

    void eliminar(String uuid, String ipCliente) throws Exception;
}