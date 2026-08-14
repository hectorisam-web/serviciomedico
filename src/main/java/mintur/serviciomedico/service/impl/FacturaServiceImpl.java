package mintur.serviciomedico.service.impl;

import mintur.serviciomedico.dao.FacturaDao;
import mintur.serviciomedico.model.Factura;
import mintur.serviciomedico.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FacturaServiceImpl implements FacturaService {

    private final FacturaDao facturaDao;

    @Autowired
    public FacturaServiceImpl(FacturaDao facturaDao) {
        this.facturaDao = facturaDao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registrarFactura(Factura factura) throws Exception {
        // En una aplicación web, aquí podrías añadir validaciones adicionales,
        // como verificar si el número de factura ya existe o si el monto es positivo.
        facturaDao.guardar(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public Factura buscarPorUuid(UUID uuidFactura) throws Exception {
        if (uuidFactura == null) {
            throw new Exception("El identificador de la factura no puede ser nulo.");
        }
        return facturaDao.buscarPorId(uuidFactura);
    }

    @Override
    @Transactional(readOnly = true)
    public Factura buscarPorNumero(String numeroFactura) throws Exception {
        if (numeroFactura == null || numeroFactura.isBlank()) {
            throw new Exception("El número de factura es obligatorio para la búsqueda.");
        }
        return facturaDao.buscarPorNumero(numeroFactura);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> listarTodas() throws Exception {
        return facturaDao.listarTodas();
    }
}