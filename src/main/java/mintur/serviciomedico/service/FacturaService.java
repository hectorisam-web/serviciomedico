package mintur.serviciomedico.service;

import mintur.serviciomedico.model.Factura;
import java.util.List;
import java.util.UUID;

public interface FacturaService {

    void registrarFactura(Factura factura) throws Exception;

    Factura buscarPorUuid(UUID uuidFactura) throws Exception;

    Factura buscarPorNumero(String numeroFactura) throws Exception;

    List<Factura> listarTodas() throws Exception;
}
