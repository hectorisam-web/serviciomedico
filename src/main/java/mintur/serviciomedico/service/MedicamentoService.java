package mintur.serviciomedico.service;

import mintur.serviciomedico.model.ItemMaestro;
import mintur.serviciomedico.model.Medicamento;
import mintur.serviciomedico.model.LoteMedicamento;

import java.util.List;

public interface MedicamentoService {

    // CRUD PRINCIPAL
    List<Medicamento> listar() throws Exception; // Sincronizado con DAO

    Medicamento buscarPorUUID(String uuid) throws Exception; // Unificado

    // Agregamos String ip para la bitácora
    void guardar(Medicamento m, String ip) throws Exception;

    // CATÁLOGOS
    List<ItemMaestro> listarPresentaciones() throws Exception;

    // BÚSQUEDAS
    List<String> buscarDescripcionLike(String filtro) throws Exception;
    Medicamento buscarPorDescripcionExacta(String descripcion) throws Exception;

    // FARMACIA (Basado en tu LoteMedicamento)
    List<LoteMedicamento> listarLotesDisponibles(String uuidMedicamento) throws Exception;
    
    // Agregamos String ip
    void actualizarStockLote(String uuidLote, int cantidad, String ip) throws Exception;

    boolean existeEntrega(String uuidControl, String uuidMedicamento) throws Exception;
}