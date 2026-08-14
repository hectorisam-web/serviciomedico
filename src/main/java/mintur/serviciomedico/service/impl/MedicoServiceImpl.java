package mintur.serviciomedico.service.impl;

import mintur.serviciomedico.dao.MedicoDao;
import mintur.serviciomedico.model.Medico;
import mintur.serviciomedico.service.MedicoService;
import mintur.serviciomedico.service.BitacoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MedicoServiceImpl implements MedicoService {

    private final MedicoDao medicoDao;
    private final BitacoraService bitacoraService;

    @Autowired
    public MedicoServiceImpl(MedicoDao medicoDao, BitacoraService bitacoraService) {
        this.medicoDao = medicoDao;
        this.bitacoraService = bitacoraService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void guardar(Medico m, UUID operadorId, String ip) throws Exception {
        // 1. Validaciones de negocio
        validarCampos(m);
        
        if (m.getUuidMedico() == null || m.getUuidMedico().isBlank()) {
            // --- REGISTRO NUEVO ---
            if (medicoDao.buscarPorCedula(m.getCedulaMedico()) != null) {
                throw new Exception("Ya existe un médico registrado con la cédula " + m.getCedulaMedico());
            }
            
            // Insertamos primero para que la DB genere el UUID si es necesario
            medicoDao.insertar(m);
            
            // Auditoría: Inserción (9 parámetros)
            bitacoraService.registrar(
                operadorId, 
                "MEDICOS", 
                "INSERT", 
                null, 
                "C.I: " + m.getCedulaMedico() + " | MPPS: " + m.getMpps(), 
                ip, 
                "Nuevo ingreso de personal médico: " + m.getNombresMedico() + " " + m.getApellidosMedico(),
                m.getUuidMedico() != null ? UUID.fromString(m.getUuidMedico()) : null,
                "MEDICOS"
            );
        } else {
            // --- ACTUALIZACIÓN ---
            Medico anterior = medicoDao.buscarPorUuid(m.getUuidMedico());
            medicoDao.actualizar(m);
            
            // Auditoría: Actualización (9 parámetros)
            bitacoraService.registrar(
                operadorId, 
                "MEDICOS", 
                "UPDATE", 
                "Estatus anterior: " + (anterior != null ? anterior.isStatus() : "N/A"), 
                "Estatus nuevo: " + m.isStatus(), 
                ip, 
                "Cambio de datos en ficha médica",
                UUID.fromString(m.getUuidMedico()), // Conversión de String a UUID
                "MEDICOS"
            );
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminar(String uuid, UUID operadorId, String ip) throws Exception {
        if (uuid == null || uuid.isBlank()) {
            throw new Exception("UUID no válido para eliminación.");
        }

        Medico medico = medicoDao.buscarPorUuid(uuid);
        if (medico == null) {
            throw new Exception("El médico no existe en el sistema.");
        }

        // REGLA DE ORO MINTUR: Si tiene historial, deshabilitar. Si no, eliminar físicamente.
        if (medicoDao.tieneConsultas(uuid)) {
            medico.setStatus(false);
            medicoDao.actualizar(medico);
            
            // Auditoría: Deshabilitación (9 parámetros)
            bitacoraService.registrar(
                operadorId, 
                "MEDICOS", 
                "LOGIC_DELETE", 
                "Estatus: Activo", 
                "Estatus: Inactivo", 
                ip, 
                "Inhabilitado por histórico en Control Diario (C.I: " + medico.getCedulaMedico() + ")",
                UUID.fromString(medico.getUuidMedico()),
                "MEDICOS"
            );
                
            throw new Exception("INFO_DESHABILITADO: El médico posee historial en Control Diario. Se ha procedido a deshabilitar su estatus.");
        } else {
            medicoDao.eliminar(uuid);
            
            // Auditoría: Eliminación Física (9 parámetros)
            bitacoraService.registrar(
                operadorId, 
                "MEDICOS", 
                "DELETE", 
                "Nombre: " + medico.getNombresMedico() + " " + medico.getApellidosMedico(), 
                null, 
                ip, 
                "Eliminación permanente de registro médico (C.I: " + medico.getCedulaMedico() + ")",
                UUID.fromString(uuid),
                "MEDICOS"
            );
        }
    }

    private void validarCampos(Medico m) throws Exception {
        if (m.getNombresMedico() == null || m.getNombresMedico().isBlank()) throw new Exception("El nombre es obligatorio.");
        if (m.getApellidosMedico() == null || m.getApellidosMedico().isBlank()) throw new Exception("El apellido es obligatorio.");
        if (m.getMpps() == null || m.getMpps().isBlank()) throw new Exception("El número de MPPS es requerido.");
        if (m.getCedulaMedico() <= 0) throw new Exception("La cédula no es válida.");
    }

    @Override @Transactional(readOnly = true)
    public List<Medico> listarTodos() throws Exception { return medicoDao.listarTodos(); }

    @Override @Transactional(readOnly = true)
    public List<Medico> listarActivos() throws Exception { return medicoDao.listarActivos(); }

    @Override @Transactional(readOnly = true)
    public Medico buscarPorUuid(String uuid) throws Exception { return medicoDao.buscarPorUuid(uuid); }

    @Override @Transactional(readOnly = true)
    public Medico buscarPorCedula(int cedula) throws Exception { return medicoDao.buscarPorCedula(cedula); }
}