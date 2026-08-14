package mintur.serviciomedico.dao;

import mintur.serviciomedico.model.Factura;
import java.util.List;
import java.util.UUID;

public interface FacturaDao {

    void guardar(Factura factura);

    List<Factura> listarTodas();

    Factura buscarPorId(UUID uuid);

    void actualizar(Factura factura);

    void eliminar(UUID uuid);

    // Métodos que tu servicio necesita:
    Factura buscarPorNumero(String numeroFactura);

    // Este es equivalente a guardar(), pero tu servicio lo usa:
    default void registrarFactura(Factura factura) {
        guardar(factura);
    }

    // Alias para mantener compatibilidad con tu servicio:
    default Factura buscarPorUuid(UUID uuid) {
        return buscarPorId(uuid);
    }
}
