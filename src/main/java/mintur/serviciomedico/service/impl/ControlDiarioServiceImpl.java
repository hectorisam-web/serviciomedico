package mintur.serviciomedico.service.impl;

import mintur.serviciomedico.dao.ControlDiarioDao;
import mintur.serviciomedico.model.ControlDiario;
import mintur.serviciomedico.service.BitacoraService;
import mintur.serviciomedico.service.ControlDiarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ControlDiarioServiceImpl implements ControlDiarioService {

    private final ControlDiarioDao controlDao;
    private final BitacoraService bitacoraService;

    @Autowired
    public ControlDiarioServiceImpl(ControlDiarioDao controlDao, BitacoraService bitacoraService) {
        this.controlDao = controlDao;
        this.bitacoraService = bitacoraService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void guardar(ControlDiario c, String ip) throws Exception {
        // --- AQUÍ ESTÁ LA CORRECCIÓN ---
        // Obtenemos el usuario del contexto de seguridad dentro del método
        System.out.println("DEBUG: Recibiendo control para Titular: " + c.getUuidTitular());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            // Asegúrate de que tu sistema de seguridad guarda el UUID en el nombre (name) del usuario
            c.setUsuarioRegistro(UUID.fromString(auth.getName()));
        } else {
            throw new Exception("Usuario no autenticado");
        }
        // -------------------------------

        if (c.getUuidControl() == null || c.getUuidControl().isBlank()) {
            c.setUuidControl(UUID.randomUUID().toString());
        }
        
        controlDao.insertar(c);

        bitacoraService.registrar(
            c.getUsuarioRegistro(),
            "CONTROL_DIARIO",
            "INSERT",
            "",
            c.toString(),
            ip,
            "Ingreso de paciente: " + c.getUuidControl(),
            UUID.fromString(c.getUuidControl()),
            "CONTROL_DIARIO"
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizarSignosVitales(ControlDiario c, String ip) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) c.setUsuarioRegistro(UUID.fromString(auth.getName()));

        ControlDiario anterior = controlDao.buscarPorUUID(c.getUuidControl());
        if (anterior == null) throw new Exception("No se encontró el registro de control.");

        controlDao.actualizarSignosVitales(c);

        bitacoraService.registrar(
            c.getUsuarioRegistro(),
            "CONTROL_DIARIO",
            "UPDATE_SIGNOS",
            anterior.toString(),
            c.toString(),
            ip,
            "Actualización de signos vitales para: " + c.getUuidControl(),
            UUID.fromString(c.getUuidControl()),
            "CONTROL_DIARIO"
        );
    }

    // --- Métodos restantes siguen igual ---
    @Override public ControlDiario buscarPorUUID(String uuid) throws Exception { return controlDao.buscarPorUUID(uuid); }
    @Override public List<ControlDiario> listar() throws Exception { return controlDao.listar(); }
    @Override public List<ControlDiario> listarPorTitular(String uuidTitular) throws Exception { return controlDao.listarPorTitular(uuidTitular); }
    @Override public List<ControlDiario> listarPorFecha(LocalDate fecha) throws Exception { return controlDao.listarPorFecha(fecha); }
    @Override public String getUuidConsultorio() { return controlDao.getUuidConsultorio(); }
    @Override public void setUuidConsultorio(String uuidConsultorio) { controlDao.setUuidConsultorio(uuidConsultorio); }
}